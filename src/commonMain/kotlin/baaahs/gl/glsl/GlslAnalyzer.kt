package baaahs.gl.glsl

import baaahs.gl.glsl.GlslCode.GlslFunction
import baaahs.gl.glsl.GlslCode.GlslVar
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderType

class GlslAnalyzer(private val plugins: Plugins) {
    fun import(src: String, defaultTitle: String? = null): Shader {
        val glslCode = analyze(src)
        val type = ShaderType.values().firstOrNull { it.matches(glslCode) }
            ?: ShaderType.Paint // Reasonable guess?

        val title = Regex("^// (.*)").find(src)?.groupValues?.get(1)

        return Shader(
            title ?: defaultTitle ?: "Untitled ${type.name} Shader",
            type,
            src)
    }

    fun analyze(glslSrc: String): GlslCode {
        return GlslCode(glslSrc, findStatements(glslSrc))
    }

    internal fun findStatements(glslSrc: String): List<GlslStatement> {
        val context = Context()
        context.parse(glslSrc, ParseState.initial(context)).visitEof()
        return context.statements
    }

    fun openShader(shaderText: String): OpenShader {
        val shader = import(shaderText)
        return openShader(shader)
    }

    fun openShader(shader: Shader): OpenShader {
        val glslObj = analyze(shader.src)
        return shader.type.open(shader, glslObj, plugins)
    }

    private class Context {
        val macros: MutableMap<String, Macro> = hashMapOf()
        val statements: MutableList<GlslStatement> = arrayListOf()
        var outputEnabled = true
        val enabledStack = mutableListOf<Boolean>()
        var lineNumber = 1
        var lineNumberForError = 1

        fun parse(
            text: String,
            initialState: ParseState,
            freezeLineNumber: Boolean = false
        ): ParseState {
            var state = initialState
            Regex("(.*?)(//|[;{}()#\n]|$)").findAll(text).forEach { matchResult ->
                val (before, token) = matchResult.destructured

                if (!freezeLineNumber && token == "\n") lineNumber++
                lineNumberForError = lineNumber

                if (before.isNotEmpty()) {
                    // Further tokenize text blocks to isolate symbol strings.
                    Regex("(.*?)([A-Za-z][A-Za-z0-9_]*|$)").findAll(before).forEach { beforeMatch ->
                        val (nonSymbol, symbol) = beforeMatch.destructured
                        if (nonSymbol.isNotEmpty())
                            state = state.visit(nonSymbol)
                        if (symbol.isNotEmpty())
                            state = state.visit(symbol)
                    }
                }
                if (token.isNotEmpty()) state = state.visit(token)
            }
            return state
        }

        fun doUndef(args: List<String>) {
            if (outputEnabled) {
                if (args.size != 1) throw glslError("#undef ${args.joinToString(" ")}")
                macros.remove(args[0])
            }
        }

        fun doIfdef(args: List<String>) {
            if (args.size != 1) throw glslError("#ifdef ${args.joinToString(" ")}")
            enabledStack.add(outputEnabled)
            outputEnabled = outputEnabled && macros.containsKey(args.first())
        }

        fun doIfndef(args: List<String>) {
            if (args.size != 1) throw glslError("#ifndef ${args.joinToString(" ")}")
            enabledStack.add(outputEnabled)
            outputEnabled = outputEnabled && !macros.containsKey(args.first())
        }

        fun doElse(args: List<String>) {
            if (args.isNotEmpty()) throw glslError("#else ${args.joinToString(" ")}")
            outputEnabled = enabledStack.last() && !outputEnabled
        }

        fun doEndif(args: List<String>) {
            if (args.isNotEmpty()) throw glslError("#endif ${args.joinToString(" ")}")
            outputEnabled = enabledStack.removeLast()
        }

        @Suppress("UNUSED_PARAMETER")
        fun doLine(args: List<String>) {
            // No-op.
        }

        fun glslError(message: String) =
            AnalysisException(message, lineNumberForError)
    }

    private class Macro(
        val params: List<String>?,
        val replacement: String
    )

    private sealed class ParseState(val context: Context) {
        companion object {
            fun initial(context: Context): ParseState =
                Statement(context)
        }

        private val text = StringBuilder()
        val textAsString get() = text.toString()
        fun textIsEmpty() = text.isEmpty()

        fun appendText(value: String) {
            text.append(value)
        }

        open fun visit(token: String): ParseState {
            return when (token) {
                "//" -> visitComment()
                ";" -> visitSemicolon()
                "{" -> visitLeftCurlyBrace()
                "}" -> visitRightCurlyBrace()
                "(" -> visitLeftParen()
                ")" -> visitRightParen()
                "#" -> visitDirective()
                "\n" -> visitNewline()
                "" -> visitEof()
                else -> visitText(token)
            }
        }

        open fun visitText(value: String): ParseState {
            return if (context.outputEnabled || value == "\n") {
                val macro = context.macros[value]
                if (macro == null) {
                    appendText(value)
                    this
                } else if (macro.params == null) {
                    context.parse(macro.replacement.trim(), this, freezeLineNumber = true)
                } else {
                    MacroExpansion(context, this, macro)
                }
            } else this
        }

