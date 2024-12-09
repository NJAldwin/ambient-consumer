package us.aldwin.ambient.consumer.realtime

import org.junit.jupiter.api.Test

class AmbientRealtimeConsumerTest {
    @Test
    fun `it instantiates`() {
        AmbientRealtimeConsumer(
            AmbientCredentials(applicationKey = "appKey", apiKeys = setOf("apiKey1", "apiKey2")),
        )
    }
}
