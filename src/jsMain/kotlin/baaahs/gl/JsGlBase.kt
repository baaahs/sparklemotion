package baaahs.gl

import baaahs.document
import baaahs.window
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.KglJs
import com.danielgergely.kgl.WebGL2RenderingContext
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

        override fun createContext(): GlContext {
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            return createContext(canvas)
        }

        fun createContext(canvas: HTMLCanvasElement): JsGlContext {
            val webgl = canvas.getContext("webgl2") as WebGL2RenderingContext?
            if (webgl == null) {
                window.alert(
                    "Running GLSL shows on iOS requires WebGL 2.0.\n" +
                            "\n" +
                            "Go to Settings → Safari → Advanced → Experimental Features and enable WebGL 2.0."
                )
                throw Exception("WebGL 2 not supported")
            }
            return JsGlContext(KglJs(webgl), "300 es", webgl)
        }
    }

    class JsGlContext(
        kgl: Kgl,
        glslVersion: String,
        internal val webgl: WebGL2RenderingContext
    ) : GlContext(kgl, glslVersion) {
        override fun <T> runInContext(fn: () -> T): T = fn()
    }
}
