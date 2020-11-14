package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.glsl.LinkException
import baaahs.gl.patch.ContentType
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderType

class DistortionShader(shader: Shader, glslCode: GlslCode, plugins: Plugins) : OpenShader.Base(shader, glslCode, plugins) {
    companion object {
        val proFormaInputPorts = listOf(
            InputPort("gl_FragCoord", GlslType.Vec2, "U/V Coordinates", ContentType.UvCoordinateStream)
        )

        val wellKnownInputPorts = listOf(
            InputPort("gl_FragCoord", GlslType.Vec4, "Coordinates", ContentType.UvCoordinateStream),
            InputPort("intensity", GlslType.Float, "Intensity", ContentType.Float), // TODO: ContentType.ZeroToOne
            InputPort("time", GlslType.Float, "Time", ContentType.Time),
            InputPort("startTime", GlslType.Float, "Activated Time", ContentType.Time),
            InputPort("endTime", GlslType.Float, "Deactivated Time", ContentType.Time)
//                        varying vec2 surfacePosition; TODO
        ).associateBy { it.id }

        val outputPort: OutputPort = OutputPort(ContentType.UvCoordinateStream)
    }

    override val shaderType: ShaderType
        get() = ShaderType.Distortion

    override val entryPointName: String get() = "mainDistortion"

    override val proFormaInputPorts
        get() = DistortionShader.proFormaInputPorts
    override val wellKnownInputPorts
        get() = DistortionShader.wellKnownInputPorts
    override val outputPort: OutputPort
        get() = DistortionShader.outputPort

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String {
        val inVar = portMap["gl_FragCoord"] ?: throw LinkException("No input for shader \"$title\"")
        return resultVar + " = " + namespace.qualify(entryPoint.name) + "($inVar.xy)"
    }
}