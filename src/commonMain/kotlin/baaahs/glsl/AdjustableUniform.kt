package baaahs.glsl

import baaahs.Color
import baaahs.shaders.GlslShader

interface AdjustableUniform {
    fun bind()
    fun setValue(surfaceOrdinal: Int, value: Any?)
}

class UnifyingAdjustableUniform(
    program: Program,
    private val param: GlslShader.Param,
    val surfaceCount: Int
) : AdjustableUniform {
    private val uniformLocation = program.getUniform(param.varName)
    var buffer: Any? = null

    override fun bind() {
        if (buffer != null && uniformLocation != null) {
            when (param.valueType) {
                GlslShader.Param.Type.INT -> uniformLocation.set(buffer as Int)
                GlslShader.Param.Type.FLOAT -> uniformLocation.set(buffer as Float)
                GlslShader.Param.Type.VEC3 -> {
                    val color = buffer as Color
                    uniformLocation.set(color.redF, color.greenF, color.blueF)
                }
            }
        }
    }

    // last one wins!
    override fun setValue(surfaceOrdinal: Int, value: Any?) {
        buffer = value
    }
}

//    val paramUniformIndices = params.map { nextTextureIndex++ }
//class AwesomerAdjustableUniform(
//    gl: Kgl,
//    paramUniformIndices: Array<Int>,
//    uvCoordsUniform: Uniform,
//    val param: GlslShader.Param, val surfaceCount: Int) {
//    // TODO: we should save these in an array, one for each surface, but let's keep it simple for now.
//    val elementCount: Int
//        get() = when (param.valueType) {
//            GlslShader.Param.Type.INT -> surfaceCount
//            GlslShader.Param.Type.FLOAT -> surfaceCount
//            GlslShader.Param.Type.VEC3 -> surfaceCount * 3
//        }
//
//    val internalFormat: Int
//        get() = when (param.valueType) {
//            GlslShader.Param.Type.INT -> GL_INT
//            GlslShader.Param.Type.FLOAT -> GL_R32F
//            GlslShader.Param.Type.VEC3 -> GL_RGB
//        }
//
//    val buffer: ArrayBufferView = when (param.valueType) {
//        GlslShader.Param.Type.INT -> Uint32Array(elementCount)
//        GlslShader.Param.Type.FLOAT -> Float32Array(elementCount)
//        GlslShader.Param.Type.VEC3 -> Float32Array(elementCount)
//    }
//
//    val textureIndex = paramUniformIndices[param.ordinal]
//    var texture = TextureResource.create(
//        GL_TEXTURE0 + textureIndex,
//        TextureProps("", GL_NEAREST, GL_NEAREST), gl
//    )
//
//    init {
//        gl.activeTexture(texture.target)
//        gl.bindTexture(GL_TEXTURE_2D, texture)
//        gl.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
//        gl.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
//
//        gl.texImage2D(
//            GL_TEXTURE_2D, 0,
//            GL_R32F, elementCount, 1, 0,
//            GL_RED,
//            GL_FLOAT, null
//        )
//        uvCoordsUniform.set(textureIndex)
//    }
//}
