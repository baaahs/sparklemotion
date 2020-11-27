package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderType

class DistortionShader(shader: Shader, glslCode: GlslCode, plugins: Plugins) : OpenShader.Base(shader, glslCode, plugins) {
    companion object {
        val implicitInputPorts = listOf(
            InputPort("gl_FragCoord", GlslType.Vec2, "U/V Coordinates", ContentType.UvCoordinateStream)
        )

        val wellKnownInputPorts = listOf(
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

    override val implicitInputPorts: List<InputPort>
        get() = DistortionShader.implicitInputPorts
    override val wellKnownInputPorts
        get() = DistortionShader.wellKnownInputPorts
    override val defaultInputPortsByType: Map<Pair<GlslType, Boolean>, InputPort>
        get() = listOf(InputPort("uv", GlslType.Vec2, "Upstream U/V Coordinate", ContentType.UvCoordinateStream))
            .associateBy { it.type to (it.contentType?.isStream ?: false) }
    override val outputPort: OutputPort
        get() = DistortionShader.outputPort
}