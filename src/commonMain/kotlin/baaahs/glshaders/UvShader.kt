package baaahs.glshaders

import baaahs.show.Shader

class UvShader(glslCode: GlslCode) : OpenShader.Base(glslCode) {
    companion object {
        val pixelCoordsTextureInputPort = InputPort(
            "pixelCoordsTexture",
            "sampler2D",
            "U/V Coordinates Texture",
            ContentType.PixelCoordinatesTexture
        )
        val magicUniforms = listOf(pixelCoordsTextureInputPort).associateBy { it.id }
    }

    override val shaderType: OpenShader.Type = OpenShader.Type.Projection

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

    override val outputPorts: List<OutputPort> = listOf(
        OutputPort(Shader.ReturnValue, "vec2", "U/V Coordinate", ContentType.UvCoordinate),
        OutputPort("sm_RasterCoord", "vec4", "Raster Coordinate", ContentType.RasterCoordinate,
            synthetic = "vec4(${Shader.ReturnValue}, 0., 0.)")
    )

    override fun invocationGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String {
        val buf = StringBuilder()
        buf.append(namespace.qualify(entryPoint.name), "(gl_FragCoord.xy)")
        return buf.toString()
    }
}