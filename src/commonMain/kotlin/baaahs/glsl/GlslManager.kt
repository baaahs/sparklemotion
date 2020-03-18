package baaahs.glsl

import com.danielgergely.kgl.GL_FRAGMENT_SHADER
import com.danielgergely.kgl.GL_VERTEX_SHADER
import com.danielgergely.kgl.Kgl

abstract class GlslManager(private val glslVersion: String) {
    abstract val available: Boolean

    protected val kgl: Kgl by lazy { createContext() }

    abstract fun createContext(): Kgl

    abstract fun <T> runInContext(fn: () -> T): T

    fun createProgram(fragShader: String): Program {
        return runInContext {
            Program(this, fragShader, glslVersion, GlslBase.plugins)
        }
    }

    fun createRenderer(
        program: Program,
        uvTranslator: UvTranslator
    ): GlslRenderer {
        return runInContext {
            GlslRenderer(kgl, object : GlslRenderer.ContextSwitcher {
                override fun <T> inContext(fn: () -> T): T = runInContext(fn)
            }, program, uvTranslator)
        }
    }

    fun createVertexShader(source: String): CompiledShader {
        val shaderId = check { createShader(GL_VERTEX_SHADER) } ?: throw IllegalStateException()
        return CompiledShader(kgl, shaderId, source)
    }

    fun createFragmentShader(source: String): CompiledShader {
        val shaderId = check { createShader(GL_FRAGMENT_SHADER) ?: throw IllegalStateException() }
        return CompiledShader(kgl, shaderId, source)
    }

    fun <T> check(fn: Kgl.() -> T): T {
        val result = kgl.fn()
        kgl.checkForGlError()
        return result
    }
}