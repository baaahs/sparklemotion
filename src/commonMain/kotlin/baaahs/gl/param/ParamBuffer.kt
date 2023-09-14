package baaahs.gl.param

import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.glsl.GlslUniform

interface ParamBuffer {
    fun resizeBuffer(width: Int, height: Int)
    fun uploadToTexture()
    fun setTexture(uniform: GlslUniform)
    fun bind(glslProgram: GlslProgram): ProgramFeedContext
    fun release()
}