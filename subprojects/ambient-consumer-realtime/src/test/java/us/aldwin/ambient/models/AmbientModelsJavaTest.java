package us.aldwin.ambient.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import us.aldwin.ambient.models.AmbientRealtimeDevice.DeviceInfo;
import us.aldwin.ambient.models.AmbientRealtimeDevice.DeviceInfo.InfoCoords;
import us.aldwin.ambient.models.AmbientRealtimeDevice.DeviceInfo.InfoCoords.Geo;
import us.aldwin.ambient.models.AmbientRealtimeDevice.DeviceInfo.InfoCoords.LatLon;

public class AmbientModelsJavaTest {
  // TODO @NJA: replace with a capture of real data
  @Test
  public void itRoundTrips() {
    Map<String, Object> dataValues = new HashMap<>();
    dataValues.put("string", "xyz");
    List<Double> coords = new ArrayList<>();
    coords.add(1.0);
    coords.add(2.0);

    AmbientRealtimeDevice device = new AmbientRealtimeDevice(
        "mac",
        "apiKey",
        new AmbientRealtimeData(
            "date",
            "tz",
            123,
            "mac",
            "deviceId",
            dataValues
        ),
        new DeviceInfo(
            "name",
            new InfoCoords(
                1.0,
                new Geo(coords, "type"),
                "address",
                "location",
                new LatLon(
                    1.0,
                    2.0
                )
            )
        )
    );

    assertEquals(device, AmbientRealtimeDevice.fromJson(device.toJson()));
  }
}
