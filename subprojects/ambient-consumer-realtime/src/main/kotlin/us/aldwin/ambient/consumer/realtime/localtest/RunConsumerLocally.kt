package us.aldwin.ambient.consumer.realtime.localtest

import us.aldwin.ambient.consumer.realtime.AmbientCredentials
import us.aldwin.ambient.consumer.realtime.AmbientRealtimeConsumer

/**
 * Intended for local testing
 */
private fun main() {
    val applicationKey = System.getenv("AMBIENT_APP_KEY")?.trim()!!
    val apiKey = System.getenv("AMBIENT_API_KEY")?.trim()!!

    val consumer =
        AmbientRealtimeConsumer(
            AmbientCredentials(applicationKey = applicationKey, apiKeys = setOf(apiKey)),
        )

    consumer.consume(
        processSubscribe = {
            println("Subscribe: $it")
        },
        processData = {
            println("Data: $it")
        },
    )
}
