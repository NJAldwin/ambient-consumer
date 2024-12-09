package us.aldwin.ambient.models

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.readValue
import us.aldwin.ambient.json.AmbientJson.mapper
import java.time.Instant

/**
 * Represents the data that is sent by the Ambient Realtime API.
 *
 * Note that for `data`, `deviceId` does not appear to be populated, but `macAddress` is, whereas
 * for `subscribed`, `macAddress` does not appear to be populated, but `deviceId` is.
 *
 * `values` contains all the data passed along (e.g. `temp1f`, `pm25`, `humidityin`, etc) depending on the reporting sensors
 */
public data class AmbientRealtimeData(
    /**
     * TODO: doc
     */
    @get:JsonProperty(required = true) @field:JsonProperty(required = true)
    val date: String,
    /**
     * TODO: doc
     */
    @get:JsonProperty(required = true) @field:JsonProperty(required = true)
    val tz: String,
    /**
     * TODO: doc
     */
    @get:JsonProperty(required = true) @field:JsonProperty(required = true)
    val dateutc: Long,
    /**
     * TODO: doc
     */
    @get:JsonProperty(required = false) @field:JsonProperty(required = false)
    val macAddress: String?,
    /**
     * TODO: doc
     */
    @get:JsonProperty(required = false) @field:JsonProperty("deviceId", required = false)
    val deviceId: String?,
    /**
     * TODO: doc
     */
    @get:JsonAnyGetter @field:JsonAnySetter
    val values: MutableMap<String, Any?> = mutableMapOf(),
) {
    /**
     * TODO: doc
     */
    @get:JsonIgnore
    @delegate:JsonIgnore
    val timestamp: Instant by lazy { Instant.ofEpochMilli(dateutc) }

    /**
     * TODO: doc
     */
    public fun toJson(): String = mapper.writeValueAsString(this)

    /**
     * TODO: doc
     */
    public companion object {
        /**
         * TODO: doc
         */
        @JvmStatic
        public fun fromJson(json: String): AmbientRealtimeData = mapper.readValue<AmbientRealtimeData>(json)
    }
}

/**
 * Subscribe event from the Ambient Realtime API.
 *
 * Note that this event fires for unsubscribes too; check the `method` to be sure.
 */
public data class AmbientRealtimeSubscribe(
    /**
     * Appears to be either `subscribe` or `unsubscribe` but hasn't been made an enum just in case of other values.
     */
    @get:JsonProperty(required = true) @field:JsonProperty(required = true)
    val method: String,
    /**
     * TODO: doc
     */
    @get:JsonProperty(required = true) @field:JsonProperty(required = true)
    val devices: List<AmbientRealtimeDevice>,
) {
    /**
     * TODO: doc
     */
    public fun toJson(): String = mapper.writeValueAsString(this)

    /**
     * TODO: doc
     */
    public companion object {
        /**
         * TODO: doc
         */
        @JvmStatic
        public fun fromJson(json: String): AmbientRealtimeSubscribe = mapper.readValue<AmbientRealtimeSubscribe>(json)
    }
}

/**
 * TODO: doc
 */
public data class AmbientRealtimeDevice(
    /**
     * TODO: doc
     */
    @get:JsonProperty(required = true) @field:JsonProperty(required = true)
    val macAddress: String,
    /**
     * TODO: doc
     */
    @get:JsonProperty(required = true) @field:JsonProperty(required = true)
    val apiKey: String,
    /**
     * TODO: doc
     */
    @get:JsonProperty(required = true) @field:JsonProperty(required = true)
    val lastData: AmbientRealtimeData,
    /**
     * TODO: doc
     */
    @get:JsonProperty(required = true) @field:JsonProperty(required = true)
    val info: DeviceInfo,
) {
    /**
     * TODO: doc
     */
    public data class DeviceInfo(
        /**
         * TODO: doc
         */
        @get:JsonProperty(required = true) @field:JsonProperty(required = true)
        val name: String,
        /**
         * TODO: doc
         */
        @get:JsonProperty(required = true) @field:JsonProperty(required = true)
        val coords: InfoCoords,
    ) {
        /**
         * TODO: doc
         */
        public data class InfoCoords(
            /**
             * TODO: doc
             */
            @get:JsonProperty(required = true) @field:JsonProperty(required = true)
            val elevation: Double,
            /**
             * TODO: doc
             */
            @get:JsonProperty(required = true) @field:JsonProperty(required = true)
            val geo: Geo,
            /**
             * TODO: doc
             */
            @get:JsonProperty(required = true) @field:JsonProperty(required = true)
            val address: String,
            /**
             * TODO: doc
             */
            @get:JsonProperty(required = true) @field:JsonProperty(required = true)
            val location: String,
            /**
             * TODO: doc
             */
            @get:JsonProperty(required = true) @field:JsonProperty(required = true)
            val coords: LatLon,
        ) {
            /**
             * TODO: doc
             */
            public data class LatLon(
                /**
                 * TODO: doc
                 */
                @get:JsonProperty(required = true) @field:JsonProperty(required = true)
                val lat: Double,
                /**
                 * TODO: doc
                 */
                @get:JsonProperty(required = true) @field:JsonProperty(required = true)
                val lon: Double,
            )

            /**
             * TODO: doc
             */
            public data class Geo(
                /**
                 * TODO: doc
                 */
                @get:JsonProperty(required = true) @field:JsonProperty(required = true)
                val coordinates: List<Double>,
                /**
                 * TODO: doc
                 */
                @get:JsonProperty(required = true) @field:JsonProperty(required = true)
                val type: String,
            )
        }
    }

    /**
     * TODO: doc
     */
    public fun toJson(): String = mapper.writeValueAsString(this)

    /**
     * TODO: doc
     */
    public companion object {
        /**
         * TODO: doc
         */
        @JvmStatic
        public fun fromJson(json: String): AmbientRealtimeDevice = mapper.readValue<AmbientRealtimeDevice>(json)
    }
}
