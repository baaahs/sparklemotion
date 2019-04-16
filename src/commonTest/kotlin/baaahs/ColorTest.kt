package baaahs;

import kotlin.test.Test
import kotlin.test.assertEquals

public class ColorTest {
    @Test
    fun testBounds() {
        Color.WHITE
        assertEquals(Color(255, 255, 0, 255),
            Color(266, 1024, -17, 350))

        assertEquals(Color(255, 255, 0, 255),
            Color(26.6f, 10.24f, -17f, 350f))

        assertEquals(Color(1f, 1f, 0f, 1f),
            Color(26.6f, 10.24f, -17f, 350f))
    }

    @Test
    fun testFromInt() {
        val white = Color.from(0xfefdff)
        assertEquals(listOf(254, 253, 255), listOf(white.redI, white.greenI, white.blueI))
    }

    @Test
    fun testFromString() {
        val white = Color.from("#fefdff")
        assertEquals(listOf(254, 253, 255), listOf(white.redI, white.greenI, white.blueI))
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
    fun testPlus() {
        assertEquals(Color.YELLOW, Color.RED.plus(Color.GREEN))
        assertEquals(Color.WHITE, Color.YELLOW.plus(Color.BLUE))
    }

    @Test
    fun testFade() {
        assertEquals(Color.RED, Color.RED.fade(Color.GREEN, 0f))
        assertEquals(Color.GREEN, Color.RED.fade(Color.GREEN, 1f))
        assertEquals(Color.from("#7f7f00"), Color.RED.fade(Color.GREEN, 0.5f))
    }
}
