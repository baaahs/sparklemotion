package baaahs.glsl

import baaahs.shaders.GlslShader
import de.fabmax.kool.createContext

class JvmGlslManager : GlslManager {
//    private val window: Long

    /**
     * This is initialization stuff that's required on the main thread.
     */
    init {
//        GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))
//        if (!GLFW.glfwInit())
//            throw IllegalStateException("Unable to initialize GLFW")
//
//        GLFW.glfwDefaultWindowHints()
//        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4)
//        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1)
//        GLFW.glfwWindowHint(
//            GLFW.GLFW_OPENGL_PROFILE,
//            GLFW.GLFW_OPENGL_CORE_PROFILE
//        )
//        GLFW.glfwWindowHint(
//            GLFW.GLFW_OPENGL_FORWARD_COMPAT,
//            GLFW.GLFW_TRUE
//        )
//        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
//        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
//        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 8)
//        GLFW.glfwWindowHint(
//            GLFW.GLFW_CONTEXT_RELEASE_BEHAVIOR,
//            GLFW.GLFW_RELEASE_BEHAVIOR_NONE
//        )

//        window = GLFW.glfwCreateWindow(
//            300,
//            300,
//            "Hello shaders!",
//            MemoryUtil.NULL,
//            MemoryUtil.NULL
//        )
//        if (window == MemoryUtil.NULL)
//            throw RuntimeException("Failed to create the GLFW window")

//        GLFW.glfwPollEvents() // Get the event loop warmed up.
    }

    override fun createRenderer(
        fragShader: String,
        adjustableValues: List<GlslShader.AdjustableValue>
    ): GlslRenderer {
        println("Creating JvmGlslManager for $fragShader!")
        return GlslRenderer(createContext(), fragShader, adjustableValues)
    }
}