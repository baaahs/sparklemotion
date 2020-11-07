package baaahs.shaders

import baaahs.Color
import baaahs.Pixels
import kotlin.test.Test
import kotlin.test.expect

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
        expect("#111111,#333333,#77777777,#cccccc,#00ffffff") { dstBuf.getColors() }
        expect("#111111,#333333,#77777777,#cccccc,#00ffffff") { render(dstBuf, fixture).getColors() }
    }

    @Test
    fun forDirect24Bit_shouldTransmitAndRenderIgnoringAlpha() {
        val dstBuf = transmit(directBuffer(argbColors, PixelBrainShader.Encoding.DIRECT_RGB), fixture)
        expect("#111111,#333333,#777777,#cccccc,#ffffff") { dstBuf.getColors() }
        expect("#111111,#333333,#777777,#cccccc,#ffffff") { render(dstBuf, fixture).getColors() }
    }

    @Test
    fun forIndexed2_shouldSetDataBufCorrectly() {
        val dstBuf = indexedBuffer(
            arrayOf(Color.BLACK, Color.YELLOW), arrayOf(0, 0, 0, 0, 0), PixelBrainShader.Encoding.INDEXED_2
        ) as PixelBrainShader.IndexedBuffer
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
            arrayOf(0, 0, 0, 0, 0), PixelBrainShader.Encoding.INDEXED_4
        ) as PixelBrainShader.IndexedBuffer

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
            arrayOf(0, 0, 0, 0, 0), PixelBrainShader.Encoding.INDEXED_16
        ) as PixelBrainShader.IndexedBuffer

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
                PixelBrainShader.Encoding.INDEXED_2
            ), fixture
        )
        expect("#000000,#ffff00,#000000,#ffff00,#000000") { dstBuf.getColors() }
        expect("#000000,#ffff00,#000000,#ffff00,#000000") { render(dstBuf, fixture).getColors() }
    }

    @Test
    fun whenFewerPixels_shouldTruncate() {
        val pixels = render(directBuffer(rgbColors), fakeFixture(3))
        expect("#111111,#333333,#777777") { pixels.joinToString(",") { it.toHexString() } }
    }

    @Test
    fun whenMorePixels_shouldRepeat() {
        val pixels = render(directBuffer(rgbColors), fakeFixture(12))
        expect(
            "#111111,#333333,#777777,#cccccc,#ffffff," +
                    "#111111,#333333,#777777,#cccccc,#ffffff," +
                    "#111111,#333333"
        ) { pixels.joinToString(",") { it.toHexString() } }
    }

    private fun directBuffer(
        colors: Array<Color>,
        encoding: PixelBrainShader.Encoding = PixelBrainShader.Encoding.DIRECT_ARGB
    ): PixelBrainShader.Buffer {
        val shader = PixelBrainShader(encoding)
        return shader.createBuffer(fixture.pixelCount).apply {
            (0 until 5).forEach { i -> this.colors[i] = colors[i] }
        }
    }

    private fun indexedBuffer(
        palette: Array<Color>,
        colorIndices: Array<Int>,
        encoding: PixelBrainShader.Encoding
    ): PixelBrainShader.Buffer {
        val shader = PixelBrainShader(encoding)
        return shader.createBuffer(fixture.pixelCount).apply {
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