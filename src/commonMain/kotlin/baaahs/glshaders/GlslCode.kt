package baaahs.glshaders

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json

class GlslCode(
    val title: String,
    val description: String? = null,
    val src: String,
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
        val dataType: String,
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
            if (commentString.startsWith("@@")) {
                Hint(commentString.trimStart('@').trim())
            } else {
                null
            }
//            val parts = commentString.split(" ")
//            if (parts.isNotEmpty() && parts.first().startsWith("@@")) {
//            } else null
        }
    }

    class Hint(string: String) {
        val pluginRef: PluginRef
        val config: JsonObject

        init {
            val parts = string.split(" ")
            val type = parts.first()
            pluginRef = Regex("(?:([\\w.]+\\.)?([A-Z]\\w+):)?(\\w+)").matchEntire(type)?.let {
                val (pluginPackage, pluginClass, resourceName) = it.destructured
                PluginRef(
                    "${pluginPackage.ifEmpty { "baaahs." }}${pluginClass.ifEmpty { "Core" }}",
                    resourceName)
            } ?: error("don't understand hint: $string")

            config = json {
                parts.subList(1, parts.size).forEach { s ->
                    val kv = s.split("=")
                    kv.first() to kv.subList(1, kv.size).joinToString("=")
                }
            }
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

    class Namespace(val prefix: String) {
        fun qualify(name: String) = "${prefix}_$name"
        fun internalQualify(name: String) = "${prefix}i_$name"
    }
}