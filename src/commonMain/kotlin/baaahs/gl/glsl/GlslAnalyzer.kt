package baaahs.gl.glsl

import baaahs.gl.glsl.GlslCode.*
import baaahs.gl.shader.*
import baaahs.only
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderType

class GlslAnalyzer(private val plugins: Plugins) {
    fun detectDialect(src: String): ShaderDialect {
        return detectDialect(parse(src))
    }

    fun detectDialect(glslCode: GlslCode): ShaderDialect {
        return plugins.shaderDialects.all
            .map { it to it.matches(glslCode) }
            .filter { (_, match) -> match != MatchLevel.NoMatch }
            .maxByOrNull { (_, match) -> match }?.first
            ?: GenericShaderDialect
    }

    fun parse(src: String): GlslCode {
        return GlslCode(src, findStatements(src))
    }

    internal fun findStatements(glslSrc: String): List<GlslStatement> {
        val context = Context()
        context.parse(glslSrc, ParseState.initial(context)).visitEof()
        return context.statements
    }

    fun import(src: String): Shader {
        val glslCode = parse(src)
        return validate(glslCode).shader
    }

    fun validate(src: String): ShaderAnalysis {
        return validate(parse(src))
    }

    fun validate(glslCode: GlslCode, shader: Shader? = null): ShaderAnalysis {
        val dialect = detectDialect(glslCode)
        return dialect.analyze(glslCode, plugins, shader)
    }

    fun openShader(src: String): OpenShader {
        return openShader(parse(src))
    }

    fun openShader(shader: Shader): OpenShader {
        return openShader(parse(shader.src), shader)
    }

    private fun openShader(glslCode: GlslCode, shader: Shader? = null): OpenShader {
        val shaderAnalysis = validate(glslCode, shader)
        return openShader(shaderAnalysis)
    }

    fun openShader(shaderAnalysis: ShaderAnalysis): OpenShader.Base {
        if (!shaderAnalysis.isValid)
            throw error(
                "Shader \"${shaderAnalysis.shader.title}\" not valid:" +
                        " ${shaderAnalysis.errors.joinToString(" ") { it.message }}"
            )

        val shaderType = plugins.shaderTypes.all
            .map { it to it.matches(shaderAnalysis) }
            .filter { (_, match) -> match != ShaderType.MatchLevel.NoMatch }
            .maxByOrNull { (_, match) -> match }?.first
            ?: ShaderType.Unknown

        return with(shaderAnalysis) {
            OpenShader.Base(this.shader, shaderAnalysis.glslCode,
                entryPoint!!, inputPorts, outputPorts.only(),
                shaderType, shaderDialect)
        }
    }

    private class Context {
        val macros: MutableMap<String, Macro> = hashMapOf()
        val statements: MutableList<GlslStatement> = arrayListOf()
        var outputEnabled = true
        val enabledStack = mutableListOf<Boolean>()
        var lineNumber = 1
        var lineNumberForError = 1

        val structs = mutableMapOf<String, GlslStruct>()

        fun findType(name: String): GlslType =
            structs[name]?.let { GlslType.Struct(it) }
                ?: GlslType.from(name)

