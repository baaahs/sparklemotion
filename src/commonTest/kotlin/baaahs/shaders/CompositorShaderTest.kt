package baaahs.shaders

import baaahs.Color
import baaahs.Surface
import kotlin.test.Test
import kotlin.test.expect

class CompositorShaderTest {
    private val aShader = SolidShader()
    private val aBuffer = aShader.Buffer().apply { color = Color.BLACK }
    private val bShader = SolidShader()
    private val bBuffer = bShader.Buffer().apply { color = Color.WHITE }
    private val compositor = CompositorShader(aShader, bShader)
    private val buffer = compositor.Buffer(aBuffer, bBuffer).apply {
        mode = CompositingMode.NORMAL
        fade = .5f
    }
    private val surface = FakeSurface(1)

    @Test
    fun shouldRender() {
        val pixels = render(buffer, surface)
        expect(Color(.5f, .5f, .5f)) { pixels[0] }
    }

    @Test
    fun addMode() {
        aBuffer.color = Color.RED
        bBuffer.color = Color.GREEN
        buffer.mode = CompositingMode.ADD
        buffer.fade = 1f
        val pixels = render(buffer, surface)
        expect(Color.YELLOW) { pixels[0] }
    }

    @Test
    fun addMode_doesntOverflow() {
        val pixels = render(
            composite(
                solid(Color(0xffeedd).opaque()),
                solid(Color(0x111111).opaque()),
                CompositingMode.ADD
            ), surface
        )
        expect(Color(0xffffee).opaque()) { pixels[0] }
    }

    @Test
    fun shouldCrossFade() {
        buffer.fade = 1f
        val pixels = render(buffer, surface)
        expect(Color.WHITE) { pixels[0] }
    }

    @Test
    fun multiLevelCompositing() {
        val pixels = render(
            composite(
                composite(
                    solid(Color(0x010000).opaque()),
                    solid(Color(0x100000).opaque()),
                    CompositingMode.ADD
                ),
                composite(
                    solid(Color(0x000100).opaque()),
                    solid(Color(0x001000).opaque()),
                    CompositingMode.ADD
                ),
                CompositingMode.ADD
            ), surface
        )
        expect(Color(0x111100).opaque()) { pixels[0] }
    }

    fun composite(
        left: Pair<Shader<*>, Shader.Buffer>,
        right: Pair<Shader<*>, Shader.Buffer>,
        mode: CompositingMode = CompositingMode.NORMAL,
        fade: Float = 1f
    ): Pair<CompositorShader, CompositorShader.Buffer> {
        val shader = CompositorShader(left.first, right.first)
        val buffer = shader.createBuffer(left.second, right.second)
        buffer.mode = mode
        buffer.fade = fade
        return shader to buffer
    }

    fun solid(color: Color): Pair<SolidShader, SolidShader.Buffer> {
        val shader = SolidShader()
        val buffer = shader.createBuffer(surface)
        buffer.color = color
        return shader to buffer
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <T : Shader.Buffer> render(srcBuf: T, surface: Surface): Pixels {
        val pixels = FakePixels(surface.pixelCount)
        val shader: Shader<T> = srcBuf.shader as Shader<T>
        val renderer = shader.createRenderer(surface)
        renderer.beginFrame(srcBuf, pixels.size)
        for (i in pixels.indices) {
            pixels[i] = renderer.draw(srcBuf, i)
        }
        renderer.endFrame()
        return pixels
    }

    internal fun <T : Shader.Buffer> render(srcShaderAndBuffer: Pair<Shader<T>, T>, surface: Surface): Pixels =
        render(srcShaderAndBuffer.second, surface)

}