package baaahs.gl.preview

import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.RenderEngine

interface ShaderPreview {
    val renderEngine: RenderEngine

    fun start()
    fun stop()
    fun destroy()
    fun setProgram(program: GlslProgram?)
    fun getProgram(): GlslProgram?
    fun render()
    fun resize(width: Int, height: Int)
}