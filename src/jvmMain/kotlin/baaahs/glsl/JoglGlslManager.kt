package baaahs.glsl

import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.KglJogl
import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.*

class JoglGlslManager : GlslManager("330 core") {
    override val available: Boolean
        get() = true

    private val gl by lazy { createGLContext() }

    override fun createContext(): Kgl = KglJogl(gl as GL3ES3)

    override fun <T> runInContext(fn: () -> T): T {
        val context = gl.context
        context.makeCurrent()
        try {
            return fn()
        } finally {
            context.release()
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