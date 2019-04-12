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

    @Test
    fun testToHexString() {
        assertEquals("#fefdff", Color.from("#fefdff").toHexString())
    }

    @Test
    fun testWithSaturation() {
        val red = Color.from("#ff0000")
        assertEquals(red, red.withSaturation(1f))
        assertEquals(Color.from("#ff7f7f"), red.withSaturation(.5f))
    }

    @Test
    fun testFade() {
        assertEquals(Color.RED, Color.RED.fade(Color.GREEN, 0f))
        assertEquals(Color.GREEN, Color.RED.fade(Color.GREEN, 1f))
        assertEquals(Color.from("#7f7f00"), Color.RED.fade(Color.GREEN, 0.5f))
    }
}
