package baaahs.shaders

import baaahs.Color
import baaahs.Surface
import kotlin.random.Random

/**
 * A shader that sets pixels to a random colors.
 */
class RandomShader : Shader<RandomShader.Buffer>(ShaderId.RANDOM) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun createRenderer(surface: Surface): Renderer = Renderer()

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*>
            get() = this@RandomShader
    }

    class Renderer : Shader.Renderer<Buffer> {
        override fun draw(buffer: Buffer, pixelIndex: Int): Color =
            Color.from(Random.nextInt(0xffffff) or (0xff ushr 24))
    }
}
