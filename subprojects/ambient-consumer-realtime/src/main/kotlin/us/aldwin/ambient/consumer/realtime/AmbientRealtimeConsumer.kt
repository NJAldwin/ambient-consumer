package us.aldwin.ambient.consumer.realtime

import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import us.aldwin.ambient.models.AmbientRealtimeData
import us.aldwin.ambient.models.AmbientRealtimeSubscribe
import java.net.URI
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference
import kotlin.system.measureTimeMillis

// TODO @NJA: tests
// TODO @NJA: suspend funs to support coroutines? (unsure if this will work correctly with the underlying library)

/**
 * (TODO: doc)
 */
public class AmbientRealtimeConsumer
    @JvmOverloads
    constructor(
        private val creds: AmbientCredentials,
        private val connectionTimeout: Duration = Duration.ofMinutes(2),
        private val noDataTimeout: Duration = Duration.ofMinutes(2),
        private val clockSupplier: () -> Clock = { Clock.systemUTC() },
    ) {
        private val log: Logger = LoggerFactory.getLogger(AmbientRealtimeConsumer::class.java)

        private val uriWithKey: URI = URI.create("https://rt2.ambientweather.net/?api=1&applicationKey=${creds.applicationKey}")

        private fun newSocket(): Socket? {
            val ambientUri: URI = uriWithKey
            val options: IO.Options =
                IO.Options.builder()
                    .setTransports(arrayOf("websocket"))
                    // I think attempting to reuse the same Manager causes issues sometimes
                    .setForceNew(true)
                    .build()
            return IO.socket(ambientUri, options)
        }

        /**
         * Subscribe to the real-time API and loop endlessly, calling the provided handlers when events happen.
         *
         * If using multiple devices, key them based off of the MAC address from the subscribe event; device ID is not populated for data events.
         *
         * @param processSubscribe is called on every subscribe or unsubscribe event
         * @param processData is called for every data event AND for each device's `lastData` in every subscribe/unsubscribe event (after `processSubscribe` is called)
         * @param installShutdownHandler uses Runtime.getRuntime().addShutdownHook to ensure the consumer is unsubscribed on shutdown; set to `false` to handle terminating the consumer separately (i.e. with `shouldStayConnected`)
         */
        @JvmOverloads
        public fun consume(
            installShutdownHandler: Boolean = true,
            shouldStayConnected: () -> Boolean = { true },
            processSubscribe: (AmbientRealtimeSubscribe) -> Unit = {},
            processData: (AmbientRealtimeData) -> Unit,
        ) {
            val lastConnection: AtomicReference<Instant?> = AtomicReference(null)
            val lastTimestamp: AtomicReference<Instant?> = AtomicReference(null)
            val lastReceived: AtomicReference<Instant?> = AtomicReference(null)

            fun handleData(
                data: AmbientRealtimeData,
            ) {
                if (data.values.isNotEmpty()) {
                    data.values.toSortedMap().forEach { (k, v) ->
                        when (v) {
                            // have only ever observed these two types
                            is String, is Number -> {}
                            null -> log.info("Null value in data: $k")
                            else -> log.warn("Unknown value type ${v.javaClass} in data: $k=$v")
                        }
                    }
                }
                val now = clockSupplier().instant()
                val prevTs = lastTimestamp.getAndSet(data.timestamp)
                val prevRcv = lastReceived.getAndSet(now)
                val ttP = Duration.ofMillis(measureTimeMillis { processData(data) })
                log.info("Processed data in $ttP")
                log.info("Data received within ${Duration.between(data.timestamp, now)}")
                if (prevTs != null) {
                    log.info("Delta from last data: ${Duration.between(prevTs, data.timestamp)}")
                }
                if (prevRcv != null) {
                    log.info("Delta from last rcv: ${Duration.between(prevRcv, now)}")
                }
                log.info("Connected for ${lastConnection.get()?.let { Duration.between(it, now) } ?: "???"}")
            }

            fun socketLoop(): FinishResult {
                val socket = newSocket() ?: return FinishResult.NO_SOCKET
                socket.onAnyIncoming { args ->
                    log.debug(" RCV {}", args)
                    args.map { log.debug("<RCV {}", it) }
                }
                socket.onAnyOutgoing { args ->
                    log.debug("SND  {}", args)
                    args.map { log.debug("SND> {}", it) }
                }
                socket.on("subscribed") { args ->
                    // note: on unsubscribe, this event also fires
                    val sub = (args[0] as? JSONObject)?.toString()?.let { AmbientRealtimeSubscribe.fromJson(it) }
                    log.info("SUB  $sub")
                    sub?.let(processSubscribe)
                    sub?.devices?.forEach { device ->
                        // for some reason, subscribe/unsubscribe events' lastData usually only has device ID, no MAC address
                        // may as well hydrate it here, so that processData can dispatch appropriately if necessary
                        handleData(device.lastData.copy(macAddress = device.lastData.macAddress ?: device.macAddress))
                    }
                }
                socket.on("data") { args ->
                    val data = (args[0] as? JSONObject)?.toString()?.let { AmbientRealtimeData.fromJson(it) }
                    log.info("DATA $data")
                    data?.let { handleData(it) }
                }

                log.info("Connecting $socket")
                val startConnecting = clockSupplier().instant()
                socket.connect()
                while (!socket.connected()) {
                    val notConnectedDuration = Duration.between(startConnecting, clockSupplier().instant())
                    log.warn("Not yet connected after $notConnectedDuration ...")
                    if (notConnectedDuration > connectionTimeout) {
                        // it seems to sometimes get stuck in this spot if it is disconnected (NOT_CONNECTED) and can't reconnect
                        log.error("Still not connected after over $connectionTimeout ($notConnectedDuration); giving up.")
                        // just in case
                        socket.disconnect()
                        Thread.sleep(100)
                        return FinishResult.TIMED_OUT_CONNECTING
                    }
                    Thread.sleep(500)
                }
                lastConnection.set(clockSupplier().instant())

                log.info("Subscribing $socket")
                socket.emit(
                    "subscribe",
                    mapOf("apiKeys" to creds.apiKeys),
                )

                fun unsubscribeDisconnect() {
                    if (socket.connected()) {
                        log.info("Unsubscribing $socket")
                        socket.emit(
                            "unsubscribe",
                            mapOf("apiKeys" to creds.apiKeys),
                        )
                        Thread.sleep(400)
                    }
                    log.info("Disconnecting $socket")
                    socket.disconnect()
                    Thread.sleep(100)
                }

                val shutdownHook =
                    object : Thread() {
                        override fun run() {
                            log.debug("Shutdown detected")
                            unsubscribeDisconnect()
                        }
                    }

                if (installShutdownHandler) {
                    Runtime.getRuntime().addShutdownHook(shutdownHook)
                }

                fun removeShutdownHook() {
                    if (installShutdownHandler) {
                        try {
                            Runtime.getRuntime().removeShutdownHook(shutdownHook)
                        } catch (e: Exception) {
                            log.error("Unable to remove shutdown hook", e)
                        }
                    }
                }

                while (socket.connected()) {
                    val lr = lastReceived.get()
                    if (lr != null && Duration.between(lr, clockSupplier().instant()) > noDataTimeout) {
                        val lc = lastConnection.get()
                        if (lc != null && Duration.between(lc, clockSupplier().instant()) > noDataTimeout) {
                            log.info("Haven't received data in over $noDataTimeout, and last connection was over $noDataTimeout ago... last rcv ${lastReceived.get()} last ts ${lastTimestamp.get()} last conn ${lastConnection.get()}")
                            unsubscribeDisconnect()
                            removeShutdownHook()
                            return FinishResult.IDLED
                        } else {
                            log.info("Haven't received data in over $noDataTimeout... last rcv ${lastReceived.get()} last ts ${lastTimestamp.get()} last conn ${lastConnection.get()}")
                        }
                    }
                    if (!shouldStayConnected()) {
                        log.info("Interrupting connection")
                        unsubscribeDisconnect()
                        removeShutdownHook()
                        return FinishResult.INTERRUPTED
                    }
                    Thread.sleep(500)
                }

                log.info("Socket no longer connected")
                // idk why we would end up here without being disconnected, but it seems to happen sometimes
                unsubscribeDisconnect()
                removeShutdownHook()
                return FinishResult.NOT_CONNECTED
            }

            do {
                val res = socketLoop()
                log.info("Socket loop finished: $res")
            } while (res != FinishResult.INTERRUPTED)
            log.info("Shutdown")
        }
    }

/**
 * Credentials for connecting to Ambient Weather.
 */
public data class AmbientCredentials(
    /**
     * The application key for this connection to Ambient Weather.
     */
    val applicationKey: String,
    /**
     * The API keys for the Ambient Weather devices to consume for.
     */
    val apiKeys: Set<String>,
)

/**
 * The status of a consumer after it has finished.
 */
public enum class FinishResult {
    /** Didn't receive any data within configured noDataTimeout **/
    IDLED,

    /** Interrupted somehow **/
    INTERRUPTED,

    /** Somehow the socket became not connected **/
    NOT_CONNECTED,

    /** Couldn't set up a new socket **/
    NO_SOCKET,

    /** Wasn't able to connect to socket within configured connectionTimeout **/
    TIMED_OUT_CONNECTING,
}
