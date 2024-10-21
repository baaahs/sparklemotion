package baaahs.shaders

import baaahs.Color
import baaahs.sm.brain.proto.Pixels
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
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
        expect(dstBuf.getColors())
            .toBe("#111111,#333333,#77777777,#cccccc,#00ffffff")
        expect(render(dstBuf, fixture).getColors())
            .toBe("#111111,#333333,#77777777,#cccccc,#00ffffff")
    }

    @Test
    fun forDirect24Bit_shouldTransmitAndRenderIgnoringAlpha() {
        val dstBuf = transmit(directBuffer(argbColors, PixelBrainShader.Encoding.DIRECT_RGB), fixture)
        expect(dstBuf.getColors())
            .toBe("#111111,#333333,#777777,#cccccc,#ffffff")
        expect(render(dstBuf, fixture).getColors())
            .toBe("#111111,#333333,#777777,#cccccc,#ffffff")
    }

    @Test
    fun forIndexed2_shouldSetDataBufCorrectly() {
        val dstBuf = indexedBuffer(
            arrayOf(Color.BLACK, Color.YELLOW), arrayOf(0, 0, 0, 0, 0), PixelBrainShader.Encoding.INDEXED_2
        ) as PixelBrainShader.IndexedBuffer
        expect(dstBuf.dataBuf.size).toBe(1)
        expect(dstBuf.byte(0)).toBe(0x00)

        dstBuf[0] = 1
        expect(dstBuf.byte(0)).toBe(0x80)
        dstBuf[2] = 1
        expect(dstBuf.byte(0)).toBe(0xA0)
        dstBuf[4] = 1
        expect(dstBuf.byte(0)).toBe(0xA8)
    }

    @Test
    fun forIndexed4_shouldSetDataBufCorrectly() {
        val dstBuf = indexedBuffer(
            arrayOf(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN),
            arrayOf(0, 0, 0, 0, 0), PixelBrainShader.Encoding.INDEXED_4
        ) as PixelBrainShader.IndexedBuffer

        expect(dstBuf.dataBuf.size).toBe(2)
        expect(dstBuf.bytes()).toBe("00 00")

        dstBuf[0] = 1 // orange
        expect(dstBuf.bytes()).toBe("40 00")

        dstBuf[2] = 3 // green
        expect(dstBuf.bytes()).toBe("4c 00")

        dstBuf[4] = 2 // yellow
        expect(dstBuf.bytes()).toBe("4c 80")

        expect(dstBuf.colors[0]).toBe(Color.ORANGE)
        expect(dstBuf.colors[2]).toBe(Color.GREEN)
        expect(dstBuf.colors[4]).toBe(Color.YELLOW)
    }

    @Test
    fun forIndexed16_shouldSetDataBufCorrectly() {
        val dstBuf = indexedBuffer(
            arrayOf(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE),
            arrayOf(0, 0, 0, 0, 0), PixelBrainShader.Encoding.INDEXED_16
        ) as PixelBrainShader.IndexedBuffer

        expect(dstBuf.dataBuf.size).toBe(3)
        expect(dstBuf.bytes()).toBe("00 00 00")

        dstBuf[0] = 1 // orange
        expect(dstBuf.bytes()).toBe("10 00 00")

        dstBuf[2] = 3 // green
        expect(dstBuf.bytes()).toBe("10 30 00")

        dstBuf[4] = 15 // unset, so white
        expect(dstBuf.bytes()).toBe("10 30 f0")

        expect(dstBuf.colors[0]).toBe(Color.ORANGE)
        expect(dstBuf.colors[2]).toBe(Color.GREEN)
        expect(dstBuf.colors[4]).toBe(Color.WHITE)
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
        expect(dstBuf.getColors())
            .toBe("#000000,#ffff00,#000000,#ffff00,#000000")
        expect(render(dstBuf, fixture).getColors())
            .toBe("#000000,#ffff00,#000000,#ffff00,#000000")
    }

    @Test
    fun whenFewerPixels_shouldTruncate() {
        val pixels = render(directBuffer(rgbColors), fakeFixture(3))
        expect(pixels.joinToString(",") { it.toHexString() })
            .toBe("#111111,#333333,#777777")
    }

    @Test
    fun whenMorePixels_shouldRepeat() {
        val pixels = render(directBuffer(rgbColors), fakeFixture(12))
        expect(pixels.joinToString(",") { it.toHexString() })
            .toBe("#111111,#333333,#777777,#cccccc,#ffffff," +
                    "#111111,#333333,#777777,#cccccc,#ffffff," +
                    "#111111,#333333")
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