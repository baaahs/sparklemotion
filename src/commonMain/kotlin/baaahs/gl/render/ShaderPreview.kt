package baaahs.gl.render

import baaahs.gl.glsl.GlslProgram

interface ShaderPreview {
    val renderEngine: RenderEngine

    fun start()
    fun stop()
    fun destroy()
    fun setProgram(program: GlslProgram)
    fun render()
    fun resize(width: Int, height: Int)
}