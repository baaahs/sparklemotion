package baaahs.gl.glsl

import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.gl.shader.dialect.ShaderDialect
import baaahs.show.Shader

interface ShaderAnalysis {
    val glslCode: GlslCode

    val shaderDialect: ShaderDialect

    val entryPoint: GlslCode.GlslFunction?

    val inputPorts: List<InputPort>

    val outputPorts: List<OutputPort>

    val isValid: Boolean

    val errors: List<GlslError>

    val shader: Shader
}