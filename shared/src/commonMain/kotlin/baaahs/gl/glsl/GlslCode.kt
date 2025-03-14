package baaahs.gl.glsl

import baaahs.englishize
import baaahs.getValue
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.dialect.findContentType
import baaahs.plugin.PluginRef
import baaahs.plugin.Plugins
import baaahs.unknown
import baaahs.util.Logger
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class GlslCode(
    val src: String,
    val statements: List<GlslStatement>,
    val fileName: String? = null
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
                is GlslOther -> {}
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
    }

    interface StatementRewriter : GlslParser.Tokenizer.State {
        fun substitute(text: String): String

        /** Yuck, this is horrible. */
        fun drain(): String
    }

    class TokenRewriter(
        private val substitutionFn: (String) -> String
    ) : StatementRewriter {
        private val buf: StringBuilder = StringBuilder()

        override fun substitute(text: String): String =
            substitutionFn.invoke(text)

        override fun visit(token: String): GlslParser.Tokenizer.State {
            buf.append(substitute(token))
            return this
        }

        override fun drain(): String =
            buf.toString().also { buf.clear() }
    }

    interface GlslStatement {
        val name: String
        val fullText: String
        val lineNumber: Int?
        val comments: List<String>

        fun toGlsl(fileNumber: Int?, statementRewriter: StatementRewriter): String {
            return substituteGlsl(fullText, statementRewriter, fileNumber)
        }

        fun substituteGlsl(text: String, statementRewriter: StatementRewriter, fileNumber: Int?): String = buildString {
            if (lineNumber != null) {
                append("\n#line $lineNumber${fileNumber?.let { " $it" } ?: ""}")
            }

            GlslParser.Tokenizer().processTokens(text, statementRewriter)
            append("\n")
            append(statementRewriter.drain())
        }
    }

    data class GlslOther(
        override val name: String,
        override val fullText: String,
        override val lineNumber: Int?,
        override val comments: List<String> = emptyList()
    ) : GlslStatement {
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
        val glslType: GlslType.Struct =
            GlslType.Struct(name, fields.map { (name, type) -> GlslType.Field(name, type) })

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
        val isAbstractFunction: Boolean
        val hint: Hint?
        val lineNumber: Int?

        fun findInjectedData(plugins: Plugins): Map<String, ContentType> = emptyMap()
    }

    data class GlslVar(
        override val name: String,
        override val type: GlslType,
        override val fullText: String = "",
        val isConst: Boolean = false,
        val isUniform: Boolean = false,
        override val isVarying: Boolean = false,
        val initExpr: String? = null,
        override val lineNumber: Int? = null,
        override val comments: List<String> = emptyList()
    ) : GlslStatement, GlslArgSite {
        override val title get() = name.englishize()
        override val isGlobalInput: Boolean get() = isUniform || isVarying
        override val isAbstractFunction: Boolean get() = false
        val isPassThrough: Boolean get() = hint?.isPassThrough == true
        override val hint: Hint? by lazy { Hint.parse(comments.joinToString(" ") { it.trim() }, lineNumber) }
        val deferInitialization: Boolean = !isConst && initExpr != null

        override fun toGlsl(fileNumber: Int?, statementRewriter: StatementRewriter): String {
            val declaration = if (deferInitialization) {
                fullText.substring(0, fullText.indexOf(initExpr!!)) + ";"
            } else fullText
            return substituteGlsl(declaration, statementRewriter, fileNumber)
        }

        fun assignmentToGlsl(fileNumber: Int?, statementRewriter: StatementRewriter): String {
            val assignment = "  $name$initExpr;"
            return substituteGlsl(assignment, statementRewriter, fileNumber)
        }
    }

    class Hint(
        val pluginRef: PluginRef?,
        val config: JsonObject?,
        private val tags: List<Pair<String, String>>,
        val lineNumber: Int? = null
    ) {
        val isPassThrough: Boolean get() = tags.any { it.first == "pass-through" }

        fun tag(name: String) =
            tags.find { it.first == name }?.second

        fun tags(tagName: String): List<String> =
            tags.filter { it.first == tagName }.map { it.second }

        fun contentType(plugins: Plugins) = contentType("type", plugins)

        fun contentType(tagName: String, plugins: Plugins) =
            tag(tagName)?.let { plugins.resolveContentType(it) }

        companion object {
            fun from(comments: List<String>, lineNumber: Int? = null): Hint? =
                parse(comments.joinToString(" ") { it.trim() }, lineNumber)

            fun parse(commentString: String, lineNumber: Int? = null): Hint? {
                var pluginRef: PluginRef? = null
                var config: JsonObject? = null
                val tags = arrayListOf<Pair<String, String>>()

                Regex("@(@?[^@]+)").findAll(commentString).forEach { match ->
                    val tag = match.groupValues[1].trim()

                    if (tag.startsWith("@")) {
                        val string = tag.trimStart('@').trim()

                        val parts = string.split(" ")
                        val type = parts.first()
                        pluginRef = PluginRef.from(type)
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
                            1 -> tags.add(parts[0] to parts[0])
                            2 -> tags.add(parts[0] to parts[1].trim())
                        }
                    }
                }

                return if (pluginRef != null || config != null || tags.isNotEmpty()) {
                    Hint(pluginRef, config, tags, lineNumber)
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
        override val comments: List<String> = emptyList(),
        val isAbstract: Boolean = false,
        override val isGlobalInput: Boolean = false
    ) : GlslStatement, GlslArgSite {
        override val title: String get() = name.englishize()
        override val type: GlslType get() = returnType
        override val isVarying: Boolean get() = true
        override val isAbstractFunction: Boolean get() = isAbstract

        override val hint: Hint? by lazy { Hint.from(comments, lineNumber) }

        override fun findInjectedData(plugins: Plugins): Map<String, ContentType> {
            return params.associate { it.name to (it.findContentType(plugins, this) ?: ContentType.Unknown) }
        }

        override fun toGlsl(fileNumber: Int?, statementRewriter: StatementRewriter): String {
            // Chomp trailing ';' if it's an abstract method.
            return super.toGlsl(fileNumber, statementRewriter)
                .let { if (isAbstract) it.trimEnd(';') else it }
        }

        fun invoker(namespace: Namespace, portMap: Map<String, GlslExpr>): Invoker {
            return object : Invoker {
                override fun toGlsl(resultVar: String): String {
                    val assignment = if (returnType != GlslType.Void) {
                        "$resultVar = "
                    } else ""

                    val args = params.joinToString(", ") { glslParam ->
                        if (glslParam.isOut)
                            resultVar
                        else
                            portMap[glslParam.name]?.s
                                ?: "/* huh? no ${glslParam.name} in ${portMap.keys}. */"
                    }

                    return assignment + namespace.qualify(name) + "($args)"
                }
            }
        }
    }

    data class GlslParam(
        override val name: String,
        override val type: GlslType,
        val isIn: Boolean = false,
        val isOut: Boolean = false,
        override val lineNumber: Int? = null,
        val comments: List<String> = emptyList()
    ) : GlslArgSite {
        override val title: String get() = name.englishize()
        override val isVarying: Boolean get() = true
        override val isGlobalInput: Boolean get() = false
        override val hint: Hint? by lazy { Hint.from(comments, lineNumber) }
        override val isAbstractFunction: Boolean get() = false
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

    interface Invoker {
        fun toGlsl(resultVar: String): String
    }
}