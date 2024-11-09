package baaahs.shaders

import baaahs.Color
import baaahs.sm.brain.proto.Pixels
import io.kotest.matchers.*
import kotlin.test.Test

class PixelShaderTest {
    private val fixture = fakeFixture(5)
    private val rgbColors = arrayOf(
        Color.from("#111111"),
        Color.from("#333333"),
        Color.from("#777777"),
        Color.from("#cccccc"),
        Color.from("#ffffff")
    )
    private val argbColors = arrayOf(
        Color.from("#ff111111"),
        Color.from("#ff333333"),
        Color.from("#77777777"),
        Color.from("#ffcccccc"),
        Color.from("#00ffffff")
    )

    @Test
    fun forDirect32Bit_shouldTransmitAndRender() {
        val dstBuf = transmit(directBuffer(argbColors, PixelBrainShader.Encoding.DIRECT_ARGB), fixture)
        dstBuf.getColors()
            .shouldBe("#111111,#333333,#77777777,#cccccc,#00ffffff")
        render(dstBuf, fixture).getColors()
            .shouldBe("#111111,#333333,#77777777,#cccccc,#00ffffff")
    }

    @Test
    fun forDirect24Bit_shouldTransmitAndRenderIgnoringAlpha() {
        val dstBuf = transmit(directBuffer(argbColors, PixelBrainShader.Encoding.DIRECT_RGB), fixture)
        dstBuf.getColors()
            .shouldBe("#111111,#333333,#777777,#cccccc,#ffffff")
        render(dstBuf, fixture).getColors()
            .shouldBe("#111111,#333333,#777777,#cccccc,#ffffff")
    }

    @Test
    fun forIndexed2_shouldSetDataBufCorrectly() {
        val dstBuf = indexedBuffer(
            arrayOf(Color.BLACK, Color.YELLOW), arrayOf(0, 0, 0, 0, 0), PixelBrainShader.Encoding.INDEXED_2
        ) as PixelBrainShader.IndexedBuffer
        dstBuf.dataBuf.size.shouldBe(1)
        dstBuf.byte(0).shouldBe(0x00)

        dstBuf[0] = 1
        dstBuf.byte(0).shouldBe(0x80)
        dstBuf[2] = 1
        dstBuf.byte(0).shouldBe(0xA0)
        dstBuf[4] = 1
        dstBuf.byte(0).shouldBe(0xA8)
    }

    @Test
    fun forIndexed4_shouldSetDataBufCorrectly() {
        val dstBuf = indexedBuffer(
            arrayOf(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN),
            arrayOf(0, 0, 0, 0, 0), PixelBrainShader.Encoding.INDEXED_4
        ) as PixelBrainShader.IndexedBuffer

        dstBuf.dataBuf.size.shouldBe(2)
        dstBuf.bytes().shouldBe("00 00")

        dstBuf[0] = 1 // orange
        dstBuf.bytes().shouldBe("40 00")

        dstBuf[2] = 3 // green
        dstBuf.bytes().shouldBe("4c 00")

        dstBuf[4] = 2 // yellow
        dstBuf.bytes().shouldBe("4c 80")

        dstBuf.colors[0].shouldBe(Color.ORANGE)
        dstBuf.colors[2].shouldBe(Color.GREEN)
        dstBuf.colors[4].shouldBe(Color.YELLOW)
    }

    @Test
    fun forIndexed16_shouldSetDataBufCorrectly() {
        val dstBuf = indexedBuffer(
            arrayOf(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE),
            arrayOf(0, 0, 0, 0, 0), PixelBrainShader.Encoding.INDEXED_16
        ) as PixelBrainShader.IndexedBuffer

        dstBuf.dataBuf.size.shouldBe(3)
        dstBuf.bytes().shouldBe("00 00 00")

        dstBuf[0] = 1 // orange
        dstBuf.bytes().shouldBe("10 00 00")

        dstBuf[2] = 3 // green
        dstBuf.bytes().shouldBe("10 30 00")

        dstBuf[4] = 15 // unset, so white
        dstBuf.bytes().shouldBe("10 30 f0")

        dstBuf.colors[0].shouldBe(Color.ORANGE)
        dstBuf.colors[2].shouldBe(Color.GREEN)
        dstBuf.colors[4].shouldBe(Color.WHITE)
    }

    @Test
    fun forIndexed2_shouldTransmitAndRender() {
        val dstBuf = transmit(
            indexedBuffer(
                arrayOf(Color.BLACK, Color.YELLOW),
                arrayOf(0, 1, 0, 1, 0),
                PixelBrainShader.Encoding.INDEXED_2
            ), fixture
        )
        dstBuf.getColors()
            .shouldBe("#000000,#ffff00,#000000,#ffff00,#000000")
        render(dstBuf, fixture).getColors()
            .shouldBe("#000000,#ffff00,#000000,#ffff00,#000000")
    }

    @Test
    fun whenFewerPixels_shouldTruncate() {
        val pixels = render(directBuffer(rgbColors), fakeFixture(3))
        pixels.joinToString(",") { it.toHexString() }
            .shouldBe("#111111,#333333,#777777")
    }

    @Test
    fun whenMorePixels_shouldRepeat() {
        val pixels = render(directBuffer(rgbColors), fakeFixture(12))
        pixels.joinToString(",") { it.toHexString() }
            .shouldBe(
                "#111111,#333333,#777777,#cccccc,#ffffff," +
                "#111111,#333333,#777777,#cccccc,#ffffff," +
                "#111111,#333333"
            )
    }

    private fun directBuffer(
        colors: Array<Color>,
        encoding: PixelBrainShader.Encoding = PixelBrainShader.Encoding.DIRECT_ARGB
    ): PixelBrainShader.Buffer {
        val shader = PixelBrainShader(encoding)
        return shader.createBuffer(fixture.componentCount).apply {
            (0 until 5).forEach { i -> this.colors[i] = colors[i] }
        }
    }

    private fun indexedBuffer(
        palette: Array<Color>,
        colorIndices: Array<Int>,
        encoding: PixelBrainShader.Encoding
    ): PixelBrainShader.Buffer {
        val shader = PixelBrainShader(encoding)
        return shader.createBuffer(fixture.componentCount).apply {
            palette.forEachIndexed { index, color -> this.palette[index] = color }
            colorIndices.forEachIndexed { pixelIndex, colorIndex -> this[pixelIndex] = colorIndex }
        }
    }

    private fun PixelBrainShader.Buffer.getColors() = colors.map { it.toHexString() }.joinToString(",")
    private fun Pixels.getColors() = map { it.toHexString() }.joinToString(",")
    private fun PixelBrainShader.IndexedBuffer.byte(index: Int) = dataBuf[index].toInt() and 0xFF
    private fun PixelBrainShader.IndexedBuffer.bytes() =
        dataBuf.joinToString(" ") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }
}