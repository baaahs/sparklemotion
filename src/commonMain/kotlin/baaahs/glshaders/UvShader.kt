package baaahs.glshaders

class UvShader(glslCode: GlslCode) : ShaderFragment.Base(glslCode) {
    companion object {
        val uvCoordsTextureInputPort = InputPort(
            "uvCoordsTexture",
            "sampler2D",
            "U/V Coordinates Texture",
            ContentType.UvCoordinateTexture
        )
        val magicUniforms = listOf(uvCoordsTextureInputPort).associateBy { it.id }
    }

    override val shaderType: ShaderFragment.Type = ShaderFragment.Type.Projection

    override val entryPoint: GlslCode.GlslFunction
            = glslCode.functions.find { it.name == "mainUvFromRaster" }!!

    override val inputPorts: List<InputPort> by lazy {
        glslCode.uniforms.map {
            magicUniforms[it.name]?.copy(type = it.type, glslVar = it)
                ?: InputPort(
                    it.name, it.type, it.name.capitalize(),
                    pluginRef = it.hint?.pluginRef,
                    pluginConfig = it.hint?.config
                )
        }
    }

    override val outputPorts: List<OutputPort>
            = listOf(OutputPort("vec2", "uvCoords", "U/V Coordinate", ContentType.UvCoordinate))

    override fun invocationGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String {
        val buf = StringBuilder()
        buf.append(namespace.qualify(entryPoint.name), "(gl_FragCoord.xy)")
        return buf.toString()
    }
}