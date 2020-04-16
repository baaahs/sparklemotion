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

    fun toGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String> = emptyMap()): String
    fun invocationGlsl(namespace: GlslCode.Namespace): String

    abstract class Base(protected val glslCode: GlslCode) : ShaderFragment {
        override val id: String = glslCode.title
        override val name: String = glslCode.title
        override val description: String? = null
    }

    companion object {
        fun tryColorShader(glslCode: GlslCode): ColorShader? {
            return when {
                glslCode.functionNames.contains("mainImage") ->
                    ShaderToyColorShader(glslCode)

                glslCode.functionNames.contains("main") ->
                    GenericColorShader(glslCode)

                else -> null
            }
        }

    }

    class ShaderToyColorShader(glslCode: GlslCode) : ColorShader(glslCode) {
        override val entryPoint: GlslCode.GlslFunction =
            glslCode.functions.find { it.name == "mainImage" }!!

        override val inputPorts: List<InputPort> by lazy {
            val list = arrayListOf<InputPort>()
            var (iResolution, iTime) = (false to false)
            glslCode.functions.forEach {
                println("body = ${it.fullText}")
                Regex("\\W(iResolution|iTime)\\W").findAll(it.fullText).forEach { match ->
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
        override val outputPorts: List<OutputPort>
            get() = listOf()

        override fun invocationGlsl(namespace: GlslCode.Namespace): String {
            return "  ${namespace.qualify(entryPoint.name)}(sm_pixelColor, gl_FragCoord.xy);\n"
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
                magicUniforms[it.name]?.copy(type = it.type, glslVar = it)
                    ?: InputPort(it.type, it.name, null, GlslCode.ContentType.Unknown, it)
            } + InputPort("vec4", "gl_FragCoord", "Coordinates", GlslCode.ContentType.UvCoordinate)
        }

        override val outputPorts: List<OutputPort>
            get() = listOf(OutputPort("vec4", "gl_FragColor", "Output Color"))

        override fun invocationGlsl(namespace: GlslCode.Namespace): String {
            val buf = StringBuilder()
            buf.append("  ", namespace.qualify(entryPoint.name), "();\n")
            return buf.toString()
        }
    }

    abstract class ColorShader(glslCode: GlslCode) : Base(glslCode) {
        override val shaderType: ShaderType = ShaderType.Color

        override fun toGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String {
            val buf = StringBuilder()

            val nonUniformGlobalsMap = hashMapOf<String, String>()
            glslCode.globalVars.forEach { glslVar ->
                if (!glslVar.isUniform) {
                    nonUniformGlobalsMap[glslVar.name] = namespace.qualify(glslVar.name)
                    buf.append(glslVar.toGlsl(namespace, glslCode.symbolNames, emptyMap()))
                    buf.append("\n")
                }
            }

//            outputPorts.forEach { outputPort ->
//                buf.append(outputPort.toGlsl(namespace))
//                buf.append("\n")
//            }

            val symbolMap = portMap + nonUniformGlobalsMap

            val symbolsToNamespace = glslCode.symbolNames.toSet()
            glslCode.functions.forEach { glslFunction ->
                buf.append(glslFunction.toGlsl(namespace, symbolsToNamespace, symbolMap))
                buf.append("\n")
            }

            return buf.toString()
        }
    }
}