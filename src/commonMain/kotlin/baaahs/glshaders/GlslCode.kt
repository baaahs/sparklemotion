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

    internal fun sansLineNumbers(): GlslCode {
        return GlslCode(
            title,
            description,
            globalVars.map { it.copy(lineNumber = null) },
            functions.map { it.copy(lineNumber = null) },
            entryPointName
        )
    }

    data class GlslVar(
        val type: String,
        val name: String,
        val isConst: Boolean = false,
        val isUniform: Boolean = false,
        val lineNumber: Int? = null
    ) {
        fun namespaced(namespace: String) =
            GlslVar(type, "${namespace}_$name", isConst, isUniform, lineNumber)
    }

    data class GlslFunction(
        val returnType: String, val name: String, val params: String, val body: String,
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
                }
            )
        }
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

    data class InputPort(
        val type: String,
        val name: String,
        val description: String?,
        val contentType: ContentType
    )

    data class OutputPort(
        val type: String,
        val name: String,
        val description: String?
    )
}