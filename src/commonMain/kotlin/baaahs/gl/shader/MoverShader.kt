package baaahs.gl.shader

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.objectSerializer
import baaahs.show.ShaderType
import baaahs.ui.Icon
import kotlinx.serialization.SerialName

@SerialName("baaahs.Core:Mover")
object MoverShader : ShaderPrototype("baaahs.Core:Mover") {
    override val serializerRegistrar = objectSerializer(id, this)

    override val shaderType: ShaderType
        get() = ShaderType.Mover

    override val entryPointName: String get() = "mainMover"
    override val icon: Icon = CommonIcons.None
    override val title: String = "Mover"
    override val wellKnownInputPorts = listOf(
        InputPort("position", GlslType.Vec3, "Position", ContentType.XyzCoordinate),
        InputPort("orientation", GlslType.Vec3, "Orientation", ContentType.XyzCoordinate),
        InputPort("time", GlslType.Float, "Time", ContentType.Time)
    )

    override val outputPort: OutputPort
        get() = OutputPort(ContentType.PanAndTilt)

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>,
        entryPoint: GlslCode.GlslFunction
    ): String {
        return resultVar + " = " + namespace.qualify(entryPoint.name) + "()"
    }

    override val template: String = """
        vec4 mainMover() {
            return vec4(0., .5);
        }
    """.trimIndent()
}