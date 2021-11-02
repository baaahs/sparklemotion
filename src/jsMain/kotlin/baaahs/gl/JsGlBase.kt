package baaahs.gl

import baaahs.document
import baaahs.window
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.KglJs
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.WebGLObject
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
        override fun <T> runInContext(fn: () -> T): T = fn()
        override suspend fun <T> asyncRunInContext(fn: suspend () -> T): T = fn()

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

        /** Creates a related context with shared state and the given Kgl. */
        open fun requestAnimationFrame(callback: (Double) -> Unit) {
            window.requestAnimationFrame(callback)
        }

        private fun ensureExtension(name: String, required: Boolean): Boolean {
            val extension = webgl.getExtension(name)
            if (required && extension == null) {
                window.alert("$name not supported")
                throw Exception("$name not supported")
            }
            return extension != null
        }
    }
}

abstract external class WebGL2RenderingContext : com.danielgergely.kgl.WebGL2RenderingContext {
    fun fenceSync(condition: Int, flags: Int): WebGLSync
    fun clientWaitSync(sync: WebGLSync, flags: Int, timeout: Number): Int
    fun deleteSync(sync: WebGLSync): WebGLSync
    fun readPixels(x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, offset: Int)
    fun getBufferSubData(target: Int, srcByteOffset: Int, dstData: ArrayBufferView, dstOffset: Int, length: Int)

    companion object {
        val PIXEL_PACK_BUFFER: Int
        val SYNC_GPU_COMMANDS_COMPLETE: Int
        val ALREADY_SIGNALED: Int
        val TIMEOUT_EXPIRED: Int
        val CONDITION_SATISFIED: Int
        val WAIT_FAILED: Int
        val STATIC_READ: Int
        val STREAM_READ: Int
    }
}

external class WebGLSync : WebGLObject
