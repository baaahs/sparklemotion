package baaahs.gl.shader

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.objectSerializer
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.ui.Icon
import kotlinx.serialization.SerialName

@SerialName("baaahs.Core:Distortion")
object DistortionShader : ShaderPrototype("baaahs.Core:Distortion") {
    override val serializerRegistrar = objectSerializer(id, this)

    override val shaderType: ShaderType
        get() = ShaderType.Distortion

    override val entryPointName: String get() = "mainDistortion"

    override val implicitInputPorts: List<InputPort> = listOf(
        InputPort("gl_FragCoord", GlslType.Vec2, "U/V Coordinates", ContentType.UvCoordinateStream)
    )
    override val title: String = "Distortion"

    override val wellKnownInputPorts = listOf(
        InputPort("intensity", GlslType.Float, "Intensity", ContentType.Float), // TODO: ContentType.ZeroToOne
        InputPort("time", GlslType.Float, "Time", ContentType.Time),
        InputPort("startTime", GlslType.Float, "Activated Time", ContentType.Time),
        InputPort("endTime", GlslType.Float, "Deactivated Time", ContentType.Time)
//                        varying vec2 surfacePosition; TODO
    )

    override val defaultInputPortsByType: Map<Pair<GlslType, Boolean>, InputPort>
        get() = listOf(InputPort("uv", GlslType.Vec2, "Upstream U/V Coordinate", ContentType.UvCoordinateStream))
            .associateBy { it.type to (it.contentType?.isStream ?: false) }

    override val outputPort: OutputPort
        get() = OutputPort(ContentType.UvCoordinateStream)
    override val icon: Icon = CommonIcons.DistortionShader
    override val defaultUpstreams: Map<ContentType, ShaderChannel> =
        mapOf(ContentType.UvCoordinateStream to ShaderChannel.Main)

    override val template: String = """
        uniform float scale; // @@Slider min=0.25 max=4 default=1

        vec2 mainDistortion(vec2 uvIn) {
          return (uvIn - .5) / scale + .5;
        }
    """.trimIndent()
}