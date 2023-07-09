package baaahs.gl

import baaahs.util.Logger
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.KglLwjgl
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GLCapabilities

class LwjglGlManager : GlManager() {
    private val window: Long = glWindow

    override val available: Boolean
        get() = window != 0L

    override fun createContext(trace: Boolean): GlContext {
        logger.warn { "DANGER!!! LwjglGlManager.createContext() doesn't actually create a new context!" }

        if (!available) throw RuntimeException("GLSL not available")
        GLFW.glfwMakeContextCurrent(window)
        checkCapabilities()
        GLFW.glfwMakeContextCurrent(0)

        return LwjglGlContext(maybeTrace(KglLwjgl, trace), window)
    }

    override fun createContext(display: Display, mode: Mode, trace: Boolean): GlContext {
        if (!available) throw RuntimeException("GLSL not available")

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE)
        val glwfWindow = GLFW.glfwCreateWindow(mode.width, mode.height, "Window on ${display.name}", display.id, 0)
        if (glwfWindow == 0L)
            throw RuntimeException("Failed to create the GLFW window")
//        GLFW.glfwSetWindowMonitor(glwfWindow, monitor.id, 0, 0, mode.width, mode.height, mode.refreshRate)
//        GLFW.glfwMakeContextCurrent(glwfWindow)
//        val capabilities = org.lwjgl.opengl.GL.createCapabilities()
//        GLFW.glfwMakeContextCurrent(0)

        return LwjglGlContext(maybeTrace(KglLwjgl, trace), window)
    }

    override fun observeDisplays(displays: Displays) {
        fun addMonitor(monitor: Long, isPrimary: Boolean) {
            val name = GLFW.glfwGetMonitorName(monitor)
                ?: error("Error getting name for monitor $monitor.")

            val currentMode = GLFW.glfwGetVideoMode(monitor)
                ?.let { Mode(it.width(), it.height()) }
                ?: error("Error getting mode for monitor \"$name\".")

            val videoModes = GLFW.glfwGetVideoModes(monitor)
                ?.map { Mode(it.width(), it.height()) }?.distinct()
                ?: error("Error getting modes for monitor \"$name\".")

            displays.add(Display(monitor, name, videoModes, currentMode, isPrimary))
        }

        fun removeMonitor(monitor: Long) {
            displays.remove(monitor)
        }

        val primaryMonitor = GLFW.glfwGetPrimaryMonitor()
        GLFW.glfwSetMonitorCallback { monitor, event ->
            when (event) {
                GLFW.GLFW_CONNECTED -> {
                    logger.info { "Monitor connected: $monitor" }
                    addMonitor(monitor, monitor == primaryMonitor)
                }
                GLFW.GLFW_DISCONNECTED -> {
                    logger.info { "Monitor disconnected: $monitor" }
                    removeMonitor(monitor)
                }
                else -> logger.debug { "Monitor event: $monitor $event" }
            }
        }
        val monitorPointers = GLFW.glfwGetMonitors() ?: error("Error in glfwGetMonitors")
        for (i in 0 until monitorPointers.limit()) {
            addMonitor(monitorPointers[i], monitorPointers[i] == primaryMonitor)
        }
    }

    inner class LwjglGlContext(kgl: Kgl, private val window: Long) : GlContext(kgl, "330 core") {
        var nestLevel = 0
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
    }

    companion object {
        private val logger = Logger("LwjglGlslManager")
        private val glCapabilities: ThreadLocal<GLCapabilities> =
            ThreadLocal.withInitial { org.lwjgl.opengl.GL.createCapabilities() }

        // This is initialization stuff that has to run on the main thread.
        private val glWindow: Long by lazy {
            logger.info { "Initializing LwjglGlslManager." }

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