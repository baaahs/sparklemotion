package baaahs.gl.param

import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram

interface ParamBuffer {
    fun resizeBuffer(width: Int, height: Int)
    fun uploadToTexture()
    fun setTexture(uniform: GlslProgram.UniformTextureUnit)
    fun bind(glslProgram: GlslProgram): ProgramFeed
    fun release()
}