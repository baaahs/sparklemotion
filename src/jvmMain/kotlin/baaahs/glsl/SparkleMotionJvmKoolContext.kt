package kotlinx.coroutines.experimental.baaahs.glsl

import de.fabmax.kool.*
import de.fabmax.kool.gl.GL_DEPTH_COMPONENT
import de.fabmax.kool.gl.GL_LINEAR
import de.fabmax.kool.gl.GL_MAX_TEXTURE_IMAGE_UNITS
import de.fabmax.kool.gl.GL_VERSION
import de.fabmax.kool.platform.JvmAssetManager
import de.fabmax.kool.platform.getResolutionAt
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.EXTTextureFilterAnisotropic
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.awt.Desktop
import java.net.URI

class Lwjgl3Context(props: InitProps) : KoolContext() {

    override val glCapabilities: GlCapabilities

    override val assetMgr = JvmAssetManager(props)

    val window: Long

    override var windowWidth = 0
        private set
    override var windowHeight = 0
        private set

    val supportedExtensions = mutableSetOf<String>()

    private val glThread = Thread.currentThread()

    init {
        // configure GLFW
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, props.msaaSamples)

        // create window
        window = GLFW.glfwCreateWindow(props.width, props.height, props.title, props.monitor, props.share)
        if (window == MemoryUtil.NULL) {
            throw KoolException("Failed to create the GLFW window")
        }

        // center the window
        val primary = DesktopImpl.primaryMonitor
        val wndPosX = primary.posX + (primary.widthPx - props.width) / 2
        val wndPosY = primary.posY + (primary.heightPx - props.height) / 2
        GLFW.glfwSetWindowPos(window, wndPosX, wndPosY)
        screenDpi = primary.dpi

        // install window callbacks
        GLFW.glfwSetFramebufferSizeCallback(window) { _, w, h ->
            windowWidth = w
            windowHeight = h
        }
        GLFW.glfwSetWindowPosCallback(window) { _, x, y ->
            screenDpi = getResolutionAt(x, y)
        }

        // install mouse callbacks
        GLFW.glfwSetMouseButtonCallback(window) { _, btn, act, _ ->
            inputMgr.handleMouseButtonState(btn, act == GLFW.GLFW_PRESS)
        }
        GLFW.glfwSetCursorPosCallback(window) { _, x, y ->
            inputMgr.handleMouseMove(x.toFloat(), y.toFloat())
        }
        GLFW.glfwSetCursorEnterCallback(window) { _, entered ->
            if (!entered) {
                inputMgr.handleMouseExit()
            }
        }
        GLFW.glfwSetScrollCallback(window) { _, _, yOff ->
            inputMgr.handleMouseScroll(yOff.toFloat())
        }

        // install keyboard callbacks
        GLFW.glfwSetKeyCallback(window) { _, key, _, action, mods ->
            val event = when (action) {
                GLFW.GLFW_PRESS -> InputManager.KEY_EV_DOWN
                GLFW.GLFW_REPEAT -> InputManager.KEY_EV_DOWN or InputManager.KEY_EV_REPEATED
                GLFW.GLFW_RELEASE -> InputManager.KEY_EV_UP
                else -> -1
            }
            if (event != -1) {
                val keyCode = KEY_CODE_MAP[key] ?: key
                var keyMod = 0
                if (mods and GLFW.GLFW_MOD_ALT != 0) {
                    keyMod = keyMod or InputManager.KEY_MOD_ALT
                }
                if (mods and GLFW.GLFW_MOD_CONTROL != 0) {
                    keyMod = keyMod or InputManager.KEY_MOD_CTRL
                }
                if (mods and GLFW.GLFW_MOD_SHIFT != 0) {
                    keyMod = keyMod or InputManager.KEY_MOD_SHIFT
                }
                if (mods and GLFW.GLFW_MOD_SUPER != 0) {
                    keyMod = keyMod or InputManager.KEY_MOD_SUPER
                }
                inputMgr.keyEvent(keyCode, keyMod, event)
            }
        }
        GLFW.glfwSetCharCallback(window) { _, codepoint ->
            inputMgr.charTyped(codepoint.toChar())
        }

