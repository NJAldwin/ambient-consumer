package us.aldwin.ambient.consumer.realtime;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class AmbientRealtimeConsumerJavaTest {
  @Test
  public void itInstantiates() {
    Set<String> apiKeys = new HashSet<String>();
    apiKeys.add("apiKey1");
    apiKeys.add("apiKey2");

    new AmbientRealtimeConsumer(
        new AmbientCredentials("appKey", apiKeys)
    );
  }
}
