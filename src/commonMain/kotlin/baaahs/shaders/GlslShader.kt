package baaahs.shaders

import baaahs.*
import baaahs.glsl.GlslBase
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter

class GlslShader(val glslProgram: String) : Shader<GlslShader.Buffer>(ShaderId.GLSL_SHADER) {

    companion object: ShaderReader<GlslShader> {
        override fun parse(reader: ByteArrayReader) = GlslShader(reader.readString())
    }

    override fun serializeConfig(writer: ByteArrayWriter) {
        writer.writeString(glslProgram)
    }

    override fun createRenderer(surface: Surface, renderContext: RenderContext): Renderer<Buffer> {
        val poolKey = GlslShader::class to glslProgram
        val pooledRenderer = renderContext.registerPooled(poolKey) { PooledRenderer(glslProgram) }
        val pixels = pooledRenderer.glslRenderer.addSurface(surface)

        return object : Renderer<Buffer> {
            override fun beginFrame(buffer: Buffer, pixelCount: Int) {
                // update uniforms from buffer...
            }

            override fun draw(buffer: Buffer, pixelIndex: Int): Color {
                return pixels[pixelIndex]
            }
        }
    }

    class PooledRenderer(glslProgram: String) : baaahs.PooledRenderer {
        val glslRenderer = GlslBase.manager.createRenderer(glslProgram)

        override fun preDraw() {
            glslRenderer.draw()
        }
    }

    override fun createRenderer(surface: Surface): Renderer<Buffer> {
        val glslRenderer = GlslBase.manager.createRenderer(glslProgram)
        val pixels = glslRenderer.addSurface(surface)

        return object : Renderer<Buffer> {
            override fun beginFrame(buffer: Buffer, pixelCount: Int) {
                // update uniforms from buffer...
            }

            override fun draw(buffer: Buffer, pixelIndex: Int): Color {
                return pixels[pixelIndex]
            }
        }
    }

    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*> get() = this@GlslShader

        override fun serialize(writer: ByteArrayWriter) {
        }

        override fun read(reader: ByteArrayReader) {
        }

    }

}
