package baaahs;

import io.kotest.matchers.*
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
    fun testFromRGBString() {
        Color.from("#fefdfc").shouldBe(Color.from(0xfffefdfc.toInt()))

        Color.from("#edc").shouldBe(Color.from(0xffeeddcc.toInt()))
    }

    @Test
    fun testFromARGBString() {
        Color.from("#fffefdfc").shouldBe(Color.from(0xfffefdfc.toInt()))
        Color.from("#f7fefdfc").shouldBe(Color.from(0xf7fefdfc.toInt()))
        Color.from("#00fefdfc").shouldBe(Color.from(0x00fefdfc))

        Color.from("#fedc").shouldBe(Color.from(0xffeeddcc.toInt()))
        Color.from("#7edc").shouldBe(Color.from(0x77eeddcc))
        Color.from("#0edc").shouldBe(Color.from(0x00eeddcc))
    }

    @Test
    fun testFromBytes() {
        val white = Color(0xfe.toByte(), 0xfd.toByte(), 0xff.toByte())
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
        assertEquals("#f7fefdff", Color.from("#f7fefdff").toHexString())
        assertEquals("#01010101", Color.from("#01010101").toHexString())
        assertEquals("#000000", Color.from("#ff000000").toHexString())
        assertEquals("#00000000", Color.from("#00000000").toHexString())
    }

    @Test
    fun testWithSaturation() {
        val red = Color.from("#ff0000")
        assertEquals(red, red.withSaturation(1f))
        assertEquals(Color.from("#ff8080"), red.withSaturation(.5f))
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
