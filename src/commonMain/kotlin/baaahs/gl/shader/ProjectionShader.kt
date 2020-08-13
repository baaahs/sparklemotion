package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.show.Shader
import baaahs.show.ShaderOutPortRef
import baaahs.show.ShaderType

class ProjectionShader(shader: Shader, glslCode: GlslCode) : OpenShader.Base(shader, glslCode) {
    companion object {
        val pixelCoordsTextureInputPort = InputPort(
            "pixelCoordsTexture",
            "sampler2D",
            "U/V Coordinates Texture",
            ContentType.PixelCoordinatesTexture
        )
        val magicUniforms = listOf(
            pixelCoordsTextureInputPort,
            InputPort(
                "resolution",
                "vec2",
                "Resolution",
                ContentType.Resolution
            ),
            InputPort(
                "previewResolution",
                "vec2",
                "Preview Resolution",
                ContentType.PreviewResolution
            )
        ).associateBy { it.id }
    }

    override val shaderType: ShaderType = ShaderType.Projection

    override val entryPointName: String get() = "mainProjection"

    override val inputPorts: List<InputPort> by lazy {
        glslCode.uniforms.map {
            magicUniforms[it.name]?.copy(dataType = it.dataType, glslVar = it)
                ?: toInputPort(it)
        }
    }

    override val outputPort: OutputPort =
        OutputPort(
            GlslType.Vec2,
            ShaderOutPortRef.ReturnValue,
            "U/V Coordinate",
            ContentType.UvCoordinateStream
        )

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String {
        return resultVar + " = " + namespace.qualify(entryPoint.name) + "(gl_FragCoord.xy)"
    }
}