        fun parse(
            text: String,
            initialState: ParseState,
            freezeLineNumber: Boolean = false
        ): ParseState {
            var state = initialState
            Regex("(.*?)(//|[;{}(,)#\n]|$)").findAll(text).forEach { matchResult ->
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

        fun checkForMacro(value: String, parseState: ParseState): ParseState? {
            val macro = macros[value]
            return if (macro == null) {
                null
            } else if (macro.params == null) {
                parse(macro.replacement.trim(), parseState, freezeLineNumber = true)
            } else {
                ParseState.MacroExpansion(this, parseState, macro)
            }
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
                UnidentifiedStatement(context)
        }

        private val text = StringBuilder()
        val textAsString get() = text.toString()
        fun textIsEmpty() = text.isEmpty()

        open fun appendText(value: String) {
            text.append(value)
        }

        open fun visit(token: String): ParseState {
            return when (token) {
                "//" -> visitComment()
                ";" -> visitSemicolon()
                "{" -> visitLeftCurlyBrace()
                "}" -> visitRightCurlyBrace()
                "(" -> visitLeftParen()
                "," -> visitComma()
                ")" -> visitRightParen()
                "#" -> visitDirective()
                "\n" -> visitNewline()
                "" -> visitEof()
                else -> visitText(token)
            }
        }

        open fun visitText(value: String): ParseState {
            return if (context.outputEnabled || value == "\n") {
                context.checkForMacro(value, this)
                    ?: run {
                        appendText(value)
                        this
                    }
            } else this
        }

        open fun visitComment(): ParseState = visitText("//")
        open fun visitSemicolon(): ParseState = visitText(";")
        open fun visitLeftCurlyBrace(): ParseState = visitText("{")
        open fun visitRightCurlyBrace(): ParseState = visitText("}")
        open fun visitLeftParen(): ParseState = visitText("(")
        open fun visitComma(): ParseState = visitText(",")
        open fun visitRightParen(): ParseState = visitText(")")
        open fun visitDirective(): ParseState = visitText("#")
        open fun visitNewline(): ParseState = visitText("\n")
        open fun visitEof(): ParseState = this

        open fun addComment(comment: String) {}
        open fun receiveSubsequentComments() {}

        private abstract class Statement(
            context: Context,
            var recipientOfNextComment: Statement? = null
        ) : ParseState(context) {
            var lineNumber = context.lineNumber
            var braceNestLevel: Int = 0
            val comments = mutableListOf<String>()

            override fun visitComment(): ParseState {
                return if (braceNestLevel == 0) {
                    Comment(context, recipientOfNextComment ?: this, this)
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
                receiveSubsequentComments()

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
                return UnidentifiedStatement(context, this)
            }

            override fun receiveSubsequentComments() {
                recipientOfNextComment = null
            }

            override fun addComment(comment: String) {
                comments.add(comment)
            }

            fun finishStatement(): Statement {
                context.statements.add(createStatement())
                return UnidentifiedStatement(context, this)
            }

            abstract fun createStatement(): GlslStatement

            fun asVarOrNull(): GlslVar? {
                val text = textAsString

                return Regex("(?:(const|uniform|varying)\\s+)?(\\w+)\\s+(\\w+)(\\s*=.*)?;", RegexOption.MULTILINE)
                    .find(text.trim())?.let {
                        val (qualifier, type, name, constValue) = it.destructured
                        var (isConst, isUniform, isVarying) = arrayOf(false, false, false)
                        when (qualifier) {
                            "const" -> isConst = true
                            "uniform" -> isUniform = true
                            "varying" -> isVarying = true
                        }
                        GlslVar(name, context.findType(type), text, isConst, isUniform, isVarying, lineNumber, comments)
                    }
            }

            fun asSpecialOrNull(): GlslOther? {
                val text = textAsString
                return Regex("^(precision)\\s+.*;", RegexOption.MULTILINE)
                    .find(text.trim())?.let {
                        val (keyword) = it.destructured
                        GlslOther(keyword, text, lineNumber, comments)
                    }
            }

            fun copyFrom(other: Statement): ParseState {
                appendText(other.textAsString)
                lineNumber = other.lineNumber
                comments.addAll(other.comments)
                return this
            }

            fun isEmpty(): Boolean = textAsString.isEmpty() && comments.isEmpty()
        }

        private class UnidentifiedStatement(
            context: Context,
            recipientOfNextComment: Statement? = null
        ) : Statement(context, recipientOfNextComment) {
            val tokensSoFar = mutableListOf<String>()

            override fun visitLeftParen(): ParseState {
                return if (context.outputEnabled && braceNestLevel == 0 && !tokensSoFar.contains("=")) {
                    val fn = Function(tokensSoFar, context)
                    fn.copyFrom(this)
                    fn.appendText("(")
                    fn.Params()
                } else {
                    super.visitLeftParen()
                }
            }

            override fun visitText(value: String): ParseState {
                return if (value == "struct") {
                    Struct(context)
                        .copyFrom(this).apply { appendText(value) }
                } else {
                    if (context.outputEnabled && value.isNotBlank())
                        tokensSoFar.add(value.trim())
                    super.visitText(value)
                }
            }

            override fun createStatement(): GlslStatement {
                return asSpecialOrNull()
                    ?: asVarOrNull()
                    ?: GlslOther("unknown", textAsString, lineNumber)
            }
        }

        private class Struct(context: Context) : Statement(context) {
            override fun createStatement(): GlslStatement =
                asStructOrNull()
                    ?: throw context.glslError("huh? couldn't find a struct in \"$textAsString\"")

            fun asStructOrNull(): GlslStruct? {
                val text = textAsString
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
                                when (parts.size) {
                                    0 -> return@forEach
                                    2 -> fields[parts[1]] = GlslType.from(parts[0])
                                    else -> throw AnalysisException("illegal struct member \"$member\"", lineNumber)
                                }
                            }
                        val varNameOrNull = if (varName.isBlank()) null else varName
                        GlslStruct(
                            name, fields, varNameOrNull, uniform.isNotBlank(),
                            text, lineNumber, comments
                        )
                            .also { context.structs[name] = it }
                    }
            }
        }

        private class Function(
            tokensSoFar: List<String>,
            context: Context
        ) : Statement(context) {
            val returnType: GlslType
            val name: String
            val params = arrayListOf<GlslParam>()

            init {
                if (tokensSoFar.size != 2)
                    throw context.glslError("unexpected tokens $tokensSoFar in function")

                returnType = context.findType(tokensSoFar[0])
                name = tokensSoFar[1]
            }

            override fun createStatement(): GlslStatement =
                GlslFunction(name, returnType, params, textAsString, lineNumber, comments)

            inner class Params(
                recipientOfNextComment: Statement? = null
            ) : Statement(context, recipientOfNextComment) {
                var qualifier: String? = null
                var type: GlslType? = null
                var name: String? = null

                override fun appendText(value: String) {
                    this@Function.appendText(value)
                }

                override fun visitText(value: String): ParseState {
                    val trimmed = value.trim()
                    if (trimmed.isNotEmpty()) {
                        if (qualifier == null && (trimmed == "in" || trimmed == "out" || trimmed == "inout")) {
                            qualifier = trimmed
                        } else if (type == null) {
                            type = GlslType.from(trimmed)
                        } else if (name == null) {
                            name = trimmed
                        } else {
                            throw context.glslError("Unexpected token \"$trimmed\".")
                        }
                    }

                    return super.visitText(value)
                }

                override fun visitComma(): ParseState {
                    addParam()
                    appendText(",")
                    return Params(this)
                }

                override fun visitRightParen(): ParseState {
                    addParam()
                    appendText(")")
                    return this@Function
                }

                override fun visitNewline(): ParseState {
                    appendText("\n")
                    receiveSubsequentComments()
                    return this
                }

                override fun createStatement(): GlslStatement {
                    error("huh?")
                }

                fun addParam() {
                    val type = type
                    val name = name

                    if (type == GlslType.Void) return
                    if (name == null && type == null) return

                    if (type == null) {
                        throw context.glslError("No type for parameter in ${this@Function.name}().")
                    } else if (name == null) {
                        throw context.glslError("No name for parameter in ${this@Function.name}().")
                    }

                    params.add(
                        GlslParam(
                            name,
                            type,
                            isIn = qualifier == null || qualifier == "in" || qualifier == "inout",
                            isOut = qualifier == "out" || qualifier == "inout",
                            lineNumber = lineNumber,
                            comments = comments
                        )
                    )
                }
            }
        }

        private class Comment(
            context: Context,
            val recipientOfComment: ParseState,
            val nextParseState: ParseState
        ) : ParseState(context) {
            val commentText = StringBuilder()

            override fun visitText(value: String): ParseState {
                commentText.append(value)
                return this
            }

            override fun visitNewline(): ParseState {
                recipientOfComment.addComment(commentText.toString())
                recipientOfComment.visitText("\n")
                nextParseState.receiveSubsequentComments()
                return nextParseState
            }

            override fun visitEof(): ParseState {
                recipientOfComment.addComment(commentText.toString())
                return super.visitEof()
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

                val str = textAsString.trim()
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
            enum class Mode { Initial, HaveName, InArgs, InReplacement }

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
                        if (value.isNotBlank() && value.trim() != ",") {
                            args!!.add(value.trim())
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

        class MacroExpansion(
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
                    context.checkForMacro(value, this)
                        ?: run {
                            super.appendText(value)
                            this
                        }
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

    companion object {
        private val wordRegex = Regex("([A-Za-z][A-Za-z0-9_]*)")
    }
}