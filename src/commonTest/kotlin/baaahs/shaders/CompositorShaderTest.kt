package baaahs.shaders

import baaahs.Color
import kotlin.test.Test
import kotlin.test.expect

class CompositorShaderTest {
    private val aShader = SolidShader()
    private val aBuffer = aShader.Buffer().apply { color = Color.BLACK }
    private val bShader = SolidShader()
    private val bBuffer = bShader.Buffer().apply { color = Color.WHITE }
    private val compositor = CompositorShader(aShader, bShader)
    private val buffer = compositor.Buffer(aBuffer, bBuffer).apply {
        mode = CompositingMode.OVERLAY
        fade = .5f
    }
    private val surface = FakeSurface(1)

    @Test
    fun shouldTransmit() {
        val dstBuf = transmit(compositor, buffer, surface)
        expect(CompositingMode.OVERLAY) { dstBuf.mode }
        expect(.5f) { dstBuf.fade }
        expect(Color.BLACK) { (dstBuf.bufferA as SolidShader.Buffer).color }
        expect(Color.WHITE) { (dstBuf.bufferB as SolidShader.Buffer).color }
    }

    @Test
    fun shouldRender() {
        val pixels = render(compositor, buffer, surface)
        expect(Color(.5f, .5f, .5f)) { pixels[0] }
    }

    @Test
    fun addMode() {
        aBuffer.color = Color.RED
        bBuffer.color = Color.GREEN
        buffer.mode = CompositingMode.ADD
        buffer.fade = 1f
        val pixels = render(compositor, buffer, surface)
        expect(Color.YELLOW) { pixels[0] }
    }

    @Test
    fun shouldCrossFade() {
        buffer.fade = 1f
        val pixels = render(compositor, buffer, surface)
        expect(Color.WHITE) { pixels[0] }
    }
}