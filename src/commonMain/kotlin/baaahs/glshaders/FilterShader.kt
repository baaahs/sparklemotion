package baaahs.glshaders

class FilterShader(glslCode: GlslCode) : OpenShader.Base(glslCode) {
    companion object {
        val wellKnownInputPorts = listOf(
            InputPort("gl_FragColor", "vec4", "Input Color", ContentType.Color),
            InputPort("gl_FragCoord", "vec4", "Coordinates", ContentType.UvCoordinate),
            InputPort("intensity", "float", "Intensity", ContentType.Float), // TODO: ContentType.ZeroToOne
            InputPort("time", "float", "Time", ContentType.Time),
            InputPort("startTime", "float", "Activated Time", ContentType.Time),
            InputPort("endTime", "float", "Deactivated Time", ContentType.Time)
//                        varying vec2 surfacePosition; TODO
        ).associateBy { it.id }
    }

    override val shaderType: OpenShader.Type
        get() = OpenShader.Type.Filter

    override val entryPoint: GlslCode.GlslFunction
        get() = glslCode.functions.find { it.name == "filterImage" }!!

    override val inputPorts: List<InputPort> by lazy {
        glslCode.uniforms.map {
            wellKnownInputPorts[it.name]?.copy(dataType = it.dataType, glslVar = it)
                ?: toInputPort(it)
        }
    }

    override val outputPort: OutputPort
        get() = OutputPort("vec4", "gl_FragColor", "Output Color", ContentType.Color)

    override fun invocationGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String {
        return StringBuilder().apply {
            append(namespace.qualify(entryPoint.name), "()")
        }.toString()
    }
}