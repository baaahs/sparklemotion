package baaahs.glsl

import baaahs.Color
import baaahs.shaders.GlslShader
import com.danielgergely.kgl.*

interface AdjustibleUniform {
    fun bind()
    fun setValue(surfaceOrdinal: Int, value: Any?)
}

class UnifyingAdjustableUniform(
    program: Program,
    private val adjustableValue: GlslShader.AdjustableValue,
    val surfaceCount: Int
) : AdjustibleUniform {
    private val uniformLocation = program.getUniform(adjustableValue.varName)
    var buffer: Any? = null

    override fun bind() {
        if (buffer != null && uniformLocation != null) {
            when (adjustableValue.valueType) {
                GlslShader.AdjustableValue.Type.INT -> uniformLocation.set(buffer as Int)
                GlslShader.AdjustableValue.Type.FLOAT -> uniformLocation.set(buffer as Float)
                GlslShader.AdjustableValue.Type.VEC3 -> {
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

//    val adjustableValueUniformIndices = adjustableValues.map { nextTextureIndex++ }
//class AwesomerAdjustableUniform(
//    gl: Kgl,
//    adjustableValueUniformIndices: Array<Int>,
//    uvCoordsUniform: Uniform,
//    val adjustableValue: GlslShader.AdjustableValue, val surfaceCount: Int) {
//    // TODO: we should save these in an array, one for each surface, but let's keep it simple for now.
//    val elementCount: Int
//        get() = when (adjustableValue.valueType) {
//            GlslShader.AdjustableValue.Type.INT -> surfaceCount
//            GlslShader.AdjustableValue.Type.FLOAT -> surfaceCount
//            GlslShader.AdjustableValue.Type.VEC3 -> surfaceCount * 3
//        }
//
//    val internalFormat: Int
//        get() = when (adjustableValue.valueType) {
//            GlslShader.AdjustableValue.Type.INT -> GL_INT
//            GlslShader.AdjustableValue.Type.FLOAT -> GL_R32F
//            GlslShader.AdjustableValue.Type.VEC3 -> GL_RGB
//        }
//
//    val buffer: ArrayBufferView = when (adjustableValue.valueType) {
//        GlslShader.AdjustableValue.Type.INT -> Uint32Array(elementCount)
//        GlslShader.AdjustableValue.Type.FLOAT -> Float32Array(elementCount)
//        GlslShader.AdjustableValue.Type.VEC3 -> Float32Array(elementCount)
//    }
//
//    val textureIndex = adjustableValueUniformIndices[adjustableValue.ordinal]
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
