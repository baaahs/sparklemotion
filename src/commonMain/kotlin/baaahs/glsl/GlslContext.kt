package baaahs.glsl

import baaahs.glshaders.GlslProgram
import com.danielgergely.kgl.*

abstract class GlslContext(private val kgl: Kgl, val glslVersion: String) {
    abstract fun <T> runInContext(fn: () -> T): T

    private val maxTextureUnit = 31 // TODO: should be gl.getParameter(gl.MAX_COMBINED_TEXTURE_IMAGE_UNITS)

    private val textureUnits = mutableMapOf<Any, TextureUnit>()
    private var activeTextureUnit: TextureUnit? = null

    private var activeProgram: GlslProgram? = null

    class Stats {
        var activeTexture = 0
        var bindTexture = 0
        var texImage2D = 0
    }
    val stats = Stats()

    fun createRenderer(uvTranslator: UvTranslator) =
        runInContext { GlslRenderer(this, uvTranslator) }

    fun createVertexShader(source: String): CompiledShader {
        val shaderId = check { createShader(GL_VERTEX_SHADER) } ?: throw IllegalStateException()
        val info = kgl.getShaderInfoLog(shaderId) ?: ""
        if (info.isNotEmpty()) {
            throw CompiledShader.CompilationException(info)
        }
        return CompiledShader(kgl, shaderId, source)
    }

    fun createFragmentShader(source: String): CompiledShader {
        val shaderId = check { createShader(GL_FRAGMENT_SHADER) ?: throw IllegalStateException() }
        return CompiledShader(kgl, shaderId, source)
    }

    fun useProgram(glslProgram: GlslProgram) {
        if (activeProgram !== glslProgram) {
            check { useProgram(glslProgram.id) }
            activeProgram = glslProgram
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
        kgl.checkForGlError()
        return result
    }

    fun release() {
//        TODO("not implemented")
    }
}