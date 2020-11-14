package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderType

class ProjectionShader(shader: Shader, glslCode: GlslCode, plugins: Plugins) : OpenShader.Base(shader, glslCode, plugins) {
    companion object {
        val proFormaInputPorts = listOf<InputPort>()

        val wellKnownInputPorts = listOf(
            InputPort("pixelCoordsTexture", GlslType.Sampler2D, "U/V Coordinates Texture", ContentType.PixelCoordinatesTexture),
            InputPort("resolution", GlslType.Vec2, "Resolution", ContentType.Resolution),
            InputPort("previewResolution", GlslType.Vec2, "Preview Resolution", ContentType.PreviewResolution)
        ).associateBy { it.id }

        val outputPort = OutputPort(ContentType.UvCoordinateStream)

    }

    override val shaderType: ShaderType = ShaderType.Projection

    override val entryPointName: String
        get() = "mainProjection"
    override val proFormaInputPorts: List<InputPort>
        get() = ProjectionShader.proFormaInputPorts
    override val wellKnownInputPorts: Map<String, InputPort>
        get() = ProjectionShader.wellKnownInputPorts
    override val outputPort: OutputPort
        get() = ProjectionShader.outputPort


    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String {
        return resultVar + " = " + namespace.qualify(entryPoint.name) + "(gl_FragCoord.xy)"
    }
}