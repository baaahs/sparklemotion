package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.glsl.LinkException
import baaahs.gl.patch.ContentType
import baaahs.show.Shader
import baaahs.show.ShaderOutPortRef
import baaahs.show.ShaderType

class FilterShader(shader: Shader, glslCode: GlslCode) : OpenShader.Base(shader, glslCode) {
    companion object {
        val proFormaInputPorts = listOf(
            InputPort("gl_FragColor", "vec4", "Input Color", ContentType.ColorStream, varName = "<arg0>")
        )

        val wellKnownInputPorts = listOf(
            InputPort("gl_FragCoord", "vec4", "Coordinates", ContentType.UvCoordinateStream),
            InputPort("intensity", "float", "Intensity", ContentType.Float), // TODO: ContentType.ZeroToOne
            InputPort("time", "float", "Time", ContentType.Time),
            InputPort("startTime", "float", "Activated Time", ContentType.Time),
            InputPort("endTime", "float", "Deactivated Time", ContentType.Time)
//                        varying vec2 surfacePosition; TODO
        ).associateBy { it.id }

        val outputPort =
            OutputPort(GlslType.Vec4, ShaderOutPortRef.ReturnValue, "Output Color", ContentType.ColorStream)
    }

    override val shaderType: ShaderType
        get() = ShaderType.Filter

    override val entryPointName: String get() = "mainFilter"

    override val proFormaInputPorts: List<InputPort>
        get() = FilterShader.proFormaInputPorts
    override val wellKnownInputPorts: Map<String, InputPort>
        get() = FilterShader.wellKnownInputPorts
    override val outputPort: OutputPort
        get() = FilterShader.outputPort

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String {
        val inVar = portMap["gl_FragColor"] ?: throw LinkException("No input for shader \"$title\"")
        return resultVar + " = " + namespace.qualify(entryPoint.name) + "($inVar)"
    }
}