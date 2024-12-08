package baaahs.gl

import baaahs.util.Logger
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.KglLwjgl
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL33
import org.lwjgl.opengl.GLCapabilities

class LwjglGlManager : GlManager() {
    private val window: Long

    init {
        window = glWindow
    }

    override val available: Boolean
        get() = window != 0L

    override fun createContext(name: String, trace: Boolean): GlContext {
        logger.warn { "DANGER!!! LwjglGlManager.createContext() doesn't actually create a new context!" }

        if (!available) throw RuntimeException("GLSL not available")
        GLFW.glfwMakeContextCurrent(window)
        checkCapabilities()
        GLFW.glfwMakeContextCurrent(0)

        return LwjglGlContext(name, maybeTrace(KglLwjgl, trace))
    }

    inner class LwjglGlContext(name: String, kgl: Kgl) : GlContext(name, kgl, "330 core") {
        private var nestLevel = 0
        override fun <T> runInContext(fn: () -> T): T {
            if (++nestLevel == 1) {
                GLFW.glfwMakeContextCurrent(window)
                checkCapabilities()
            }
            try {
                return fn()
            } finally {
                if (--nestLevel == 0) {
                    GLFW.glfwMakeContextCurrent(0)
                }
            }
        }

        override suspend fun <T> asyncRunInContext(fn: suspend () -> T): T {
            if (++nestLevel == 1) {
                GLFW.glfwMakeContextCurrent(window)
                checkCapabilities()
            }
            try {
                return fn()
            } finally {
                if (--nestLevel == 0) {
                    GLFW.glfwMakeContextCurrent(0)
                }
            }
        }

        override fun getGlInt(parameter: Int): Int {
            val value = IntArray(1)
            GL33.glGetIntegerv(parameter, value)
            return value[0]
        }
    }

    companion object {
        private val logger = Logger("LwjglGlslManager")
        private val glCapabilities: ThreadLocal<GLCapabilities> =
            ThreadLocal.withInitial { org.lwjgl.opengl.GL.createCapabilities() }

        // This is initialization stuff that has to run on the main thread.
        private val glWindow: Long by lazy {
            logger.debug { "Initializing LwjglGlslManager." }

            if (Thread.currentThread().name != "main") {
                logger.warn { "GLSL not available. On a Mac, start java with `-XstartOnFirstThread`" }
                0L
            } else if (System.getenv("NO_GPU") == "true") {
                logger.warn { "NO_GPU=true; GLSL not available." }
                0L
            } else {
                GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))
                check(GLFW.glfwInit()) { "Unable to initialize GLFW" }

                GLFW.glfwDefaultWindowHints()
                GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
                GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2)
                GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
                GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE)
                GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
                GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE)
                GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 8)
                GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_RELEASE_BEHAVIOR, GLFW.GLFW_RELEASE_BEHAVIOR_NONE)
                GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE)

                val glwfWindow = GLFW.glfwCreateWindow(300, 300, "Hello shaders!", 0, 0)
                if (glwfWindow == 0L)
                    throw RuntimeException("Failed to create the GLFW window")

                GLFW.glfwPollEvents() // Get the event loop warmed up.

                glwfWindow
            }
        }

        // get via ThreadLocal because it's expensive and only has to happen once per thread
        private fun checkCapabilities() {
            glCapabilities.get()
        }
    }
}