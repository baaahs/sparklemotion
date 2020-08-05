package baaahs.glshaders

import baaahs.show.Shader
import baaahs.show.ShaderOutPortRef
import baaahs.show.ShaderType

class UvShader(shader: Shader, glslCode: GlslCode) : OpenShader.Base(shader, glslCode) {
    companion object {
        val pixelCoordsTextureInputPort = InputPort(
            "pixelCoordsTexture",
            "sampler2D",
            "U/V Coordinates Texture",
            ContentType.PixelCoordinatesTexture
        )
        val magicUniforms = listOf(pixelCoordsTextureInputPort).associateBy { it.id }
    }

    override val shaderType: ShaderType = ShaderType.Projection

    override val entryPoint: GlslCode.GlslFunction
            = glslCode.functions.find { it.name == "mainUvFromRaster" }!!

    override val inputPorts: List<InputPort> by lazy {
        glslCode.uniforms.map {
            magicUniforms[it.name]?.copy(dataType = it.dataType, glslVar = it)
                ?: InputPort(
                    it.name, it.dataType, it.name.capitalize(),
                    pluginRef = it.hint?.pluginRef,
                    pluginConfig = it.hint?.config
                )
        }
    }

    override val outputPort: OutputPort = OutputPort("vec2", ShaderOutPortRef.ReturnValue, "U/V Coordinate", ContentType.UvCoordinate)

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String {
        return resultVar + " = " + namespace.qualify(entryPoint.name) + "(gl_FragCoord.xy)"
    }
}