package baaahs.glshaders

class GlslCode(
    val title: String,
    val description: String? = null,
    internal val globalVars: List<GlslVar>,
    internal val functions: List<GlslFunction>,
    private val entryPointName: String = "main"
) {
    val globalVarsByName = globalVars.associateBy { it.name }
    val functionsByName = functions.associateBy { it.name }

    fun namespace(namespace: String): GlslCode {
        val symbolNames = HashSet(globalVars.map { it.name } + functions.map { it.name })
        symbolNames.add("gl_FragCoord")
        symbolNames.add("gl_FragColor")

        return GlslCode(
            title,
            description,
            globalVars.map { it.namespaced(namespace) },
            functions.map { it.namespaced(namespace, symbolNames) },
            "${namespace}_$entryPointName"
        )
    }

    internal fun stripSource(): GlslCode {
        return GlslCode(
            title,
            description,
            globalVars.map { it.stripSource() },
            functions.map { it.stripSource() },
            entryPointName
        )
    }

    companion object {
        fun replaceCodeWords(originalText: String, replaceFn: (String) -> String): String {
            val buf = StringBuilder()

            var inComment = false
            Regex("(.*?)(\\w+|//|\n|\\Z)", RegexOption.MULTILINE).findAll(originalText).forEach { matchResult ->
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

    data class GlslVar(
        val type: String,
        val name: String,
        val fullText: String = "",
        val isConst: Boolean = false,
        val isUniform: Boolean = false,
        val lineNumber: Int? = null
    ) {
        fun namespaced(namespace: String) =
            GlslVar(type, "${namespace}_$name", fullText, isConst, isUniform, lineNumber)

        fun toGlsl(namespace: String): String {
            return "${lineNumber?.let { "\n#line $lineNumber\n" }}" +
                    replaceCodeWords(fullText) {
                        if (it == name) "${namespace}_$it" else it
                    }
        }

        fun stripSource() = copy(fullText = "", lineNumber = null)
    }

    data class GlslFunction(
        val returnType: String, val name: String, val params: String, val fullText: String,
        val lineNumber: Int? = null,
        val globalVars: Set<String> = emptySet()
    ) {
        fun namespaced(namespace: String, symbolNames: Set<String>): GlslFunction {
            return GlslFunction(
                returnType,
                "${namespace}_$name",
                params,
                fullText.replace(GlslAnalyzer.wordRegex) { matchResult ->
                    val (word) = matchResult.destructured
                    if (symbolNames.contains(word)) {
                        "${namespace}_$word"
                    } else {
                        word
                    }
                },
                lineNumber,
                globalVars
            )
        }

        fun toGlsl(
            namespace: String,
            globalVarNames: Set<String>,
            portMap: Map<String, String>
        ): String {
            return "${lineNumber?.let { "\n#line $lineNumber\n" }}" +
                    replaceCodeWords(fullText) {
                        when {
                            it == name -> "${namespace}_$it"
                            globalVarNames.contains(it) -> portMap[it] ?: it
                            else -> it
                        }
                    }
        }

        fun stripSource() = copy(lineNumber = null, globalVars = emptySet())
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

}