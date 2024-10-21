package baaahs.gl.param

import baaahs.glsl.TextureUniform

interface ParamBuffer {
    fun resizeBuffer(width: Int, height: Int)
    fun uploadToTexture()
    fun setTexture(uniform: TextureUniform)
    fun release()
}