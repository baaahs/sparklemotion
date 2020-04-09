package baaahs.glshaders

interface ShaderFragment {
    val id: String
    val name: String
    val description: String?
    val shaderType: ShaderType
    val entryPoint: GlslCode.GlslFunction
    val inputPorts: List<GlslCode.InputPort>
    val outputPorts: List<GlslCode.OutputPort>
//    TODO val inputDefaults: Map<String, InputDefault>

    abstract class Base(protected val glslCode: GlslCode) : ShaderFragment {
        override val id: String = glslCode.title
        override val name: String = glslCode.title
        override val description: String? = null
    }

    companion object {
        fun tryColorShader(glslCode: GlslCode): ColorShader? {
            if (glslCode.functionsByName.containsKey("mainImage")) {
                ShaderToyColorShader(glslCode)?.let { return it }
            }

            if (glslCode.functionsByName.containsKey("main")) {
                GenericColorShader(glslCode)?.let { return it }
            }

            return null
        }

    }

    class ShaderToyColorShader(glslCode: GlslCode) : ColorShader(glslCode) {
        override val entryPoint: GlslCode.GlslFunction =
            GlslCode.GlslFunction(
                "void", "sm_main", "",
                "{\n    mainImage(sm_FragColor, sm_FragCoord);\n}"
            )

        override val inputPorts: List<GlslCode.InputPort> by lazy {
            val list = arrayListOf<GlslCode.InputPort>()
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
            list.add(GlslCode.InputPort("vec2", "sm_FragCoord", "Coordinates", GlslCode.ContentType.UvCoordinate))
            if (iResolution)
                list.add(GlslCode.InputPort("vec2", "iResolution", "Resolution", GlslCode.ContentType.Resolution))
            if (iTime)
                list.add(GlslCode.InputPort("float", "iTime", "Time", GlslCode.ContentType.Time))

            list
        }

    }

    class GenericColorShader(glslCode: GlslCode) : ColorShader(glslCode) {
        override val entryPoint: GlslCode.GlslFunction =
            glslCode.functions.find { it.name == "main" }!!

        override val inputPorts: List<GlslCode.InputPort> by lazy {
            TODO()
        }
    }

    abstract class ColorShader(glslCode: GlslCode) : Base(glslCode) {
        override val shaderType: ShaderType = ShaderType.Color

        override val outputPorts: List<GlslCode.OutputPort>
            get() = TODO("not implemented")

    }
}