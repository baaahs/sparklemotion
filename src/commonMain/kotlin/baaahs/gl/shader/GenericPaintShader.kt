package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.Plugins
import baaahs.show.Shader

class GenericPaintShader(
    shader: Shader,
    glslCode: GlslCode,
    plugins: Plugins
) : PaintShader(shader, glslCode, plugins) {
    companion object {
        val proFormaInputPorts = listOf(
            InputPort("gl_FragCoord", GlslType.Vec4, "Coordinates", ContentType.UvCoordinateStream)
        )

        val wellKnownInputPorts = listOf(
            InputPort("resolution", GlslType.Vec2, "Resolution", ContentType.Resolution),
            InputPort("mouse", GlslType.Vec2, "Mouse", ContentType.Mouse),
            InputPort("time", GlslType.Float, "Time", ContentType.Time)
//                        varying vec2 surfacePosition; TODO
        ).associateBy { it.id }

        val outputPort =
            OutputPort(ContentType.ColorStream, "Output Color", "gl_FragColor")
    }

    override val proFormaInputPorts: List<InputPort>
        get() = GenericPaintShader.proFormaInputPorts
    override val wellKnownInputPorts: Map<String, InputPort>
        get() = GenericPaintShader.wellKnownInputPorts
    override val outputPort: OutputPort
        get() = GenericPaintShader.outputPort

    override val entryPointName: String get() = "main"

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String {
        return namespace.qualify(entryPoint.name) + "()"
    }
}