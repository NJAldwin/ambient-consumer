package us.aldwin.ambient.models

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import us.aldwin.ambient.models.AmbientRealtimeDevice.Companion.fromJson
import us.aldwin.ambient.models.AmbientRealtimeDevice.DeviceInfo
import us.aldwin.ambient.models.AmbientRealtimeDevice.DeviceInfo.InfoCoords
import us.aldwin.ambient.models.AmbientRealtimeDevice.DeviceInfo.InfoCoords.Geo
import us.aldwin.ambient.models.AmbientRealtimeDevice.DeviceInfo.InfoCoords.LatLon

class AmbientModelsTest {
    // TODO @NJA: replace with a capture of real data
    @Test
    fun itRoundTrips() {
        val device =
            AmbientRealtimeDevice(
                "mac",
                "apiKey",
                AmbientRealtimeData(
                    "date",
                    "tz",
                    123,
                    "mac",
                    "deviceId",
                    mutableMapOf("string" to "xyz"),
                ),
                DeviceInfo(
                    "name",
                    InfoCoords(
                        1.0,
                        Geo(listOf(1.0, 2.0), "type"),
                        "address",
                        "location",
                        LatLon(
                            1.0,
                            2.0,
                        ),
                    ),
                ),
            )

        Assertions.assertEquals(device, fromJson(device.toJson()))
    }
}
