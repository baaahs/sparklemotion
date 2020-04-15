package baaahs.glshaders

class GlslCode(
    val title: String,
    val description: String? = null,
    glslStatements: List<GlslAnalyzer.GlslStatement>,
    private val entryPointName: String = "main"
) {
    internal val globalVarsByName = linkedMapOf<String, GlslVar>()
    internal val functionsByName = linkedMapOf<String, GlslFunction>()

    val statements = glslStatements.map {
        it.asVarOrNull()?.also { glslVar -> globalVarsByName[glslVar.name] = glslVar }
            ?: it.asFunctionOrNull()?.also { glslFunction -> functionsByName[glslFunction.name] = glslFunction }
    }
    val symbolNames = globalVarsByName.keys + functionsByName.keys
    val globalVars: Collection<GlslVar> get() = globalVarsByName.values
    val functions: Collection<GlslFunction> get() = functionsByName.values

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
        fun namespaced(namespace: Namespace, symbolNames: Set<String>): GlslFunction {
            return GlslFunction(
                returnType,
                namespace.qualify(name),
                params,
                fullText.replace(GlslAnalyzer.wordRegex) { matchResult ->
                    val (word) = matchResult.destructured
                    if (symbolNames.contains(word)) {
                        namespace.qualify(word)
                    } else {
                        word
                    }
                },
                lineNumber,
                symbols
            )
        }

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