        open fun visitComment(): ParseState = visitText("//")
        open fun visitSemicolon(): ParseState = visitText(";")
        open fun visitLeftCurlyBrace(): ParseState = visitText("{")
        open fun visitRightCurlyBrace(): ParseState = visitText("}")
        open fun visitLeftParen(): ParseState = visitText("(")
        open fun visitRightParen(): ParseState = visitText(")")
        open fun visitDirective(): ParseState = visitText("#")
        open fun visitNewline(): ParseState = visitText("\n")
        open fun visitEof(): ParseState = this

        open fun addComment(comment: String) {}

        private class Statement(
            context: Context,
            val precedingStatement: Statement? = null
        ) : ParseState(context) {
            var lineNumber = context.lineNumber
            var braceNestLevel: Int = 0
            val comments = mutableListOf<String>()
            var nextCommentIsMeantForPreviousStatement = true

            override fun visitComment(): ParseState {
                return if (braceNestLevel == 0) {
                    val parentParseState =
                        if (nextCommentIsMeantForPreviousStatement && precedingStatement != null)
                            precedingStatement
                        else this
                    Comment(context, parentParseState, this)
                } else {
                    super.visitComment()
                }
            }

            override fun visitSemicolon(): ParseState {
                return if (braceNestLevel == 0) {
                    visitText(";")
                    finishStatement()
                } else {
                    visitText(";")
                }
            }

            override fun visitLeftCurlyBrace(): ParseState {
                braceNestLevel++
                return super.visitLeftCurlyBrace()
            }

            override fun visitRightCurlyBrace(): ParseState {
                braceNestLevel--
                super.visitRightCurlyBrace()

                return if (braceNestLevel == 0) {
                    val isStruct = textAsString.trim()
                        .contains(Regex("^(uniform\\s+)?struct\\s"))
                    if (isStruct) {
                        this
                    } else {
                        finishStatement()
                    }
                } else {
                    this
                }
            }

            override fun visitDirective(): ParseState {
                return Directive(context, this)
            }

            override fun visitNewline(): ParseState {
                nextCommentIsMeantForPreviousStatement = false

                return if (textIsEmpty() && comments.isEmpty()) {
                    // Skip leading newlines.
                    lineNumber = context.lineNumber
                    this
                } else {
                    super.visitNewline()
                }
            }

            override fun visitEof(): ParseState {
                if (!isEmpty()) finishStatement()
                return Statement(context, this)
            }

            override fun addComment(comment: String) {
                comments.add(comment)
            }

            fun finishStatement(): Statement {
                context.statements.add(
                    GlslStatement(
                        textAsString,
                        comments,
                        lineNumber
                    )
                )
                return Statement(context, this)
            }

            fun isEmpty(): Boolean = textAsString.isEmpty() && comments.isEmpty()
        }

        private class Comment(
            context: Context,
            val parentParseState: ParseState,
            val nextParseState: ParseState
        ) : ParseState(context) {
            val commentText = StringBuilder()

            override fun visitText(value: String): ParseState {
                commentText.append(value)
                return this
            }

            override fun visitNewline(): ParseState {
                parentParseState.addComment(commentText.toString())
                parentParseState.visitText("\n")
                return nextParseState
            }
        }

        private class Directive(context: Context, val priorParseState: ParseState) : ParseState(context) {
            override fun visitText(value: String): ParseState {
                return if (textIsEmpty() && value == "define") {
                    MacroDeclaration(context, priorParseState)
                } else {
                    appendText(value)
                    this
                }
            }

            override fun visitNewline(): ParseState {
                context.lineNumberForError -= 1

                val str = textAsString
                val args = str.split(Regex("\\s+")).toMutableList()
                val directive = args.removeFirst()
                when (directive) {
                    "define" -> throw IllegalStateException("This should be handled by MacroDeclaration.")
                    "undef" -> context.doUndef(args)
                    "ifdef" -> context.doIfdef(args)
                    "ifndef" -> context.doIfndef(args)
                    "else" -> context.doElse(args)
                    "endif" -> context.doEndif(args)
                    "line" -> context.doLine(args)
                    else -> throw context.glslError("unknown directive #$str")
                }
                priorParseState.visitText("\n")
                return priorParseState
            }

            override fun visitEof(): ParseState {
                return visitNewline()
            }
        }

        class MacroDeclaration(context: Context, val priorParseState: ParseState) : ParseState(context) {
            enum class Mode { Initial, HaveName, InArgs, InReplacement}
            private var mode = Mode.Initial
            private var name: String? = null
            private var args: MutableList<String>? = null

            override fun visitText(value: String): ParseState {
                when (mode) {
                    Mode.Initial -> {
                        if (value.isNotBlank()) {
                            name = value
                            mode =
                                Mode.HaveName
                        }
                    }
                    Mode.HaveName -> {
                        mode =
                            Mode.InReplacement
                        super.visitText(value)
                    }
                    Mode.InArgs -> {
                        if (value.isNotBlank() && value != ",") {
                            args!!.add(value)
                        }
                    }
                    Mode.InReplacement -> {
                        super.visitText(value)
                    }
                }
                return this
            }

