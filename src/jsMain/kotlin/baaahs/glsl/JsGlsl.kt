package baaahs.glsl

import baaahs.shaders.GlslShader
import com.danielgergely.kgl.KglJs
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document

actual object GlslBase {
    actual val manager: GlslManager by lazy { JsGlslManager() }

    class JsGlslManager : GlslManager {
        override fun createRenderer(fragShader: String, adjustableValues: List<GlslShader.AdjustableValue>): GlslRenderer {
            val contextSwitcher = object : GlslRenderer.ContextSwitcher {
                override fun <T> inContext(fn: () -> T): T = fn()
            }
            return GlslRenderer(createContext(), contextSwitcher, fragShader, adjustableValues, "300 es")
        }

        private fun createContext(): KglJs {
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            val gl = canvas.getContext("webgl2")!!
            return KglJs(gl.asDynamic())
        }
    }
}
