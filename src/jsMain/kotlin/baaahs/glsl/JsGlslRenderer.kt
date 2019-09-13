package baaahs.glsl

import baaahs.Color
import baaahs.Surface
import baaahs.getTimeMillis
import baaahs.shaders.GlslShader
import baaahs.timeSync
import de.fabmax.kool.TextureProps
import de.fabmax.kool.createContext
import de.fabmax.kool.defaultProps
import de.fabmax.kool.gl.*
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.createFloat32Buffer
import org.khronos.webgl.*
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

class JsGlslRenderer(
    fragShader: String,
    adjustableValues: List<GlslShader.AdjustableValue>
) : GlslRenderer(createContext(), fragShader, adjustableValues) {
    val canvas = document.createElement("canvas") as HTMLCanvasElement
//    var gl: WebGL2RenderingContext = canvas.getContext("webgl2")!! as WebGL2RenderingContext



    init {

    }
















    companion object {
        val GL = WebGLRenderingContext
        val GL2 = WebGL2RenderingContext
    }




    private val Uniform?.location: WebGLUniformLocation? get() = this?.locationInternal as WebGLUniformLocation?
}

external abstract class WebGL2RenderingContext : WebGLRenderingContext {
    companion object {
        val RED: Int = definedExternally
        val FRAMEBUFFER_INCOMPLETE_ATTACHMENT: Int = definedExternally
        val R32F: Int = definedExternally
        val RG32F: Int = definedExternally
        val DEPTH_COMPONENT32F: Int = definedExternally
    }
}