            override fun visitLeftParen(): ParseState {
                return if (mode == Mode.HaveName) {
                    mode = Mode.InArgs
                    args = arrayListOf()
                    this
                } else super.visitLeftParen()
            }

            override fun visitRightParen(): ParseState {
                return if (mode == Mode.InArgs) {
                    mode = Mode.InReplacement
                    this
                } else super.visitRightParen()
            }

            override fun visitNewline(): ParseState {
                context.lineNumberForError -= 1
                val name = name ?: throw context.glslError("#define with no macro name")
                if (context.outputEnabled) {
                    context.macros[name] = Macro(args, textAsString)
                }
                priorParseState.visitText("\n")
                return priorParseState
            }
        }

        private class MacroExpansion(
            context: Context,
            private val priorParseState: ParseState,
            private val macro: Macro
        ) : ParseState(context) {
            private var parenCount = 0

            override fun visitText(value: String): ParseState {
                return if (parenCount == 0) {
                    priorParseState.visitText(macro.replacement.trim())
                    insertReplacement(value)
                } else {
                    super.appendText(value)
                    this
                }
            }

            override fun visitLeftParen(): ParseState {
                parenCount++
                return if (parenCount == 1) {
                    this
                } else {
                    super.visitLeftParen()
                }
            }

            override fun visitRightParen(): ParseState {
                parenCount--

                return if (parenCount == 0) {
                    val args = textAsString.split(',').map { it.trim() }
                    val subs = (macro.params ?: emptyList()).zip(args).associate { it }
                    val substituted = macro.replacement.trim().replace(wordRegex) {
                        subs[it.value] ?: it.value
                    }
                    insertReplacement(substituted)
                } else {
                    super.visitRightParen()
                }
            }

            override fun visitNewline(): ParseState {
                return priorParseState
            }

            private fun insertReplacement(value: String): ParseState {
                return context.parse(value, priorParseState, freezeLineNumber = true)
            }
        }
    }

    data class GlslStatement(
        val text: String,
        val comments: List<String> = emptyList(),
        val lineNumber: Int? = null
    ) {
        fun asSpecialOrNull(): GlslCode.GlslOther? {
            return Regex("^(precision)\\s+.*;", RegexOption.MULTILINE)
                .find(text.trim())?.let {
                    val (keyword) = it.destructured
                    GlslCode.GlslOther(keyword, text, lineNumber, comments)
                }
        }

        fun asStructOrNull(): GlslCode.GlslStruct? {
            return Regex("^(uniform\\s+)?struct\\s+(\\w+)\\s+\\{([^}]+)}(?:\\s+(\\w+)?)?;\$", RegexOption.MULTILINE)
                .find(text.trim())?.let {
                    val (uniform, name, members, varName) = it.destructured
                    val fields = mutableMapOf<String, GlslType>()

                    members.replace(Regex("//.*"), "")
                        .split(";")
                        .forEach { member ->
                            val trimmed = member.trim()
                            if (trimmed.isEmpty()) return@forEach
                            val parts = trimmed.split(Regex("\\s+"))
                            when(parts.size) {
                                0 -> return@forEach
                                2 -> fields[parts[1]] = GlslType.from(parts[0])
                                else -> throw AnalysisException("illegal struct member \"$member\"", lineNumber ?: -1)
                            }
                        }
                    val varNameOrNull = if (varName.isBlank()) null else varName
                    GlslCode.GlslStruct(name, fields, varNameOrNull, uniform.isNotBlank(), text, lineNumber, comments)
                }
        }

        fun asVarOrNull(): GlslVar? {
            // If there are curly braces it must be a function.
            if (text.contains("{")) return null

            return Regex("(?:(const|uniform|varying)\\s+)?(\\w+)\\s+(\\w+)(\\s*=.*)?;", RegexOption.MULTILINE)
                .find(text.trim())?.let {
                    val (qualifier, type, name, constValue) = it.destructured
                    var (isConst, isUniform, isVarying) = arrayOf(false, false, false)
                    when (qualifier) {
                        "const" -> isConst = true
                        "uniform" -> isUniform = true
                        "varying" -> isVarying = true
                    }
                    GlslVar(GlslType.from(type), name, text, isConst, isUniform, isVarying, lineNumber, comments)
                }
        }

        fun asFunctionOrNull(globalVars: Set<String> = emptySet()): GlslFunction? {
            return Regex("(\\w+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*(\\{[\\s\\S]*})", RegexOption.MULTILINE)
                .find(text.trim())?.let {
                    val (returnType, name, params, body) = it.destructured
                    GlslFunction(GlslType.from(returnType), name, params, text, lineNumber, globalVars, comments)
                }
        }
    }

    companion object {
        val wordRegex = Regex("([A-Za-z][A-Za-z0-9_]*)")
    }
}