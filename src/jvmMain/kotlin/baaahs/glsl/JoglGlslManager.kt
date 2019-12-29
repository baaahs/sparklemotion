package baaahs.glsl

import baaahs.shaders.GlslShader
import com.danielgergely.kgl.KglJogl
import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.*

class JoglGlslManager : GlslManager {
    override val available: Boolean
        get() = true

    override fun createRenderer(
        fragShader: String,
        uvTranslator: UvTranslator,
        adjustableValues: List<GlslShader.AdjustableValue>,
        plugins: List<GlslPlugin>
    ): GlslRenderer {
        val gl = createGLContext()
        val contextSwitcher = object : GlslRenderer.ContextSwitcher {
            override fun <T> inContext(fn: () -> T): T {
                val context = gl.context
                context.makeCurrent()
                try {
                    return fn()
                } finally {
                    context.release()
                }
            }
        }

        val kgl = KglJogl(gl as GL3ES3)
        return contextSwitcher.inContext {
            GlslRenderer(kgl, contextSwitcher, fragShader, uvTranslator, adjustableValues, "330 core", plugins)
        }
    }

    companion object {
        fun createGLContext(): GL4 {
            val glProfile = GLProfile.getGL4ES3()
            val glCapabilities = GLCapabilities(glProfile)
            glCapabilities.isOnscreen = false
            val glWindow = GLWindow.create(glCapabilities)
            glWindow.isUndecorated = true
            val factory = GLDrawableFactory.getFactory(glProfile)
            val autoDrawable = factory.createOffscreenAutoDrawable(null, glCapabilities, null, 1, 1)
            autoDrawable.display()
            return TraceGL4(autoDrawable.gl as GL4?, System.out)
        }
    }
}