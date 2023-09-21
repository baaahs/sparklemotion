package baaahs.gl.preview

import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.RenderEngine

interface ShaderPreview {
    val renderEngine: RenderEngine
    var program: GlslProgram?

    fun start()
    fun stop()
    fun destroy()
    fun render()
    fun resize(width: Int, height: Int)
}