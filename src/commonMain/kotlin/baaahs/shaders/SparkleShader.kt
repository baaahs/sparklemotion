package baaahs.shaders

import baaahs.Color
import baaahs.Surface
import kotlin.random.Random

/**
 * A shader that randomly sets some pixels to white, changing with each frame.
 */
class SparkleShader : Shader<SparkleShader.Buffer>(ShaderId.SPARKLE) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun createRenderer(surface: Surface): Shader.Renderer<Buffer> = Renderer()

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*> = this@SparkleShader

        var color: Color = Color.WHITE
        var sparkliness: Float = .1F
    }

    class Renderer : Shader.Renderer<Buffer> {
        override fun draw(buffer: Buffer, pixelIndex: Int): Color {
            return if (Random.nextFloat() < buffer.sparkliness ) { buffer.color } else { Color.BLACK }
        }
    }
}
