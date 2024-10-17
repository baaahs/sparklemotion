package baaahs.gl

import baaahs.document
import baaahs.getWebGL2Context
import baaahs.getWebGLContext
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.KglJs
import org.khronos.webgl.WebGLRenderingContext
import web.gl.GLenum
import web.gl.WebGL2RenderingContext
import web.html.HTMLCanvasElement
import web.prompts.alert

actual object GlBase {
    actual val manager: GlManager by lazy { jsManager }
    val jsManager: JsGlManager by lazy { JsGlManager() }

    class JsGlManager : GlManager() {
        override val available: Boolean by lazy {
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            val gl = canvas.getWebGLContext()
            gl != null
        }

        override fun createContext(name: String, trace: Boolean): JsGlContext {
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            return createContext(name, canvas, trace)
        }

        fun createContext(name: String, canvas: HTMLCanvasElement, trace: Boolean = false): JsGlContext {
            val webgl = canvas.getWebGL2Context()
            if (webgl == null) {
                alert(
                    "Running GLSL shows on iOS requires WebGL 2.0.\n" +
                            "\n" +
                            "Go to Settings → Safari → Advanced → Experimental Features and enable WebGL 2.0."
                )
                throw Exception("WebGL 2 not supported")
            }
            return JsGlContext(
                name,
                canvas,
                maybeTrace(KglJs(webgl.unsafeCast<WebGLRenderingContext>()), trace),
                "300 es",
                webgl
            )
        }
    }

    open class JsGlContext(
        name: String,
        val canvas: HTMLCanvasElement,
        kgl: Kgl,
        glslVersion: String,
        internal val webgl: WebGL2RenderingContext,
        checkForErrors: Boolean = false,
        state: State = State()
    ) : GlContext(name, kgl, glslVersion, checkForErrors, state) {
        override fun <T> runInContext(fn: () -> T): T = fn()
        override suspend fun <T> asyncRunInContext(fn: suspend () -> T): T = fn()

        override fun getGlInt(parameter: Int): Int =
            webgl.getParameter(parameter as GLenum) as Int

        // For RGBA32F in FloatsResultType.
        override fun checkIfResultBufferCanContainFloats(required: Boolean): Boolean {
            return ensureExtension("EXT_color_buffer_float", required)
        }

        // For RGBA16F in FloatsResultType.
        override fun checkIfResultBufferCanContainHalfFloats(required: Boolean): Boolean {
            return ensureExtension("EXT_color_buffer_half_float", required)
        }

        override fun checkForLinearFilteringOfFloatTextures(required: Boolean): Boolean {
            return ensureExtension("OES_texture_float_linear", required)
        }

        override fun checkForParallelShaderCompile(required: Boolean): Boolean {
            return ensureExtension("KHR_parallel_shader_compile", required)
        }

        /** Creates a related context with shared state and the given Kgl. */
        open fun requestAnimationFrame(callback: (Double) -> Unit) {
            web.animations.requestAnimationFrame(callback)
        }

        private fun ensureExtension(name: String, required: Boolean): Boolean {
            val extension = webgl.getExtension(name) as Any?
            if (required && extension == null) {
                alert("$name not supported")
                throw Exception("$name not supported")
            }
            return extension != null
        }
    }
}