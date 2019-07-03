package baaahs.shaders

import baaahs.Color
import baaahs.Pixels
import kotlin.test.Test
import kotlin.test.expect

class PixelShaderTest {
    private val surface = FakeSurface(5)
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
        val dstBuf = transmit(directBuffer(argbColors, PixelShader.Encoding.DIRECT_ARGB), surface)
        expect("#111111,#333333,#77777777,#cccccc,#00ffffff") { dstBuf.getColors() }
        expect("#111111,#333333,#77777777,#cccccc,#00ffffff") { render(dstBuf, surface).getColors() }
    }

    @Test
    fun forDirect24Bit_shouldTransmitAndRenderIgnoringAlpha() {
        val dstBuf = transmit(directBuffer(argbColors, PixelShader.Encoding.DIRECT_RGB), surface)
        expect("#111111,#333333,#777777,#cccccc,#ffffff") { dstBuf.getColors() }
        expect("#111111,#333333,#777777,#cccccc,#ffffff") { render(dstBuf, surface).getColors() }
    }

    @Test
    fun forIndexed2_shouldSetDataBufCorrectly() {
        val dstBuf = indexedBuffer(
            arrayOf(Color.BLACK, Color.YELLOW), arrayOf(0, 0, 0, 0, 0), PixelShader.Encoding.INDEXED_2
        ) as PixelShader.IndexedBuffer
        expect(1) { dstBuf.dataBuf.size }
        expect(0x00) { dstBuf.byte(0) }

        dstBuf[0] = 1
        expect(0x80) { dstBuf.byte(0) }
        dstBuf[2] = 1
        expect(0xA0) { dstBuf.byte(0) }
        dstBuf[4] = 1
        expect(0xA8) { dstBuf.byte(0) }
    }

    @Test
    fun forIndexed4_shouldSetDataBufCorrectly() {
        val dstBuf = indexedBuffer(
            arrayOf(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN),
            arrayOf(0, 0, 0, 0, 0), PixelShader.Encoding.INDEXED_4
        ) as PixelShader.IndexedBuffer

        expect(2) { dstBuf.dataBuf.size }
        expect("00 00") { dstBuf.bytes() }

        dstBuf[0] = 1 // orange
        expect("40 00") { dstBuf.bytes() }

        dstBuf[2] = 3 // green
        expect("4c 00") { dstBuf.bytes() }

        dstBuf[4] = 2 // yellow
        expect("4c 80") { dstBuf.bytes() }

        expect(Color.ORANGE) { dstBuf.colors[0] }
        expect(Color.GREEN) { dstBuf.colors[2] }
        expect(Color.YELLOW) { dstBuf.colors[4] }
    }

    @Test
    fun forIndexed16_shouldSetDataBufCorrectly() {
        val dstBuf = indexedBuffer(
            arrayOf(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE),
            arrayOf(0, 0, 0, 0, 0), PixelShader.Encoding.INDEXED_16
        ) as PixelShader.IndexedBuffer

        expect(3) { dstBuf.dataBuf.size }
        expect("00 00 00") { dstBuf.bytes() }

        dstBuf[0] = 1 // orange
        expect("10 00 00") { dstBuf.bytes() }

        dstBuf[2] = 3 // green
        expect("10 30 00") { dstBuf.bytes() }

        dstBuf[4] = 15 // unset, so white
        expect("10 30 f0") { dstBuf.bytes() }

        expect(Color.ORANGE) { dstBuf.colors[0] }
        expect(Color.GREEN) { dstBuf.colors[2] }
        expect(Color.WHITE) { dstBuf.colors[4] }
    }

    @Test
    fun forIndexed2_shouldTransmitAndRender() {
        val dstBuf = transmit(
            indexedBuffer(
                arrayOf(Color.BLACK, Color.YELLOW),
                arrayOf(0, 1, 0, 1, 0),
                PixelShader.Encoding.INDEXED_2
            ), surface
        )
        expect("#000000,#ffff00,#000000,#ffff00,#000000") { dstBuf.getColors() }
        expect("#000000,#ffff00,#000000,#ffff00,#000000") { render(dstBuf, surface).getColors() }
    }

    @Test
    fun whenFewerPixels_shouldTruncate() {
        val pixels = render(directBuffer(rgbColors), FakeSurface(3))
        expect("#111111,#333333,#777777") { pixels.joinToString(",") { it.toHexString() } }
    }

    @Test
    fun whenMorePixels_shouldRepeat() {
        val pixels = render(directBuffer(rgbColors), FakeSurface(12))
        expect(
            "#111111,#333333,#777777,#cccccc,#ffffff," +
                    "#111111,#333333,#777777,#cccccc,#ffffff," +
                    "#111111,#333333"
        ) { pixels.joinToString(",") { it.toHexString() } }
    }

    private fun directBuffer(
        colors: Array<Color>,
        encoding: PixelShader.Encoding = PixelShader.Encoding.DIRECT_ARGB
    ): PixelShader.Buffer {
        val shader = PixelShader(encoding)
        return shader.createBuffer(surface).apply {
            (0 until 5).forEach { i -> this.colors[i] = colors[i] }
        }
    }

    private fun indexedBuffer(
        palette: Array<Color>,
        colorIndices: Array<Int>,
        encoding: PixelShader.Encoding
    ): PixelShader.Buffer {
        val shader = PixelShader(encoding)
        return shader.createBuffer(surface).apply {
            palette.forEachIndexed { index, color -> this.palette[index] = color }
            colorIndices.forEachIndexed { pixelIndex, colorIndex -> this[pixelIndex] = colorIndex }
        }
    }

    private fun PixelShader.Buffer.getColors() = colors.map { it.toHexString() }.joinToString(",")
    private fun Pixels.getColors() = map { it.toHexString() }.joinToString(",")
    private fun PixelShader.IndexedBuffer.byte(index: Int) = dataBuf[index].toInt() and 0xFF
    private fun PixelShader.IndexedBuffer.bytes() =
        dataBuf.joinToString(" ") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }
}