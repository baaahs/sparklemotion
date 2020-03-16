package baaahs.shaders

import baaahs.Color
import baaahs.Surface

/**
 * A shader which combines the results of two sub-shaders according to a specified compositing mode and cross-fade
 * value.
 */
class CompositorShader(val aShader: Shader<*>, val bShader: Shader<*>) :
    Shader<CompositorShader.Buffer>(ShaderId.COMPOSITOR) {

    override fun createBuffer(surface: Surface) = Buffer(aShader.createBuffer(surface), bShader.createBuffer(surface))

    override fun createRenderer(surface: Surface, renderContext: RenderContext): Shader.Renderer<Buffer> {
        val rendererA: Shader.Renderer<*> = aShader.createRenderer(surface, renderContext)
        val rendererB: Shader.Renderer<*> = bShader.createRenderer(surface, renderContext)
        return Renderer(rendererA, rendererB)
    }

    override fun createRenderer(surface: Surface): Shader.Renderer<Buffer> {
        val rendererA: Shader.Renderer<*> = aShader.createRenderer(surface)
        val rendererB: Shader.Renderer<*> = bShader.createRenderer(surface)
        return Renderer(rendererA, rendererB)
    }

    fun createBuffer(bufferA: Shader.Buffer, bufferB: Shader.Buffer): Buffer =
        Buffer(bufferA, bufferB)

    inner class Buffer(
        val bufferA: Shader.Buffer, val bufferB: Shader.Buffer,
        var mode: CompositingMode = CompositingMode.NORMAL,
        var fade: Float = 0.5f
    ) : Shader.Buffer {
        override val shader: Shader<*> = this@CompositorShader
    }

    class Renderer<A : Shader.Buffer, B : Shader.Buffer>(
        private val rendererA: Shader.Renderer<A>,
        private val rendererB: Shader.Renderer<B>
    ) : Shader.Renderer<Buffer> {

        @Suppress("UNCHECKED_CAST")
        override fun beginFrame(buffer: Buffer, pixelCount: Int) {
            rendererA.beginFrame(buffer.bufferA as A, pixelCount)
            rendererB.beginFrame(buffer.bufferB as B, pixelCount)
        }

        @Suppress("UNCHECKED_CAST")
        override fun draw(buffer: Buffer, pixelIndex: Int): Color {
            val dest = rendererA.draw(buffer.bufferA as A, pixelIndex)
            val src = rendererB.draw(buffer.bufferB as B, pixelIndex)
            return dest.fade(buffer.mode.composite(src, dest), buffer.fade)
        }

        override fun endFrame() {
            rendererA.endFrame()
            rendererB.endFrame()
        }

        override fun release() {
            rendererA.release()
            rendererB.release()
        }
    }
}

enum class CompositingMode {
    NORMAL {
        override fun composite(src: Color, dest: Color) = src
    },
    ADD {
        override fun composite(src: Color, dest: Color) = dest.plus(src)
    };

    abstract fun composite(src: Color, dest: Color): Color

    companion object {
        val values = values()
        fun get(i: Byte): CompositingMode {
            return values[i.toInt()]
        }
    }
}