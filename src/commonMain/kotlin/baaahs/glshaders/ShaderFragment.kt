package baaahs.glshaders

interface ShaderFragment {
    val id: String
    val name: String
    val description: String?
    val shaderType: ShaderType
    val entryPoint: GlslCode.GlslFunction
    val inputPorts: List<InputPort>
    val outputPorts: List<OutputPort>
//    TODO val inputDefaults: Map<String, InputDefault>

    abstract class Base(protected val glslCode: GlslCode) : ShaderFragment {
        override val id: String = glslCode.title
        override val name: String = glslCode.title
        override val description: String? = null
    }

    companion object {
        fun tryColorShader(glslCode: GlslCode): ColorShader? {
            return when {
                glslCode.functionsByName.containsKey("mainImage") ->
                    ShaderToyColorShader(glslCode)

                glslCode.functionsByName.containsKey("main") ->
                    GenericColorShader(glslCode)

                else -> null
            }
        }

    }

    class ShaderToyColorShader(glslCode: GlslCode) : ColorShader(glslCode) {
        override val entryPoint: GlslCode.GlslFunction =
            GlslCode.GlslFunction(
                "void", "sm_main", "",
                "{\n    mainImage(sm_FragColor, sm_FragCoord);\n}"
            )

        override val inputPorts: List<InputPort> by lazy {
            val list = arrayListOf<InputPort>()
            var (iResolution, iTime) = (false to false)
            glslCode.functions.forEach {
                println("body = ${it.body}")
                Regex("\\W(iResolution|iTime)\\W").findAll(it.body).forEach { match ->
                    when (match.groupValues[1]) {
                        "iResolution" -> iResolution = true
                        "iTime" -> iTime = true
                    }
                }
            }
            list.add(InputPort("vec2", "sm_FragCoord", "Coordinates", GlslCode.ContentType.UvCoordinate))
            if (iResolution)
                list.add(InputPort("vec2", "iResolution", "Resolution", GlslCode.ContentType.Resolution))
            if (iTime)
                list.add(InputPort("float", "iTime", "Time", GlslCode.ContentType.Time))

            list.addAll(glslCode.globalVars.map { InputPort(it.type, it.name, null, GlslCode.ContentType.Unknown) })

            list
        }

    }

    class GenericColorShader(glslCode: GlslCode) : ColorShader(glslCode) {
        companion object {
            val magicUniforms = listOf(
                InputPort("vec4", "gl_FragCoord", "Coordinates", GlslCode.ContentType.UvCoordinate),
                InputPort("vec2", "resolution", "Resolution", GlslCode.ContentType.Resolution),
                InputPort("vec2", "mouse", "Mouse", GlslCode.ContentType.Unknown),
                InputPort("float", "time", "Time", GlslCode.ContentType.Time)
            ).associateBy { it.name }
        }

        override val entryPoint: GlslCode.GlslFunction =
            glslCode.functions.find { it.name == "main" }!!

        override val inputPorts: List<InputPort> by lazy {
            glslCode.globalVars.map {
                magicUniforms[it.name]?.copy(type = it.type)
                    ?: InputPort(it.type, it.name, null, GlslCode.ContentType.Unknown)
            } + InputPort("vec4", "gl_FragCoord", "Coordinates", GlslCode.ContentType.UvCoordinate)
        }
    }

    abstract class ColorShader(glslCode: GlslCode) : Base(glslCode) {
        override val shaderType: ShaderType = ShaderType.Color

        override val outputPorts: List<OutputPort>
            get() = TODO("not implemented")

    }
}