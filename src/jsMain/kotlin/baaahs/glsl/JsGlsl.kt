package baaahs.glsl

import baaahs.shaders.GlslShader
import com.danielgergely.kgl.KglJs
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

actual object GlslBase {
    actual val plugins: MutableList<GlslPlugin> = mutableListOf()
    actual val manager: GlslManager by lazy { JsGlslManager() }

    class JsGlslManager : GlslManager {
        override val available: Boolean by lazy {
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            val gl = canvas.getContext("webgl")
            gl != null
        }

        override fun createRenderer(
            fragShader: String,
            uvTranslator: UvTranslator,
            params: List<GlslShader.Param>,
            plugins: List<GlslPlugin>
        ): GlslRenderer {
            val contextSwitcher = object : GlslRenderer.ContextSwitcher {
                override fun <T> inContext(fn: () -> T): T = fn()
            }
            return GlslRenderer(createContext(), contextSwitcher, fragShader, uvTranslator, params, "300 es", plugins)
        }

        private fun createContext(): KglJs {
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            val gl = canvas.getContext("webgl2")
            if (gl == null) {
                window.alert("Running GLSL shows on iOS requires WebGL 2.0.\n" +
                        "\n" +
                        "Go to Settings → Safari → Advanced → Experimental Features and enable WebGL 2.0.")
                throw Exception("WebGL 2 not supported")
            }
            return KglJs(gl.asDynamic())
        }
    }
}
