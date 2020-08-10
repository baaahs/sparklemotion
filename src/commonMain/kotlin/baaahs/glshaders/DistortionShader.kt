package baaahs.glshaders

import baaahs.glsl.LinkException
import baaahs.show.Shader
import baaahs.show.ShaderOutPortRef
import baaahs.show.ShaderType

class DistortionShader(shader: Shader, glslCode: GlslCode) : OpenShader.Base(shader, glslCode) {
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
        get() = ShaderType.Distortion

    override val entryPointName: String get() = "mainDistortion"

    override val inputPorts: List<InputPort> by lazy {
        listOf(InputPort("gl_FragCoord", "vec2", "U/V Coordinatess", ContentType.UvCoordinate)) +
                glslCode.uniforms.map {
                    wellKnownInputPorts[it.name]?.copy(dataType = it.dataType, glslVar = it)
                        ?: toInputPort(it)
                }
    }

    override val outputPort: OutputPort =
        OutputPort(GlslType.Vec2, ShaderOutPortRef.ReturnValue, "U/V Coordinate", ContentType.UvCoordinate)

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String {
        val inVar = portMap["gl_FragCoord"] ?: throw LinkException("No input for shader \"$title\"")
        return resultVar + " = " + namespace.qualify(entryPoint.name) + "($inVar.xy)"
    }
}