package baaahs.glshaders

class UvShader(glslCode: GlslCode) : ShaderFragment.Base(glslCode) {
    companion object {
        val uvCoordsTextureInputPort = InputPort(
            "sampler2D",
            "uvCoordsTexture",
            "U/V Coordinates Texture",
            GlslCode.ContentType.UvCoordinateTexture
        )
        val magicUniforms = listOf(uvCoordsTextureInputPort).associateBy { it.id }
    }

    override val shaderType: ShaderFragment.Type = ShaderFragment.Type.Projection

    override val entryPoint: GlslCode.GlslFunction
            = glslCode.functions.find { it.name == "mainUvFromRaster" }!!

    override val inputPorts: List<InputPort> by lazy {
        glslCode.uniforms.map {
            magicUniforms[it.name]?.copy(type = it.type, glslVar = it)
                ?: InputPort(it.type, it.name, it.name.capitalize(), GlslCode.ContentType.Float,
                    it.hint?.plugin ?: "baaahs.Core:invalid", it.hint?.map ?: emptyMap(), it)
        }
    }

    override val outputPorts: List<OutputPort>
            = listOf(OutputPort("vec2", "uvCoord", "U/V Coordinate", GlslCode.ContentType.UvCoordinate))

    override fun invocationGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String {
        val buf = StringBuilder()
        buf.append(namespace.qualify(entryPoint.name), "(gl_FragCoord.xy)")
        return buf.toString()
    }
}