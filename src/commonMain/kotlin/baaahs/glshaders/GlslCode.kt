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
        override val lineNumber: Int?
    ) : Statement {
        override fun stripSource() = copy(fullText = "", lineNumber = null)
    }

    data class GlslVar(
        val type: String,
        override val name: String,
        override val fullText: String = "",
        val isConst: Boolean = false,
        val isUniform: Boolean = false,
        override val lineNumber: Int? = null
    ) : Statement {
        override fun stripSource() = copy(fullText = "", lineNumber = null)
    }

    data class GlslFunction(
        val returnType: String,
        override val name: String,
        val params: String,
        override val fullText: String,
        override val lineNumber: Int? = null,
        val symbols: Set<String> = emptySet()
    ) : Statement {
        override fun stripSource() = copy(lineNumber = null, symbols = emptySet())
    }

    enum class ContentType(val description: String?) {
        UvCoordinate("U/V Coordinate"),
        XyCoordinate("X/Y Coordinate"),
        XyzCoordinate("X/Y/Z Coordinate"),
        Color("Color"),
        Time("Time"),
        Resolution("Resolution"),
        Unknown(null)
    }

    class Namespace(private val prefix: String) {
        fun qualify(name: String) = "${prefix}_$name"
    }
}