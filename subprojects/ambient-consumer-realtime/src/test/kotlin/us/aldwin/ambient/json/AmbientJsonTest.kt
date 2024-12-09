package us.aldwin.ambient.json

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import us.aldwin.ambient.json.AmbientJson.mapper

class AmbientJsonTest {
    @Test
    fun `it instantiates a mapper`() {
        val mapper = mapper

        val value = mapper.readValue("\"hello\"", String::class.java)
        Assertions.assertEquals("hello", value)
    }
}
