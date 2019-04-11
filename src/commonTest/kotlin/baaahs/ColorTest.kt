package baaahs;

import kotlin.test.Test
import kotlin.test.assertEquals

public class ColorTest {
    @Test
    fun testFromInt() {
        val white = Color.from(0xfefdff)
        assertEquals(listOf(254, 253, 255), listOf(white.red, white.green, white.blue))
    }

    @Test
    fun testFromString() {
        val white = Color.from("#fefdff")
        assertEquals(listOf(254, 253, 255), listOf(white.red, white.green, white.blue))
    }

    @Test
    fun testDistanceTo() {
        assertEquals(1f, Color.WHITE.distanceTo(Color.BLACK))
        assertEquals(0f, Color.WHITE.distanceTo(Color.WHITE))
    }
}
