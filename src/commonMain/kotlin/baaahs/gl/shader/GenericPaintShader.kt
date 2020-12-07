package baaahs.gl.shader

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.listOf
import baaahs.plugin.objectSerializer
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.ui.Icon
import kotlinx.serialization.SerialName

@SerialName("baaahs.Core:Paint")
object GenericPaintShader : ShaderPrototype("baaahs.Core:Paint") {
    override val serializerRegistrar = objectSerializer(id, this)

    override val implicitInputPorts = listOf(
        InputPort("gl_FragCoord", GlslType.Vec4, "Coordinates", ContentType.UvCoordinateStream)
    )

    override val wellKnownInputPorts = listOf(
            InputPort("resolution", GlslType.Vec2, "Resolution", ContentType.Resolution),
            InputPort("mouse", GlslType.Vec2, "Mouse", ContentType.Mouse),
            InputPort("time", GlslType.Float, "Time", ContentType.Time)
//                        varying vec2 surfacePosition; TODO
        )

    override val defaultUpstreams: Map<ContentType, ShaderChannel> =
        mapOf(ContentType.UvCoordinateStream to ShaderChannel.Main)
    override val title: String = "Paint"

    override val shaderType: ShaderType
        get() = ShaderType.Paint

    override val entryPointName: String get() = "main"
    override val icon: Icon = CommonIcons.PaintShader
    override val template: String = """
        uniform float time;

        void main() {
            gl_FragColor = vec4(gl_FragCoord.x, gl_FragCoord.y, mod(time, 1.), 1.);
        }
    """.trimIndent()


    override val outputPort: OutputPort
        get() = OutputPort(ContentType.ColorStream, "Output Color", "gl_FragColor")

    override fun validate(glslCode: GlslCode): List<GlslError> {
        return super.validate(glslCode) +
                if (!glslCode.refersToGlobal("gl_FragColor"))
                    GlslError("Shader doesn't write to gl_FragColor.", findEntryPoint(glslCode).lineNumber)
                        .listOf()
                else emptyList()
    }
}