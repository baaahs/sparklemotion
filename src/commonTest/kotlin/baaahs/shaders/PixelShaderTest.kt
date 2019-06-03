package baaahs.shaders

import baaahs.Color
import kotlin.test.Test
import kotlin.test.expect

class PixelShaderTest {
    private val shader = PixelShader()
    private val buffer = shader.Buffer(5).apply {
        (0 until 5).forEach { i -> colors[i] = Color.from(i) }
    }

    @Test
    fun shouldTransmit() {
        val dstBuf = transmit(shader, buffer, FakeSurface(5))
        expect("0,1,2,3,4") {
            dstBuf.colors.map { "${it.argb}" }.joinToString(",")
        }
    }

    @Test
    fun shouldRender() {
        val pixels = render(shader, buffer, FakeSurface(5))
        expect("0,1,2,3,4") { pixels.joinToString(",") { "${it.argb}" } }
    }

    @Test
    fun whenFewerPixels_shouldTruncate() {
        val pixels = render(shader, buffer, FakeSurface(3))
        expect("0,1,2") { pixels.joinToString(",") { "${it.argb}" } }
    }

    @Test
    fun whenMorePixels_shouldRepeat() {
        val pixels = render(shader, buffer, FakeSurface(12))
        expect("0,1,2,3,4,0,1,2,3,4,0,1") { pixels.joinToString(",") { "${it.argb}" } }
    }
}