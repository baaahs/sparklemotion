package baaahs.gl.glsl

import baaahs.Logger
import baaahs.englishize
import baaahs.plugin.PluginRef
import baaahs.unknown
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json

class GlslCode(
    val src: String,
    glslStatements: List<GlslAnalyzer.GlslStatement>
) {
    internal val globalVarNames = hashSetOf<String>()
    internal val functionNames = hashSetOf<String>()
    internal val structNames = hashSetOf<String>()

    val statements = glslStatements.map {
        it.asSpecialOrNull()
            ?: it.asStructOrNull()
            ?: it.asVarOrNull()?.also { glslVar -> globalVarNames.add(glslVar.name) }
            ?: it.asFunctionOrNull()?.also { glslFunction -> functionNames.add(glslFunction.name) }
            ?: GlslOther("unknown", it.text, it.lineNumber).also {
                if (it.fullText.isNotBlank()) logger.warn { "unrecognized GLSL: ${it.fullText} at ${it.lineNumber}" }
            }
    }
    val symbolNames = globalVarNames + functionNames + structNames
    val globalVars: Collection<GlslVar> get() = statements.filterIsInstance<GlslVar>()
    val globalInputVars: Collection<GlslVar> get() = globalVars.filter { it.isUniform || it.isVarying }
    val uniforms: Collection<GlslVar> get() = globalVars.filter { it.isUniform }
    val functions: Collection<GlslFunction> get() = statements.filterIsInstance<GlslFunction>()
    val structs: Collection<GlslStruct> get() = statements.filterIsInstance<GlslStruct>()

    fun findFunctionOrNull(name: String) =
        functions.find { it.name == name}
    fun findFunction(name: String) =
        findFunctionOrNull(name) ?: error(unknown("function", name, functions.map { it.name }))

    companion object {
        private val logger = Logger("GlslCode")

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

    data class GlslStruct(
        override val name: String,
        override val fullText: String,
        override val lineNumber: Int? = null,
        override val comments: List<String> = emptyList()
    ) : Statement {
        override fun stripSource() = copy(fullText = "", lineNumber = null)
    }

    data class GlslVar(
        val type: GlslType,
        override val name: String,
        override val fullText: String = "",
        val isConst: Boolean = false,
        val isUniform: Boolean = false,
        val isVarying: Boolean = false,
        override val lineNumber: Int? = null,
        override val comments: List<String> = emptyList()
    ) : Statement {
        override fun stripSource() = copy(fullText = "", lineNumber = null)

        val hint: Hint? by lazy { Hint.parse(comments.joinToString(" ") { it.trim() }, lineNumber) }

        fun displayName() = name.englishize()
    }

    class Hint(
        val pluginRef: PluginRef?,
        val config: JsonObject?,
        val tags: Map<String, String>
    ) {
        fun tag(name: String) = tags[name]

        companion object {
            fun parse(commentString: String, lineNumber: Int?): Hint? {
                var pluginRef: PluginRef? = null
                var config: JsonObject? = null
                val tags = mutableMapOf<String, String>()

                Regex("@(@?[^@]+)").findAll(commentString).forEach { match ->
                    val tag = match.groupValues[1].trim()

                    if (tag.startsWith("@")) {
                        val string = commentString.trimStart('@').trim()

                        val parts = string.split(" ")
                        val type = parts.first()
                        pluginRef = Regex("(?:([\\w.]+\\.)?([A-Z]\\w+):)?(\\w+)").matchEntire(type)?.let {
                            val (pluginPackage, pluginClass, resourceName) = it.destructured
                            PluginRef(
                                "${pluginPackage.ifEmpty { "baaahs." }}${pluginClass.ifEmpty { "Core" }}",
                                resourceName
                            )
                        } ?: throw AnalysisException(
                            "don't understand hint: $string",
                            lineNumber ?: -1
                        )

                        config = json {
                            parts.subList(1, parts.size).forEach { s ->
                                val kv = s.split("=")
                                kv.first() to kv.subList(1, kv.size).joinToString("=")
                            }
                        }

                    } else {
                        val parts = tag.split(Regex("\\s"), limit = 2)
                        when (parts.size) {
                            0 -> {} // No-op.
                            1 -> tags[parts[0]] = parts[0]
                            2 -> tags[parts[0]] = parts[1].trim()
                        }
                    }
                }

                return if (pluginRef != null || config != null || tags.isNotEmpty()) {
                    Hint(pluginRef, config, tags)
                } else null
            }
        }
    }

    data class GlslFunction(
        val returnType: GlslType,
        override val name: String,
        val params: String,
        override val fullText: String,
        override val lineNumber: Int? = null,
        val symbols: Set<String> = emptySet(),
        override val comments: List<String> = emptyList()
    ) : Statement {
        override fun stripSource() = copy(lineNumber = null, symbols = emptySet())
    }

    class Namespace(private val prefix: String) {
        fun qualify(name: String) = build(prefix, name)
        fun internalQualify(name: String) = build(prefix + "i", name)

        // Because double underscores are reserved in GLSL.
        private fun build(prefix: String, name: String): String {
            return if (name.startsWith('_')) {
                "${prefix}x$name"
            } else {
                "${prefix}_$name"
            }
        }
    }
}