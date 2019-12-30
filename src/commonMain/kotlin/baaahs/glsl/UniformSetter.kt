package baaahs.glsl

import baaahs.Color
import baaahs.shaders.GlslShader

class UniformSetter(program: Program, private val param: GlslShader.Param) {
    private val uniformLocation = program.getUniform(param.varName)

    fun set(value: Any?) {
        if (value != null && uniformLocation != null) {
            when (param.valueType) {
                GlslShader.Param.Type.INT -> uniformLocation.set(value as Int)
                GlslShader.Param.Type.FLOAT -> uniformLocation.set(value as Float)
                GlslShader.Param.Type.VEC3 -> {
                    val color = value as Color
                    uniformLocation.set(color.redF, color.greenF, color.blueF)
                }
            }
        }
    }
}
