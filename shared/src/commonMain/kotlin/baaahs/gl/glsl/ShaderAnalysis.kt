package baaahs.gl.glsl

import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.gl.shader.dialect.GenericShaderDialect
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

class ErrorsShaderAnalysis(
    private val src: String,
    private val e: GlslException,
    override val shader: Shader
) : ShaderAnalysis {
    override val glslCode: GlslCode get() = GlslCode(src, emptyList(), null)
    override val shaderDialect: ShaderDialect get() = GenericShaderDialect
    override val entryPoint: GlslCode.GlslFunction? get() = null
    override val inputPorts: List<InputPort> get() = emptyList()
    override val outputPorts: List<OutputPort> get() = emptyList()
    override val isValid: Boolean get() = false
    override val errors: List<GlslError> get() = e.errors
}