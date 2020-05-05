package baaahs.glshaders

class GlslCode(
    val title: String,
    val description: String? = null,
    glslStatements: List<GlslAnalyzer.GlslStatement>
) {
    internal val globalVarNames = hashSetOf<String>()
    internal val functionNames = hashSetOf<String>()

    val statements = glslStatements.map {
        it.asSpecialOrNull()
            ?: it.asVarOrNull()?.also { glslVar -> globalVarNames.add(glslVar.name) }
            ?: it.asFunctionOrNull()?.also { glslFunction -> functionNames.add(glslFunction.name) }
    }
    val symbolNames = globalVarNames + functionNames
    val globalVars: Collection<GlslVar> get() = statements.filterIsInstance<GlslVar>()
    val uniforms: Collection<GlslVar> get() = globalVars.filter { it.isUniform }
    val functions: Collection<GlslFunction> get() = statements.filterIsInstance<GlslFunction>()

    companion object {
        fun replaceCodeWords(originalText: String, replaceFn: (String) -> String): String {
            val buf = StringBuilder()

            var inComment = false
            Regex("(.*?)(\\w+|//|\n|\\Z|$)", RegexOption.MULTILINE).findAll(originalText).forEach { matchResult ->
                val (before, str) = matchResult.destructured
                buf.append(before)
                when (str) {
                    "//" -> {
                        inComment = true; buf.append(str)
                    }
                    "\n" -> {
                        inComment = false; buf.append(str)
                    }
                    else -> {
                        buf.append(if (inComment) str else replaceFn(str))
                    }
                }
            }
            return buf.toString()
        }
    }

    interface Statement {
        val name: String
        val fullText: String
        val lineNumber: Int?
        val comments: List<String>

        fun stripSource(): Statement

        fun toGlsl(
            namespace: Namespace,
            symbolsToNamespace: Set<String>,
            symbolMap: Map<String, String>
        ): String {
            return "${lineNumber?.let { "\n#line $lineNumber\n" }}" +
                    replaceCodeWords(fullText) {
                        symbolMap[it]
                            ?: if (it == name || symbolsToNamespace.contains(it)) {
                                namespace.qualify(it)
                            } else {
                                it
                            }
                    }
        }
    }

    data class GlslOther(
        override val name: String,
        override val fullText: String,
        override val lineNumber: Int?,
        override val comments: List<String> = emptyList()
    ) : Statement {
        override fun stripSource() = copy(fullText = "", lineNumber = null)
    }

    data class GlslVar(
        val type: String,
        override val name: String,
        override val fullText: String = "",
        val isConst: Boolean = false,
        val isUniform: Boolean = false,
        override val lineNumber: Int? = null,
        override val comments: List<String> = emptyList()
    ) : Statement {
        override fun stripSource() = copy(fullText = "", lineNumber = null)

        val hint: Hint? by lazy {
            val commentString = comments.joinToString(" ") { it.trim() }
            val parts = commentString.split(" ")
            if (parts.isNotEmpty() && parts.first().startsWith("@@")) {
                val type = parts.first().trimStart('@')
                val fullType = Regex("(?:([\\w.]+\\.)?([A-Z]\\w+):)?(\\w+)").matchEntire(type)?.let {
                    val (prefix, pluginClass, arg) = it.destructured
                    "${prefix.ifEmpty { "baaahs." }}${pluginClass .ifEmpty { "Core" }}:$arg"
                } ?: type
                val map = parts.subList(1, parts.size).associate { s ->
                    val kv = s.split("=")
                    kv.first() to kv.subList(1, kv.size).joinToString("=")
                }
                Hint(fullType, map)
            } else null
        }
    }

    data class GlslFunction(
        val returnType: String,
        override val name: String,
        val params: String,
        override val fullText: String,
        override val lineNumber: Int? = null,
        val symbols: Set<String> = emptySet(),
        override val comments: List<String> = emptyList()
    ) : Statement {
        override fun stripSource() = copy(lineNumber = null, symbols = emptySet())
    }

    data class Hint(val plugin: String, val map: Map<String, String>)

    enum class ContentType(val description: String?, val pluginId: String? = null) {
        RasterCoordinate("Raster Coordinate"),
        UvCoordinate("U/V Coordinate"),
        UvCoordinateTexture("U/V Coordinates Texture", "baaahs.Core:uvCoords"),
        XyCoordinate("X/Y Coordinate"),
        XyzCoordinate("X/Y/Z Coordinate"),
        Color("Color", "baaahs.Core:ColorPicker"),
        Time("Time", "baaahs.Core:time"),
        Resolution("Resolution", "baaahs.Core:resolution"),
        Float("Float", "baaahs.Core:Slider"),
        Int("Integer"),
        Unknown("Unknown")
    }

    class Namespace(private val prefix: String) {
        fun qualify(name: String) = "${prefix}_$name"
        fun internalQualify(name: String) = "${prefix}i_$name"
    }
}