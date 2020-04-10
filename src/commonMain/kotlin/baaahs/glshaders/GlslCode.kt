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
        val isConst: Boolean = false,
        val isUniform: Boolean = false,
        val originalText: String = "",
        val lineNumber: Int? = null
    ) {
        fun namespaced(namespace: String) =
            GlslVar(type, "${namespace}_$name", isConst, isUniform, originalText, lineNumber)

        fun toGlsl(namespace: String): String {
            return "${lineNumber?.let { "\n#line $lineNumber\n" }}" +
                    replaceCodeWords(originalText) {
                        if (it == name) "${namespace}_$it" else it
                    }
        }

        fun stripSource() = copy(originalText = "", lineNumber = null)
    }

    data class GlslFunction(
        val returnType: String, val name: String, val params: String, val body: String,
        val originalText: String = "",
        val lineNumber: Int? = null
    ) {
        fun namespaced(namespace: String, symbolNames: HashSet<String>): GlslFunction {
            return GlslFunction(
                returnType,
                "${namespace}_$name",
                params,
                body.replace(GlslAnalyzer.wordRegex) { matchResult ->
                    val (word) = matchResult.destructured
                    if (symbolNames.contains(word)) {
                        "${namespace}_$word"
                    } else {
                        word
                    }
                },
                originalText,
                lineNumber
            )
        }

        fun toGlsl(namespace: String, globalVarNames: Set<String>): String {
            return "${lineNumber?.let { "\n#line $lineNumber\n" }}" +
                    replaceCodeWords(originalText) {
                        if (it == name || globalVarNames.contains(it)) "${namespace}_$it" else it
                    }
        }

        fun stripSource() = copy(originalText = "", lineNumber = null)
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