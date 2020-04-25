package baaahs.glsl

import baaahs.glshaders.GlslProgram
import com.danielgergely.kgl.GL_FRAGMENT_SHADER
import com.danielgergely.kgl.GL_VERTEX_SHADER
import com.danielgergely.kgl.Kgl

abstract class GlslContext(private val kgl: Kgl, val glslVersion: String) {
    abstract fun <T> runInContext(fn: () -> T): T

    fun createProgram(fragShader: String): Program =
        runInContext { Program(this, fragShader, glslVersion, GlslBase.plugins) }

    fun createRenderer(program: GlslProgram, uvTranslator: UvTranslator) =
        runInContext { GlslRenderer(this, program, uvTranslator) }

    fun createVertexShader(source: String): CompiledShader {
        val shaderId = check { createShader(GL_VERTEX_SHADER) } ?: throw IllegalStateException()
        return CompiledShader(kgl, shaderId, source)
    }

    fun createFragmentShader(source: String): CompiledShader {
        val shaderId = check { createShader(GL_FRAGMENT_SHADER) ?: throw IllegalStateException() }
        return CompiledShader(kgl, shaderId, source)
    }

    fun <T> noCheck(fn: Kgl.() -> T): T {
        return kgl.fn()
    }

    fun <T> check(fn: Kgl.() -> T): T {
        val result = kgl.fn()
        kgl.checkForGlError()
        return result
    }
}