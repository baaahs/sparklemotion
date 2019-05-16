package baaahs;

import kotlin.test.Test
import kotlin.test.assertEquals

public class ColorTest {
    @Test
    fun testBounds() {
        Colors.WHITE
        assertEquals(ColorFrom(255, 255, 0, 255),
            ColorFrom(266, 1024, -17, 350))

        assertEquals(ColorFrom(255, 255, 0, 255),
            ColorFrom(26.6f, 10.24f, -17f, 350f))

        assertEquals(ColorFrom(1f, 1f, 0f, 1f),
            ColorFrom(26.6f, 10.24f, -17f, 350f))
    }

    @Test
    fun testFromInt() {
        val white = Colors.from(0xfefdff)
        assertEquals(listOf(254, 253, 255), listOf(white.redI, white.greenI, white.blueI))
    }

    @Test
    fun testFromString() {
        val white = Colors.from("#fefdff")
        assertEquals(listOf(254, 253, 255), listOf(white.redI, white.greenI, white.blueI))
    }

    @Test
    fun testDistanceTo() {
        assertEquals(1f, Colors.WHITE.distanceTo(Colors.BLACK))
        assertEquals(0f, Colors.WHITE.distanceTo(Colors.WHITE))
    }

    @Test
    fun testToHexString() {
        assertEquals("#fefdff", Colors.from("#fefdff").toHexString())
    }

    @Test
    fun testWithSaturation() {
        val red = Colors.from("#ff0000")
        assertEquals(red, red.withSaturation(1f))
        assertEquals(Colors.from("#ff7f7f"), red.withSaturation(.5f))
    }

    @Test
    fun testPlus() {
        assertEquals(Colors.YELLOW, Colors.RED.plus(Colors.GREEN))
        assertEquals(Colors.WHITE, Colors.YELLOW.plus(Colors.BLUE))
    }

    @Test
    fun testFade() {
        assertEquals(Colors.RED, Colors.RED.fade(Colors.GREEN, 0f))
        assertEquals(Colors.GREEN, Colors.RED.fade(Colors.GREEN, 1f))
        assertEquals(Colors.from("#7f7f00"), Colors.RED.fade(Colors.GREEN, 0.5f))
    }
}
