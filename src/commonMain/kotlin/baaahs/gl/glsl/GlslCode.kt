package baaahs.gl.glsl

import baaahs.englishize
import baaahs.gl.shader.InputPort
import baaahs.plugin.PluginRef
import baaahs.plugin.Plugins
import baaahs.unknown
import baaahs.util.Logger
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class GlslCode(
    val src: String,
    private val statements: List<GlslStatement>
) {
    val globalVarNames = hashSetOf<String>()
    val functionNames = hashSetOf<String>()
    val structsByName = mutableMapOf<String, GlslStruct>()

    init {
        statements.forEach {
            when (it) {
                is GlslStruct -> {
                    structsByName[it.name] = it
                    it.varName?.let { varName -> globalVarNames += varName }
                }
                is GlslVar -> globalVarNames.add(it.name)
                is GlslFunction -> functionNames.add(it.name)
                else -> {
                    if (it.fullText.isNotBlank()) {
                        logger.warn { "unrecognized GLSL: ${it.fullText} at ${it.lineNumber}" }
                    }
                }
            }
        }
    }
    val symbolNames = globalVarNames + functionNames + structsByName.keys
    val globalVars: Collection<GlslVar> get() =
        statements.filterIsInstance<GlslVar>() +
                structs.filter { it.varName != null }
                    .map { it.getSyntheticVar() }
    val globalInputVars: Collection<GlslVar> get() = globalVars.filter { it.isUniform || it.isVarying }
    val uniforms: Collection<GlslVar> get() = globalVars.filter { it.isUniform }
    val functions: Collection<GlslFunction> get() = statements.filterIsInstance<GlslFunction>()
    val structs: Collection<GlslStruct> get() = statements.filterIsInstance<GlslStruct>()

    fun findFunctionOrNull(name: String) =
        functions.find { it.name == name}

    fun findFunction(name: String) =
        findFunctionOrNull(name)
            ?: error(unknown("function", name, functions.map { it.name }))

    // TODO: We ought to ignore e.g. local variables, or strings that only appear in comments.
    fun refersToGlobal(name: String): Boolean {
        return src.contains(name)
    }

    companion object {
        private val logger = Logger<GlslCode>()

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

    interface GlslStatement {
        val name: String
        val fullText: String
        val lineNumber: Int?
        val comments: List<String>

        fun stripSource(): GlslStatement

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
    ) : GlslStatement {
        override fun stripSource() = copy(fullText = "", lineNumber = null)
    }

    data class GlslStruct(
        override val name: String,
        val fields: Map<String, GlslType>,
        val varName: String?,
        val isUniform: Boolean = false,
        override val fullText: String,
        override val lineNumber: Int? = null,
        override val comments: List<String> = emptyList()
    ) : GlslStatement {
        override fun stripSource() = copy(fullText = "", lineNumber = null)

        fun getSyntheticVar(): GlslVar {
            val structType = GlslType.Struct(this)
            val fullText = "${if (isUniform) "uniform " else ""}$name $varName;"
            return GlslVar(varName!!, structType, fullText, lineNumber = lineNumber, comments = comments)
        }
    }

    interface GlslArgSite {
        val name: String
        val title: String
        val type: GlslType
        val isVarying: Boolean
        val isGlobalInput: Boolean
        val hint: Hint?

        fun toInputPort(plugins: Plugins): InputPort {
            return InputPort(
                name, type, title,
                pluginRef = hint?.pluginRef,
                pluginConfig = hint?.config,
                contentType = hint?.contentType(plugins),
                glslArgSite = this
            )
        }
    }

    data class GlslVar(
        override val name: String,
        override val type: GlslType,
        override val fullText: String = "",
        val isConst: Boolean = false,
        val isUniform: Boolean = false,
        override val isVarying: Boolean = false,
        override val lineNumber: Int? = null,
        override val comments: List<String> = emptyList()
    ) : GlslStatement, GlslArgSite {
        override val title get() = name.englishize()
        override val isGlobalInput: Boolean get() = isUniform || isVarying
        override val hint: Hint? by lazy { Hint.parse(comments.joinToString(" ") { it.trim() }, lineNumber) }

        override fun stripSource() = copy(fullText = "", lineNumber = null)
    }

    class Hint(
        val pluginRef: PluginRef?,
        val config: JsonObject?,
        val tags: Map<String, String>
    ) {
        fun tag(name: String) = tags[name]

        fun contentType(plugins: Plugins) =
            tag("type")?.let { plugins.resolveContentType(it) }

        companion object {
            fun from(comments: List<String>, lineNumber: Int? = null): Hint? =
                parse(comments.joinToString(" ") { it.trim() }, lineNumber)

            fun parse(commentString: String, lineNumber: Int? = null): Hint? {
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

                        config = buildJsonObject {
                            parts.subList(1, parts.size).forEach { s ->
                                val kv = s.split("=")
                                put(kv.first(), kv.subList(1, kv.size).joinToString("="))
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
        override val name: String,
        val returnType: GlslType,
        val params: List<GlslParam>,
        override val fullText: String,
        override val lineNumber: Int? = null,
        override val comments: List<String> = emptyList()
    ) : GlslStatement {
        val hint: Hint? by lazy { Hint.from(comments, lineNumber) }

        override fun stripSource() = copy(lineNumber = null)

        fun invocationGlsl(namespace: Namespace, resultVar: String, portMap: Map<String, String>): String {
            val assignment = if (returnType != GlslType.Void) {
                "$resultVar = "
            } else ""

            val args = params.joinToString(", ") { glslParam ->
                if (glslParam.isOut)
                    resultVar
                else
                    portMap[glslParam.name]
                        ?: "/* huh? ${glslParam.name} */"
            }

            return assignment + namespace.qualify(name) + "($args)"
        }
    }

    data class GlslParam(
        override val name: String,
        override val type: GlslType,
        val isIn: Boolean = false,
        val isOut: Boolean = false,
        val lineNumber: Int? = null,
        val comments: List<String> = emptyList()
    ) : GlslArgSite {
        override val title: String get() = name.englishize()
        override val isVarying: Boolean get() = true
        override val isGlobalInput: Boolean get() = false
        override val hint: Hint? by lazy { Hint.from(comments, lineNumber) }
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