package baaahs.glshaders

class ShaderFragment(
    val title: String,
    val globalVars: List<GlslVar>,
    val functions: List<GlslFunction>,
    val entryPointName: String = "main"
) {
    val entryPoint: GlslFunction get() = functions.first { it.name == entryPointName }

    fun namespace(namespace: String): ShaderFragment {
        val symbolNames = HashSet(globalVars.map { it.name } + functions.map { it.name })
        return ShaderFragment(
            title,
            globalVars.map { it.namespaced(namespace) },
            functions.map { it.namespaced(namespace, symbolNames) },
            "${namespace}_$entryPointName"
        )
    }

    internal fun sansLineNumbers(): ShaderFragment {
        return ShaderFragment(
            title,
            globalVars.map { it.copy(lineNumber = null) },
            functions.map { it.copy(lineNumber = null) },
            entryPointName
        )
    }

    enum class Qualifier {

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
}