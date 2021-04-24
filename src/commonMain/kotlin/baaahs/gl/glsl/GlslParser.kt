package baaahs.gl.glsl

class GlslParser {
    fun parse(src: String): GlslCode {
        return GlslCode(src, findStatements(src))
    }

    internal fun findStatements(glslSrc: String): List<GlslCode.GlslStatement> {
        val context = Context()
        context.parse(glslSrc, ParseState.initial(context)).visitEof()
        return context.statements
    }

    private class Context {
        val macros: MutableMap<String, Macro> = hashMapOf()
        val statements: MutableList<GlslCode.GlslStatement> = arrayListOf()
        var outputEnabled = true
        val enabledStack = mutableListOf<Boolean>()
        var lineNumber = 1
        var lineNumberForError = 1

        val structs = mutableMapOf<String, GlslCode.GlslStruct>()

        fun findType(name: String): GlslType =
            structs[name]?.let { GlslType.Struct(it) }
                ?: GlslType.from(name)

        fun parse(
            text: String,
            initialState: ParseState,
            freezeLineNumber: Boolean = false
        ): ParseState {
            var state = initialState
            Regex("(.*?)(//|/\\*|\\*/|[;{}(,)#\n]|$)").findAll(text).forEach { matchResult ->
                val (before, token) = matchResult.destructured

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

                if (!freezeLineNumber && token == "\n") lineNumber++
                lineNumberForError = lineNumber

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
        fun textIsBlank() = text.isBlank()

        fun trimWhitespace() {
            while (text.lastOrNull() == ' ') text.setLength(text.length - 1)
        }

        open fun appendText(value: String) {
            text.append(value)
        }

        open fun visit(token: String): ParseState {
            return when (token) {
                "//" -> visitComment()
                "/*" -> visitBlockCommentBegin()
                "*/" -> visitBlockCommentEnd()
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
        open fun visitBlockCommentBegin(): ParseState = visitText("/*")
        open fun visitBlockCommentEnd(): ParseState = visitText("*/")
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
                    Comment(context, null, this)
                }
            }

            override fun visitBlockCommentBegin(): ParseState {
                return BlockComment(context, recipientOfNextComment ?: this, this)
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
                recipientOfNextComment?.let {
                    it.visitNewline()
                    receiveSubsequentComments()
                }

                return if (textIsBlank()) {
                    trimWhitespace()
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

            abstract fun createStatement(): GlslCode.GlslStatement

            fun asVarOrNull(): GlslCode.GlslVar? {
                val text = textAsString

                return Regex("(?:(const|uniform|varying)\\s+)?(\\w+)\\s+(\\w+)(\\s*\\[\\s*\\d+\\s*])?(\\s*=.*)?;", RegexOption.MULTILINE)
                    .find(text.trim())?.let {
                        val (qualifier, type, name, arraySpec, constValue) = it.destructured
                        var (isConst, isUniform, isVarying) = arrayOf(false, false, false)
                        when (qualifier) {
                            "const" -> isConst = true
                            "uniform" -> isUniform = true
                            "varying" -> isVarying = true
                        }
                        val (trimmedText, trimmedLineNumber) = chomp(text, lineNumber)
                        GlslCode.GlslVar(
                            name, context.findType(type), trimmedText, isConst, isUniform, isVarying,
                            trimmedLineNumber, comments
                        )
                    }
            }

            fun asSpecialOrNull(): GlslCode.GlslOther? {
                val text = textAsString
                return Regex("^(precision)\\s+.*;", RegexOption.MULTILINE)
                    .find(text.trim())?.let {
                        val (keyword) = it.destructured
                        GlslCode.GlslOther(keyword, text, lineNumber, comments)
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

            override fun createStatement(): GlslCode.GlslStatement {
                return asSpecialOrNull()
                    ?: asVarOrNull()
                    ?: GlslCode.GlslOther("unknown", textAsString, lineNumber)
            }
        }

        private class Struct(context: Context) : Statement(context) {
            override fun createStatement(): GlslCode.GlslStatement =
                asStructOrNull()
                    ?: throw context.glslError("huh? couldn't find a struct in \"$textAsString\"")

            fun asStructOrNull(): GlslCode.GlslStruct? {
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
                        GlslCode.GlslStruct(
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
            val params = arrayListOf<GlslCode.GlslParam>()
            var isAbstract = true

            init {
                if (tokensSoFar.size != 2)
                    throw context.glslError("unexpected tokens $tokensSoFar in function")

                returnType = context.findType(tokensSoFar[0])
                name = tokensSoFar[1]
            }

            override fun visitLeftCurlyBrace(): ParseState {
                isAbstract = false
                return super.visitLeftCurlyBrace()
            }

            override fun createStatement(): GlslCode.GlslStatement =
                GlslCode.GlslFunction(name, returnType, params, textAsString, lineNumber, comments, isAbstract)

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

                override fun createStatement(): GlslCode.GlslStatement {
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
                        GlslCode.GlslParam(
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
            val recipientOfComment: ParseState?,
            val nextParseState: ParseState
        ) : ParseState(context) {
            val commentText = StringBuilder()

            override fun visitText(value: String): ParseState {
                commentText.append(value)
                return this
            }

            override fun visitNewline(): ParseState {
                appendComment()
                nextParseState.visitNewline()
                return nextParseState
            }

            override fun visitEof(): ParseState {
                appendComment()
                return super.visitEof()
            }

            private fun appendComment() {
                if (recipientOfComment == null) {
                    nextParseState.visitText("//")
                    nextParseState.visitText(commentText.toString())
                }

                recipientOfComment?.addComment(commentText.toString())
            }
        }

        private class BlockComment(
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
                nextParseState.visitNewline()
                return super.visitNewline()
            }

            override fun visitBlockCommentEnd(): ParseState {
                recipientOfComment.addComment(commentText.toString())
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

        /** Chomp leading whitespace. */
        private fun chomp(text: String, lineNumber: Int?): Pair<String, Int?> {
            var newlineCount = 0
            var i = 0
            loop@ while (i < text.length) {
                when (text[i]) {
                    '\n' -> newlineCount++
                    ' ', '\t' -> {}
                    else -> break@loop
                }
                i++
            }
            val trimmedText = text.substring(i)
            val trimmedLineNumber = lineNumber?.plus(newlineCount)
            return trimmedText to trimmedLineNumber
        }
    }
}