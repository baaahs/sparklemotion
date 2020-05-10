package baaahs.glsl

import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.KglJogl
import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.*

class JoglGlslManager : GlslManager() {
    override val available: Boolean
        get() = true

    override fun createContext(): GlslContext {
        val gl = createGLContext()
        return JoglGlslContext(KglJogl(gl as GL3ES3), gl)
    }

    class JoglGlslContext(kgl: Kgl, gl: GL4) : GlslContext(kgl, "330 core") {
        private val context = gl.context
        override fun <T> runInContext(fn: () -> T): T {
            context.makeCurrent()
            try {
                return fn()
            } finally {
                context.release()
            }
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