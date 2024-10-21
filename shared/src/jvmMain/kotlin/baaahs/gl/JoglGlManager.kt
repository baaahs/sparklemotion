//package baaahs.gl
//
//import com.danielgergely.kgl.Kgl
//import com.danielgergely.kgl.KglJogl
//import com.jogamp.newt.opengl.GLWindow
//import com.jogamp.opengl.*
//
//class JoglGlManager : GlManager() {
//    override val available: Boolean
//        get() = true
//
//    override fun createContext(trace: Boolean): GlContext {
//        val gl = createGLContext()
//        val kgl = maybeTrace(KglJogl(gl as GL3ES3), trace)
//        return JoglGlContext(kgl, gl)
//    }
//
//    class JoglGlContext(kgl: Kgl, gl: GL4) : GlContext(kgl, "330 core") {
//        private val context = gl.context
//        override fun <T> runInContext(fn: () -> T): T {
//            context.makeCurrent()
//            try {
//                return fn()
//            } finally {
//                context.release()
//            }
//        }
//
//        override suspend fun <T> asyncRunInContext(fn: suspend () -> T): T {
//            context.makeCurrent()
//            try {
//                return fn()
//            } finally {
//                context.release()
//            }
//        }
//    }
//
//    companion object {
//        fun createGLContext(): GL4 {
//            val glProfile = GLProfile.getGL4ES3()
//            val glCapabilities = GLCapabilities(glProfile)
//            glCapabilities.isOnscreen = false
//            val glWindow = GLWindow.create(glCapabilities)
//            glWindow.isUndecorated = true
//            val factory = GLDrawableFactory.getFactory(glProfile)
//            val autoDrawable = factory.createOffscreenAutoDrawable(null, glCapabilities, null, 1, 1)
//            autoDrawable.display()
//            return TraceGL4(autoDrawable.gl as GL4?, System.out)
//        }
//    }
//}