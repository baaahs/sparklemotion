package baaahs.shaders

import baaahs.Color
import baaahs.Surface
import kotlin.math.PI
import kotlin.math.sin

/**
 * A shader that treats a surface's pixels as a linear strip and applies a configurable sine wave along the strip.
 */
class SineWaveShader() : Shader<SineWaveShader.Buffer>(ShaderId.SINE_WAVE) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun createRenderer(surface: Surface): Shader.Renderer<Buffer> = Renderer()

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*>
            get() = this@SineWaveShader

        var color: Color = Color.WHITE
        var theta: Float = 0f
        var density: Float = 1f
    }

    class Renderer : Shader.Renderer<Buffer> {
        private var pixelCount: Int = 1

        override fun beginFrame(buffer: Buffer, pixelCount: Int) {
            this.pixelCount = pixelCount
        }

        override fun draw(buffer: Buffer, pixelIndex: Int): Color {
            val theta = buffer.theta
            val density = buffer.density

            val v = sin(theta + 2 * PI * (pixelIndex.toFloat() / pixelCount * density)) / 2 + .5
            return Color.BLACK.fade(buffer.color, v.toFloat())
        }
    }
}