        // get the thread stack and push a new frame
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            GLFW.glfwGetFramebufferSize(window, pWidth, pHeight)
            windowWidth = pWidth[0]
            windowHeight = pHeight[0]
        } // the stack frame is popped automatically

        // make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window)
        // enable v-sync
        GLFW.glfwSwapInterval(1)
        // make the window visible
        GLFW.glfwShowWindow(window)

        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed
        // externally. LWJGL detects the context that is current in the current thread, creates the GLCapabilities
        // instance and makes the OpenGL bindings available for use.
        GL.createCapabilities()

        val versionStr = GL11.glGetString(GL_VERSION) ?: ""
        var versionMajor = 0
        var versionMinor = 0
        if (versionStr.matches(Regex("^[0-9]\\.[0-9].*"))) {
            val parts = versionStr.split(Regex("[^0-9]"), 3)
            versionMajor = parts[0].toInt()
            versionMinor = parts[1].toInt()
        }

        // check for anisotropic texture filtering support
        var anisotropicTexFilterInfo = AnisotropicTexFilterInfo.NOT_SUPPORTED
        supportedExtensions.addAll(GL11.glGetString(GL11.GL_EXTENSIONS)?.split(" ") ?: emptyList())
        if (supportedExtensions.contains("GL_EXT_texture_filter_anisotropic")) {
            val max = GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT)
            anisotropicTexFilterInfo = AnisotropicTexFilterInfo(max, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT)
        }

        // get number of available texture unis
        val maxTexUnits = GL11.glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS)

        // This is required to be able to set gl_PointSize in vertex shaders (to get same behaviour as in GLES)
        GL11.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE)

        // Geometry shaders are introduced with OpenGL 3.2
        val geometryShader = versionMajor > 3 || (versionMajor == 3 && versionMinor >= 2)

        glCapabilities = GlCapabilities(
            uint32Indices = true,
            shaderIntAttribs = true,
            maxTexUnits = maxTexUnits,
            premultipliedAlphaTextures = true,
            depthTextures = true,
            depthComponentIntFormat = GL_DEPTH_COMPONENT,
            depthFilterMethod = GL_LINEAR,
            anisotropicTexFilterInfo = anisotropicTexFilterInfo,
            geometryShader = geometryShader,
            glslDialect = GlslDialect.GLSL_DIALECT_330,
            glVersion = GlVersion("OpenGL", versionMajor, versionMinor)
        )

        textureMgr.maxTextureLoadsPerFrame = MAX_TEXTURE_LOADS_PER_FRAmE
    }

    override fun openUrl(url: String)  = Desktop.getDesktop().browse(URI(url))

    override fun run() {
        // run the rendering loop until the user has attempted to close the window
        var prevTime = System.nanoTime()
        while (!GLFW.glfwWindowShouldClose(window)) {
            // determine time delta
            val time = System.nanoTime()
            val dt = (time - prevTime) / 1e9
            prevTime = time

            // render engine content
            render(dt)
            // swap the color buffers
            GLFW.glfwSwapBuffers(window)
            // Poll for window events. The key callback above will only be invoked during this call.
            GLFW.glfwPollEvents()
        }
    }

    override fun destroy() {
        // free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(window)
        GLFW.glfwDestroyWindow(window)

        // terminate GLFW and free the error callback
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)?.free()
    }

    override fun checkIsGlThread() {
        if (Thread.currentThread() !== glThread) {
            throw KoolException("Not on GL thread")
        }
    }

    class InitProps(init: InitProps.() -> Unit = {}) {
        var width = 1024
        var height = 768
        var title = "Kool"
        var monitor = 0L
        var share = 0L

        var msaaSamples = 8

        var assetsBaseDir = "./assets"

        val extraFonts = mutableListOf<String>()

        init {
            init()
        }
    }

    companion object {
        const val MAX_TEXTURE_LOADS_PER_FRAmE = 20

        val KEY_CODE_MAP: Map<Int, Int> = mutableMapOf(
            GLFW.GLFW_KEY_LEFT_CONTROL to InputManager.KEY_CTRL_LEFT,
            GLFW.GLFW_KEY_RIGHT_CONTROL to InputManager.KEY_CTRL_RIGHT,
            GLFW.GLFW_KEY_LEFT_SHIFT to InputManager.KEY_SHIFT_LEFT,
            GLFW.GLFW_KEY_RIGHT_SHIFT to InputManager.KEY_SHIFT_RIGHT,
            GLFW.GLFW_KEY_LEFT_ALT to InputManager.KEY_ALT_LEFT,
            GLFW.GLFW_KEY_RIGHT_ALT to InputManager.KEY_ALT_RIGHT,
            GLFW.GLFW_KEY_LEFT_SUPER to InputManager.KEY_SUPER_LEFT,
            GLFW.GLFW_KEY_RIGHT_SUPER to InputManager.KEY_SUPER_RIGHT,
            GLFW.GLFW_KEY_ESCAPE to InputManager.KEY_ESC,
            GLFW.GLFW_KEY_MENU to InputManager.KEY_MENU,
            GLFW.GLFW_KEY_ENTER to InputManager.KEY_ENTER,
            GLFW.GLFW_KEY_KP_ENTER to InputManager.KEY_NP_ENTER,
            GLFW.GLFW_KEY_KP_DIVIDE to InputManager.KEY_NP_DIV,
            GLFW.GLFW_KEY_KP_MULTIPLY to InputManager.KEY_NP_MUL,
            GLFW.GLFW_KEY_KP_ADD to InputManager.KEY_NP_PLUS,
            GLFW.GLFW_KEY_KP_SUBTRACT to InputManager.KEY_NP_MINUS,
            GLFW.GLFW_KEY_BACKSPACE to InputManager.KEY_BACKSPACE,
            GLFW.GLFW_KEY_TAB to InputManager.KEY_TAB,
            GLFW.GLFW_KEY_DELETE to InputManager.KEY_DEL,
            GLFW.GLFW_KEY_INSERT to InputManager.KEY_INSERT,
            GLFW.GLFW_KEY_HOME to InputManager.KEY_HOME,
            GLFW.GLFW_KEY_END to InputManager.KEY_END,
            GLFW.GLFW_KEY_PAGE_UP to InputManager.KEY_PAGE_UP,
            GLFW.GLFW_KEY_PAGE_DOWN to InputManager.KEY_PAGE_DOWN,
            GLFW.GLFW_KEY_LEFT to InputManager.KEY_CURSOR_LEFT,
            GLFW.GLFW_KEY_RIGHT to InputManager.KEY_CURSOR_RIGHT,
            GLFW.GLFW_KEY_UP to InputManager.KEY_CURSOR_UP,
            GLFW.GLFW_KEY_DOWN to InputManager.KEY_CURSOR_DOWN,
            GLFW.GLFW_KEY_F1 to InputManager.KEY_F1,
            GLFW.GLFW_KEY_F2 to InputManager.KEY_F2,
            GLFW.GLFW_KEY_F3 to InputManager.KEY_F3,
            GLFW.GLFW_KEY_F4 to InputManager.KEY_F4,
            GLFW.GLFW_KEY_F5 to InputManager.KEY_F5,
            GLFW.GLFW_KEY_F6 to InputManager.KEY_F6,
            GLFW.GLFW_KEY_F7 to InputManager.KEY_F7,
            GLFW.GLFW_KEY_F8 to InputManager.KEY_F8,
            GLFW.GLFW_KEY_F9 to InputManager.KEY_F9,
            GLFW.GLFW_KEY_F10 to InputManager.KEY_F10,
            GLFW.GLFW_KEY_F11 to InputManager.KEY_F11,
            GLFW.GLFW_KEY_F12 to InputManager.KEY_F12
        )
    }
}

