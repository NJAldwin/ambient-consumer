package us.aldwin.ambient.consumer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ToRemoveTest {
    @Test
    fun `it returns the expected information`() {
        assertEquals("Foo", ToRemove.placeholder())
    }
}
