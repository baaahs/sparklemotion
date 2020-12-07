package baaahs.show

import baaahs.gl.glsl.GlslCode
import baaahs.gl.shader.*
import baaahs.show.mutable.MutableShader

enum class ShaderType(
    val priority: Int,
    val prototype: ShaderPrototype
) {
    Unknown(0, GenericShaderPrototype),
    Projection(0, ProjectionShader),
    Distortion(1, DistortionShader),
    Paint(3, GenericPaintShader),
    Filter(4, FilterShader),
    Mover(0, MoverShader);

    fun newShaderFromTemplate(): MutableShader {
        return prototype.newShaderFromTemplate()
    }

    fun matches(glslCode: GlslCode): MatchLevel {
        return prototype.matches(glslCode)
    }
}