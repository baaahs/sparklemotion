package baaahs.shaders

import baaahs.Surface
import baaahs.glshaders.GlslProgram
import baaahs.glsl.GlslBase
import baaahs.glsl.GlslContext
import baaahs.glsl.UvTranslator
import kotlin.js.JsName

class GlslShader(
    override val glslProgram: GlslProgram,
    val uvTranslator: UvTranslator,
    private val renderContext: GlslContext = globalRenderContext
) : IGlslShader {

    companion object {
        @JsName("globalRenderContext")
        val globalRenderContext by lazy { GlslBase.manager.createContext() }
    }

    class Buffer(val shader: IGlslShader, val surface: Surface) {
//        val values = Array<Any?>(patch.uniformInputs.size) { }

        fun release() {
//            TODO("not implemented")
        }
    }

}

interface IGlslShader {
    val glslProgram: GlslProgram

    fun createBuffer(surface: Surface): GlslShader.Buffer = GlslShader.Buffer(this, surface)
}