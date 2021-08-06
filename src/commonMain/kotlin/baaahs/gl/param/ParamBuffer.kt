package baaahs.gl.param

import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.glsl.Uniform

interface ParamBuffer {
    fun resizeBuffer(width: Int, height: Int)
    fun uploadToTexture()
    fun setTexture(uniform: Uniform)
    fun bind(glslProgram: GlslProgram): ProgramFeed
    fun release()
}