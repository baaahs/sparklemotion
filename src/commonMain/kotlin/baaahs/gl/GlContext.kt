package baaahs.gl

import baaahs.gl.glsl.CompilationException
import baaahs.gl.glsl.CompiledShader
import baaahs.gl.glsl.GlslProgram
import baaahs.glsl.Uniform
import baaahs.util.Logger
import com.danielgergely.kgl.*

abstract class GlContext(
    private val kgl: Kgl,
    val glslVersion: String,
    var checkForErrors: Boolean = false
) {
    init { logger.debug { "Created ${this::class.simpleName}" } }
    abstract fun <T> runInContext(fn: () -> T): T

    private var viewport: List<Int> = emptyList()
    private val maxTextureUnit = 31 // TODO: should be gl.getParameter(gl.MAX_COMBINED_TEXTURE_IMAGE_UNITS)

    private val textureUnits = mutableMapOf<Any, TextureUnit>()
    private var activeTextureUnit: TextureUnit? = null

    private var activeProgram: GlslProgram? = null
    private var activeRenderBuffer: RenderBuffer? = null

    class Stats {
        var activeTexture = 0
        var bindTexture = 0
        var texImage2D = 0
    }
    val stats = Stats()

    fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        val newViewport = listOf(x, y, width, height)
        if (newViewport != viewport) {
            check { viewport(x, y, width, height) }
            viewport = newViewport
        }
    }

    fun createVertexShader(source: String): CompiledShader {
        return CompiledShader(this, GL_VERTEX_SHADER, source)
    }

    fun createFragmentShader(source: String): CompiledShader {
        return CompiledShader(this, GL_FRAGMENT_SHADER, source)
    }

    fun compile(vertexShader: CompiledShader, fragShader: CompiledShader): Program {
        return runInContext {
            val program = runInContext { check { createProgram() ?: throw IllegalStateException() } }
            check { attachShader(program, vertexShader.shaderId) }
            check { attachShader(program, fragShader.shaderId) }
            check { linkProgram(program) }
            if (check { getProgramParameter(program, GL_LINK_STATUS) } != GL_TRUE) {
                vertexShader.validate()
                fragShader.validate()

                val infoLog = check { getProgramInfoLog(program) }
                throw CompilationException(infoLog ?: "Huh? Program error?")
            }
            program
        }
    }

    fun useProgram(glslProgram: GlslProgram) {
        if (activeProgram !== glslProgram) {
            check { useProgram(glslProgram.id) }
            activeProgram = glslProgram
        }
    }

    fun createRenderBuffer(): RenderBuffer {
        val frameBuffer = check { createFramebuffer() }
        val renderBuffer = check { createRenderbuffer() }

        return RenderBuffer(frameBuffer, renderBuffer)
    }

    inner class RenderBuffer(
        private val framebuffer: Framebuffer,
        private val renderbuffer: Renderbuffer
    ) {
        var curRenderbufferStorageArgs = emptyList<Any>()
        var curFramebufferRenderbufferArgs = emptyList<Any>()

        fun bind(
            internalformat: Int, width: Int, height: Int,
            attachment: Int, renderbuffertarget: Int
        ) {
            if (activeRenderBuffer != this) {
                check { bindFramebuffer(GL_FRAMEBUFFER, framebuffer) }
                check { bindRenderbuffer(GL_RENDERBUFFER, renderbuffer) }
                activeRenderBuffer = this
            }

            val newRenderbufferStorageArgs = listOf(internalformat, width, height)
            if (newRenderbufferStorageArgs != curRenderbufferStorageArgs) {
                check { renderbufferStorage(GL_RENDERBUFFER, internalformat, width, height) }
                curRenderbufferStorageArgs = newRenderbufferStorageArgs
            }

            val newFramebufferRenderbufferArgs = listOf(attachment, renderbuffertarget)
            if (newFramebufferRenderbufferArgs != curFramebufferRenderbufferArgs) {
                check { renderbufferStorage(GL_RENDERBUFFER, internalformat, width, height) }
                curFramebufferRenderbufferArgs = newFramebufferRenderbufferArgs
                check { framebufferRenderbuffer(GL_FRAMEBUFFER, attachment, renderbuffertarget, renderbuffer) }
            }

            if (checkForErrors) {
                val status = check { checkFramebufferStatus(GL_FRAMEBUFFER) }
                if (status != GL_FRAMEBUFFER_COMPLETE) {
                    logger.warn { "FrameBuffer huh? $status" }
                }
            }
        }

        fun release() {
            if (activeRenderBuffer == this) {
                check { bindRenderbuffer(GL_RENDERBUFFER, null) }
                check { bindFramebuffer(GL_FRAMEBUFFER, null) }
            }

            check { deleteFramebuffer(framebuffer) }
            check { deleteRenderbuffer(renderbuffer) }
        }
    }

    fun getTextureUnit(key: Any): TextureUnit {
        return textureUnits[key] ?: allocTextureUnit().also { textureUnits[key] = it }
    }

    private fun allocTextureUnit(): TextureUnit {
        val nextTextureUnit = textureUnits.size
        check(nextTextureUnit <= maxTextureUnit) { "too many texture units" }
        return TextureUnit(nextTextureUnit)
    }

    inner class TextureUnit(private val unitNumber: Int) {
        var boundTexture: Texture? = null

        private fun activate() {
            if (activeTextureUnit !== this) {
                stats.activeTexture++
                check { activeTexture(GL_TEXTURE0 + unitNumber) }
                activeTextureUnit = this
            }
        }

        fun bindTexture(texture: Texture) {
            if (boundTexture !== texture) {
                activate()
                stats.bindTexture++
                check { bindTexture(GL_TEXTURE_2D, texture) }
                boundTexture = texture
            }
        }

        fun uploadTexture(level: Int, internalFormat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, buffer: Buffer, offset: Int = 0) {
            stats.texImage2D++
            check { texImage2D(GL_TEXTURE_2D, level, internalFormat, width, height, border, format, type, buffer, offset) }
        }

        fun configure(minFilter: Int = GL_LINEAR, maxFilter: Int = GL_LINEAR) {
            check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter) }
            check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, maxFilter) }
            check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE) }
            check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE) }
        }

        fun setUniform(uniform: Uniform) {
            uniform.set(unitNumber)
        }
    }

    fun <T> noCheck(fn: Kgl.() -> T): T {
        return kgl.fn()
    }

    fun <T> check(fn: Kgl.() -> T): T {
        val result = kgl.fn()

        if (checkForErrors) checkForGlError()

        return result
    }

    private fun checkForGlError() {
        val error = kgl.getError()

        val code = when (error) {
            GL_INVALID_ENUM -> "GL_INVALID_ENUM"
            GL_INVALID_VALUE -> "GL_INVALID_VALUE"
            GL_INVALID_OPERATION -> "GL_INVALID_OPERATION"
            GL_INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION"
            GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "FRAMEBUFFER_INCOMPLETE_ATTACHMENT"
//            GL_CONTEXT_LOST_WEBGL -> "GL_CONTEXT_LOST_WEBGL"
            GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
            else -> "unknown error $error"
        }

        if (error != 0) {
            logger.error { "OpenGL Error: $code" }
            throw RuntimeException("OpenGL Error: $code")
        }
    }

    fun release() {
//        TODO("not implemented")
    }

    companion object {
        private val logger = Logger("GlslContext")

        const val GL_RGB32F = 0x8815
    }
}