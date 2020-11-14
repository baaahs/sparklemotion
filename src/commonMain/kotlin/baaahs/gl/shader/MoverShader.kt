package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderType

class MoverShader(shader: Shader, glslCode: GlslCode, plugins: Plugins) : OpenShader.Base(shader, glslCode, plugins) {
    companion object {
        val proFormaInputPorts = listOf(
            InputPort("gl_FragColor", GlslType.Vec4, "Input Color", ContentType.ColorStream)
        )

        val wellKnownInputPorts = listOf(
            InputPort("position", GlslType.Vec3, "Position", ContentType.XyzCoordinate),
            InputPort("orientation", GlslType.Vec3, "Orientation", ContentType.XyzCoordinate),
            InputPort("time", GlslType.Float, "Time", ContentType.Time)
        ).associateBy { it.id }

        val outputPort = OutputPort(ContentType.PanAndTilt)
    }

    override val shaderType: ShaderType
        get() = ShaderType.Mover

    override val entryPointName: String get() = "mainMover"

    override val proFormaInputPorts: List<InputPort>
        get() = MoverShader.proFormaInputPorts
    override val wellKnownInputPorts: Map<String, InputPort>
        get() = MoverShader.wellKnownInputPorts
    override val outputPort: OutputPort
        get() = MoverShader.outputPort

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String {
        return resultVar + " = " + namespace.qualify(entryPoint.name) + "()"
    }
}