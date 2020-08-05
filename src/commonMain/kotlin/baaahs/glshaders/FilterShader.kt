package baaahs.glshaders

import baaahs.show.Shader
import baaahs.show.ShaderOutPortRef
import baaahs.show.ShaderType

class FilterShader(shader: Shader, glslCode: GlslCode) : OpenShader.Base(shader, glslCode) {
    companion object {
        val wellKnownInputPorts = listOf(
            InputPort("gl_FragCoord", "vec4", "Coordinates", ContentType.UvCoordinate),
            InputPort("intensity", "float", "Intensity", ContentType.Float), // TODO: ContentType.ZeroToOne
            InputPort("time", "float", "Time", ContentType.Time),
            InputPort("startTime", "float", "Activated Time", ContentType.Time),
            InputPort("endTime", "float", "Deactivated Time", ContentType.Time)
//                        varying vec2 surfacePosition; TODO
        ).associateBy { it.id }
    }

    override val shaderType: ShaderType
        get() = ShaderType.Filter

    override val entryPoint: GlslCode.GlslFunction
        get() = glslCode.functions.find { it.name == "filterImage" }!!

    override val inputPorts: List<InputPort> by lazy {
        listOf(InputPort("gl_FragColor", "vec4", "Input Color", ContentType.Color, varName = "<arg0>")) +
                glslCode.uniforms.map {
                    wellKnownInputPorts[it.name]?.copy(dataType = it.dataType, glslVar = it)
                        ?: toInputPort(it)
                }
    }

    override val outputPort: OutputPort
        get() = OutputPort("vec4", ShaderOutPortRef.ReturnValue, "Output Color", ContentType.Color)

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String {
        return resultVar + " = " + namespace.qualify(entryPoint.name) + "(${portMap["gl_FragColor"]})"
    }
}