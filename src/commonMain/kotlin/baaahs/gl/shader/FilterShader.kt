package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderType

class FilterShader(shader: Shader, glslCode: GlslCode, plugins: Plugins) : OpenShader.Base(shader, glslCode, plugins) {
    companion object {
        val glFragColorInputPort =
            InputPort("gl_FragColor", GlslType.Vec4, "Input Color", ContentType.ColorStream)

        val wellKnownInputPorts = listOf(
            InputPort("gl_FragCoord", GlslType.Vec4, "Coordinates", ContentType.UvCoordinateStream),
            InputPort("intensity", GlslType.Float, "Intensity", ContentType.Float), // TODO: ContentType.ZeroToOne
            InputPort("time", GlslType.Float, "Time", ContentType.Time),
            InputPort("startTime", GlslType.Float, "Activated Time", ContentType.Time),
            InputPort("endTime", GlslType.Float, "Deactivated Time", ContentType.Time)
//                        varying vec2 surfacePosition; TODO
        ).associateBy { it.id }

        val outputPort = OutputPort(ContentType.ColorStream)
    }

    override val shaderType: ShaderType
        get() = ShaderType.Filter

    override val entryPointName: String get() = "mainFilter"

    override val implicitInputPorts: List<InputPort>
        get() = listOf(glFragColorInputPort)

    override val wellKnownInputPorts: Map<String, InputPort>
        get() = FilterShader.wellKnownInputPorts
    override val defaultInputPortsByType: Map<Pair<GlslType, Boolean>, InputPort>
        get() = listOf(InputPort("color", GlslType.Vec4, "Upstream Color", ContentType.ColorStream))
            .associateBy { it.type to (it.contentType?.isStream ?: false) }

    override val outputPort: OutputPort
        get() = FilterShader.outputPort
}