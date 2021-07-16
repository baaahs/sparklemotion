package baaahs.gl

import baaahs.document
import baaahs.window
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.KglJs
import com.danielgergely.kgl.Texture
import com.danielgergely.kgl.WebGL2RenderingContext
import org.khronos.webgl.WebGLTexture
import org.w3c.dom.HTMLCanvasElement

actual object GlBase {
    actual val manager: GlManager by lazy { jsManager }
    val jsManager: JsGlManager by lazy { JsGlManager() }

    class JsGlManager : GlManager() {
        override val available: Boolean by lazy {
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            val gl = canvas.getContext("webgl")
            gl != null
        }

        override fun createContext(trace: Boolean): JsGlContext {
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            return createContext(canvas, trace)
        }

        fun createContext(canvas: HTMLCanvasElement, trace: Boolean = false): JsGlContext {
            val webgl = canvas.getContext("webgl2") as WebGL2RenderingContext?
            if (webgl == null) {
                window.alert(
                    "Running GLSL shows on iOS requires WebGL 2.0.\n" +
                            "\n" +
                            "Go to Settings → Safari → Advanced → Experimental Features and enable WebGL 2.0."
                )
                throw Exception("WebGL 2 not supported")
            }
            return JsGlContext(
                canvas,
                maybeTrace(KglJs(webgl), trace),
                "300 es",
                webgl
            )
        }
    }

    open class JsGlContext(
        val canvas: HTMLCanvasElement,
        kgl: Kgl,
        glslVersion: String,
        internal val webgl: WebGL2RenderingContext,
        checkForErrors: Boolean = false,
        state: State = State()
    ) : GlContext(kgl, glslVersion, checkForErrors, state) {
        private val checkedExtensions = hashSetOf<String>()

        override fun <T> runInContext(fn: () -> T): T = fn()

        override fun ensureResultBufferCanContainFloats() {
            // For RGBA32F in FloatXyzwParam:
            ensureExtension("EXT_color_buffer_float")
        }

        override fun glFramebufferTexture2D(
            target: Int, attachment: Int, texTarget: Int, texture: Texture?, level: Int
        ) {
            webgl.framebufferTexture2D(target, attachment, texTarget, texture as WebGLTexture?, level)
        }

        /** Creates a related context with shared state and the given Kgl. */
        open fun requestAnimationFrame(callback: (Double) -> Unit) {
            window.requestAnimationFrame(callback)
        }

        private fun ensureExtension(name: String) {
            if (checkedExtensions.add(name) && webgl.getExtension(name) == null) {
                window.alert("$name not supported")
                throw Exception("$name not supported")
            }
        }
    }
}
