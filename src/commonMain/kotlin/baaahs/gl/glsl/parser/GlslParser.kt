package baaahs.gl.glsl.parser

import baaahs.gl.glsl.*

class GlslParser {
    fun parse(src: String, fileName: String? = null): GlslCode {
        return GlslCode(src, findStatements(src), fileName)
    }

    internal fun findStatements(glslSrc: String): List<GlslCode.GlslStatement> {
        val context = Context()
        context.parse(glslSrc, ParseState.initial(context))
            .visitEof(Token("", -1))
        return context.statements
    }

    private class Context {
        val macros: MutableMap<String, Macro> = hashMapOf()
        var macroDepth = 0
        val statements: MutableList<GlslCode.GlslStatement> = arrayListOf()
        var outputEnabled = true
        var matchedBranch = false
        val enabledStack = mutableListOf<Boolean>()
        val tokenizer = Tokenizer()

        val structs = mutableMapOf<String, GlslCode.GlslStruct>()

        fun findType(name: String): GlslType =
            structs[name]?.let { GlslType.Struct(it) }
                ?: GlslType.from(name)

        fun parse(text: String, initialState: ParseState): ParseState =
            tokenizer.processTokens(text, initialState)

        fun parse(tokens: List<Token>, initialState: ParseState): ParseState =
            tokenizer.processTokens(tokens.asSequence(), initialState)

        fun doUndef(args: List<String>) {
            if (outputEnabled) {
                if (args.size != 1) throw glslError("#undef ${args.joinToString(" ")}")
                macros.remove(args[0])
            }
        }

        fun doIf(args: List<String>) {
            if (args.isEmpty()) throw glslError("#if ${args.joinToString(" ")}")
            enabledStack.add(outputEnabled)
            val matches = evaluate(args.joinToString(" "))
            outputEnabled = outputEnabled && matches
            matchedBranch = matches
        }

        fun doIfdef(args: List<String>) {
            if (args.size != 1) throw glslError("#ifdef ${args.joinToString(" ")}")
            enabledStack.add(outputEnabled)
            val matches = macros.containsKey(args.first())
            outputEnabled = outputEnabled && matches
            matchedBranch = matches
        }

        fun doIfndef(args: List<String>) {
            if (args.size != 1) throw glslError("#ifndef ${args.joinToString(" ")}")
            enabledStack.add(outputEnabled)
            val matches = !macros.containsKey(args.first())
            outputEnabled = outputEnabled && matches
            matchedBranch = matches
        }

        fun doElse(args: List<String>) {
            if (enabledStack.isEmpty()) throw glslError("#else outside of #if/#endif")
            if (args.isNotEmpty()) throw glslError("#else ${args.joinToString(" ")}")
            outputEnabled = !matchedBranch && enabledStack.last() && !outputEnabled
            if (outputEnabled) matchedBranch = true
        }

        fun doElif(args: List<String>) {
            if (enabledStack.isEmpty()) throw glslError("#elif outside of #if/#endif")
            if (args.isEmpty()) throw glslError("#elif ${args.joinToString(" ")}")
            if (enabledStack.last()) {
                val matches = !matchedBranch && evaluate(args.joinToString(" "))
                outputEnabled = matches
                if (matches) matchedBranch = true
            }
        }

        fun doEndif(args: List<String>) {
            if (enabledStack.isEmpty()) throw glslError("#endif outside of #if")
            if (args.isNotEmpty()) throw glslError("#endif ${args.joinToString(" ")}")
            outputEnabled = enabledStack.removeLast()
            matchedBranch = false
        }

        @Suppress("UNUSED_PARAMETER")
        fun doLine(args: List<String>) {
            // No-op.
        }

        private fun evaluate(args: String) =
            try {
                GlslMacroExpressionEvaluator.evaluate(args)
            } catch (e: Exception) {
                throw glslError(e.message!!)
            }

        fun checkForMacro(token: Token, parseState: ParseState): ParseState? {
            val macro = macros[token.text]
            return when {
                macro == null -> null
                macro.params == null -> {
                    macroDepth++
                    if (macroDepth >= maxMacroDepth)
                        throw glslError("Max macro depth exceeded for \"${token.text}\".")

                    tokenizer.processTokens(macro.replacement.asSequence(), parseState)
                        .also { macroDepth-- }
                }
                else -> ParseState.MacroExpansion(this, parseState, macro)
            }
        }

        fun glslError(message: String) =
            AnalysisException(message, tokenizer.lineNumberForError)
    }

    private class Macro(
        val params: List<Token>?,
        val replacement: List<Token>
    )

    private sealed class ParseState(val context: Context) : State<ParseState> {
        companion object {
            fun initial(context: Context): ParseState =
                UnidentifiedStatement(context)
        }

        val tokens = mutableListOf<Token>()
        val tokensAsString get() = tokens.joinToString("") { it.text }
        fun textIsEmpty() = tokens.isEmpty() || tokensAsString.isEmpty()
        fun textIsBlank() = tokens.isEmpty() || tokensAsString.isBlank()

        open fun appendText(token: Token) {
            tokens.add(token)
        }

        override fun visit(token: Token): ParseState =
            when (token.text) {
                "//" -> visitComment(token)
                "/*" -> visitBlockCommentBegin(token)
                "*/" -> visitBlockCommentEnd(token)
                ";" -> visitSemicolon(token)
                "{" -> visitLeftCurlyBrace(token)
                "}" -> visitRightCurlyBrace(token)
                "[" -> visitLeftBracket(token)
                "]" -> visitRightBracket(token)
                "(" -> visitLeftParen(token)
                "," -> visitComma(token)
                ")" -> visitRightParen(token)
                "#" -> visitDirective(token)
                "\n" -> visitNewline(token)
                "" -> visitEof(token)
                else -> visitText(token)
            }

        open fun visitText(token: Token): ParseState =
            if (context.outputEnabled || token.text == "\n")
                checkForMacro(token)
            else this

        fun checkForMacro(token: Token): ParseState =
            context.checkForMacro(token, this)
                ?: run {
                    appendText(token)
                    this
                }

        open fun visitCommentText(token: Token): ParseState = visitText(token)
        open fun visitComment(token: Token): ParseState = visitText(token)
        open fun visitBlockCommentBegin(token: Token): ParseState = visitText(token)
        open fun visitBlockCommentEnd(token: Token): ParseState = visitText(token)
        open fun visitSemicolon(token: Token): ParseState = visitText(token)
        open fun visitLeftCurlyBrace(token: Token): ParseState = visitText(token)
        open fun visitRightCurlyBrace(token: Token): ParseState = visitText(token)
        open fun visitLeftBracket(token: Token): ParseState = visitText(token)
        open fun visitRightBracket(token: Token): ParseState = visitText(token)
        open fun visitLeftParen(token: Token): ParseState = visitText(token)
        open fun visitComma(token: Token): ParseState = visitText(token)
        open fun visitRightParen(token: Token): ParseState = visitText(token)
        open fun visitDirective(token: Token): ParseState = visitText(token)
        open fun visitNewline(token: Token): ParseState = visitText(token)
        open fun visitEof(token: Token): ParseState = this

        open fun addComment(comment: String) {}
        open fun receiveSubsequentComments() {}

        private abstract class Statement(
            context: Context,
            var recipientOfNextComment: Statement? = null
        ) : ParseState(context) {
            var lineNumber = context.tokenizer.lineNumber
            var braceNestLevel: Int = 0
            val comments = mutableListOf<String>()

            override fun visitComment(token: Token): ParseState {
                return if (braceNestLevel == 0) {
                    Comment(context, token, recipientOfNextComment ?: this, this)
                } else {
                    Comment(context, token, null, this)
                }
            }

            override fun visitBlockCommentBegin(token: Token): ParseState {
                return BlockComment(context, token, recipientOfNextComment ?: this, this)
            }

            override fun visitSemicolon(token: Token): ParseState {
                return if (braceNestLevel == 0) {
                    visitText(token)
                    finishStatement()
                } else {
                    visitText(token)
                }
            }

            override fun visitLeftCurlyBrace(token: Token): ParseState {
                braceNestLevel++
                return super.visitLeftCurlyBrace(token)
            }

            override fun visitRightCurlyBrace(token: Token): ParseState {
                braceNestLevel--
                super.visitRightCurlyBrace(token)

                return if (braceNestLevel == 0) {
                    val isStruct = tokensAsString.trim()
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

            override fun visitDirective(token: Token): ParseState {
                return PreDirective(context, this)
            }

            override fun visitNewline(token: Token): ParseState {
                recipientOfNextComment?.let {
                    it.visitNewline(token)
                    receiveSubsequentComments()
                }

                return if (textIsBlank()) {
                    // Skip leading newlines.
                    lineNumber = context.tokenizer.lineNumber
                    this
                } else {
                    super.visitNewline(token)
                }
            }

            override fun visitEof(token: Token): ParseState {
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
                val text = tokensAsString

                // Escaped closing brace/bracket required for Kotlin 1.5+/JS or we fail with "lone quantifier brackets".
                @Suppress("RegExpRedundantEscape")
                return Regex("(?:(const|uniform|varying)\\s+)?(\\w+)\\s+(\\w+)(?:\\s*\\[\\s*(\\d+)\\s*\\])?(\\s*=.*)?;", RegexOption.MULTILINE)
                    .find(text.trim())?.let {
                        val (qualifier, type, name, arraySpec, initExpr) = it.destructured
                        var (isConst, isUniform, isVarying) = arrayOf(false, false, false)
                        when (qualifier) {
                            "const" -> isConst = true
                            "uniform" -> isUniform = true
                            "varying" -> isVarying = true
                        }
                        var glslType = context.findType(type)
                        if (arraySpec.isNotEmpty()) {
                            val arity = arraySpec.toInt()
                            glslType = glslType.arrayOf(arity)
                        }
                        val trimmedText = text.trimStart()
                        GlslCode.GlslVar(
                            name, glslType, trimmedText, isConst, isUniform, isVarying,
                            initExpr.ifEmpty { null }, tokens.first().lineNumber, comments
                        )
                    }
            }

            fun asSpecialOrNull(): GlslCode.GlslOther? {
                val text = tokensAsString
                return Regex("^(precision)\\s+.*;", RegexOption.MULTILINE)
                    .find(text.trim())?.let {
                        val (keyword) = it.destructured
                        GlslCode.GlslOther(keyword, text, lineNumber, comments)
                    }
            }

            fun copyFrom(other: Statement): ParseState {
                tokens.addAll(other.tokens)
                lineNumber = other.lineNumber
                comments.addAll(other.comments)
                return this
            }

            fun isEmpty(): Boolean = tokensAsString.isEmpty() && comments.isEmpty()
        }

        private class UnidentifiedStatement(
            context: Context,
            recipientOfNextComment: Statement? = null
        ) : Statement(context, recipientOfNextComment) {
            val tokensSoFar = mutableListOf<Token>()

            override fun visitLeftParen(token: Token): ParseState {
                return if (context.outputEnabled && braceNestLevel == 0 && !tokensSoFar.contains("=")) {
                    val fn = Function(tokensSoFar, context)
                    fn.copyFrom(this)
                    fn.appendText(token)
                    fn.Params()
                } else {
                    super.visitLeftParen(token)
                }
            }

            override fun visitText(token: Token): ParseState {
                return if (token.text == "struct") {
                    Struct(context)
                        .copyFrom(this)
                        .apply { appendText(token) }
                } else {
                    if (context.outputEnabled && token.text.isNotBlank()) {
                        tokensSoFar.add(token)
                    }

                    if (tokens.isEmpty() && token.text.isBlank()) {
                        this
                    } else {
                        super.visitText(token)
                    }
                }
            }

            override fun createStatement(): GlslCode.GlslStatement {
                return asSpecialOrNull()
                    ?: asVarOrNull()
                    ?: GlslCode.GlslOther("unknown", tokensAsString, lineNumber)
            }
        }

        private class Struct(context: Context) : Statement(context) {
            override fun createStatement(): GlslCode.GlslStatement =
                asStructOrNull()
                    ?: throw context.glslError("huh? couldn't find a struct in \"$tokensAsString\"")

            fun asStructOrNull(): GlslCode.GlslStruct? {
                val text = tokensAsString

                // Escaped closing brace/bracket required for Kotlin 1.5+/JS or we fail with "lone quantifier brackets".
                @Suppress("RegExpRedundantEscape")
                return Regex("^(uniform\\s+)?struct\\s+(\\w+)\\s+\\{([^}]+)\\}(?:\\s+(\\w+)?)?;\$", RegexOption.MULTILINE)
                    .find(text.trim())?.let { match ->
                        val (uniform, name, members, varName) = match.destructured
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
                        val varNameOrNull = varName.ifBlank { null }
                        GlslCode.GlslStruct(
                            name, fields, varNameOrNull, uniform.isNotBlank(),
                            text, lineNumber, comments
                        )
                            .also { context.structs[name] = it }
                    }
            }
        }

        private class Function(
            tokensSoFar: List<Token>,
            context: Context
        ) : Statement(context) {
            val returnType: GlslType
            val name: Token
            val params = arrayListOf<GlslCode.GlslParam>()
            var isAbstract = true

            init {
                val leadingModifierCount =
                    tokensSoFar.indexOfFirst { !precisionStrings.contains(it.text) }

                if (tokensSoFar.size - leadingModifierCount != 2)
                    throw context.glslError("unexpected tokens $tokensSoFar in function")

                returnType = context.findType(tokensSoFar[leadingModifierCount].text)
                name = tokensSoFar[leadingModifierCount + 1]
            }

            override fun visitLeftCurlyBrace(token: Token): ParseState {
                isAbstract = false
                return super.visitLeftCurlyBrace(token)
            }

            override fun createStatement(): GlslCode.GlslStatement =
                GlslCode.GlslFunction(name.text, returnType, params, tokensAsString, lineNumber, comments, isAbstract)

            inner class Params(
                recipientOfNextComment: Statement? = null
            ) : Statement(context, recipientOfNextComment) {
                var qualifier: String? = null
                var type: GlslType? = null
                var name: String? = null
                private var inArraySize = false

                override fun appendText(value: Token) {
                    this@Function.appendText(value)
                }

                override fun visitText(token: Token): ParseState {
                    val trimmed = token.text.trim()
                    when {
                        trimmed.isEmpty() -> {} // Ignore.

                        modifierStrings.contains(trimmed) -> {} // Ignore.

                        inArraySize -> {
                            val macro = context.macros[trimmed]
                            // Very confused about this, but without it we get double macro expansion in the name:
                            if (macro == null) name += trimmed
                        }

                        qualifier == null && (varDirections.contains(trimmed)) -> {
                            qualifier = trimmed
                        }

                        type == null -> type = context.findType(trimmed)

                        name == null -> name = trimmed

                        else -> throw context.glslError("Unexpected token \"$trimmed\".")
                    }

                    return super.visitText(token)
                }

                override fun visitComma(token: Token): ParseState {
                    addParam()
                    appendText(token)
                    return Params(this)
                }

                override fun visitLeftBracket(token: Token): ParseState {
                    if (inArraySize) error("Already in param array size.")
                    inArraySize = true
                    name += "["
                    return super.visitText(token)
                }

                override fun visitRightBracket(token: Token): ParseState {
                    if (!inArraySize) error("Not in param array size.")
                    inArraySize = false
                    name += "]"
                    return super.visitText(token)
                }

                override fun visitRightParen(token: Token): ParseState {
                    addParam()
                    appendText(token)
                    return this@Function
                }

                override fun visitNewline(token: Token): ParseState {
                    appendText(token)
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
            val startToken: Token,
            val recipientOfComment: ParseState?,
            val nextParseState: ParseState
        ) : ParseState(context) {
            val commentText = mutableListOf<Token>()

            override fun visitText(token: Token): ParseState {
                commentText.add(token)
                return this
            }

            override fun visitNewline(token: Token): ParseState {
                appendComment(token)
                return nextParseState.visitNewline(token)
            }

            override fun visitEof(token: Token): ParseState {
                appendComment(token)
                return super.visitEof(token)
            }

            private fun appendComment(endToken: Token) {
                if (recipientOfComment == null) {
                    nextParseState.visitCommentText(startToken)
                    commentText.forEach {
                        nextParseState.visitCommentText(it)
                    }
                    if (endToken.text != "\n") {
                        nextParseState.visitCommentText(endToken)
                    }
                } else {
                    recipientOfComment.addComment(
                        commentText.joinToString("") { it.text }
                    )
                }
            }
        }

        private class BlockComment(
            context: Context,
            val startToken: Token,
            val recipientOfComment: ParseState?,
            val nextParseState: ParseState,
            val chompNewlines: Boolean = false
        ) : ParseState(context) {
            val commentText = mutableListOf<Token>()

            override fun visitText(token: Token): ParseState {
                commentText.add(token)
                return this
            }

            override fun visitNewline(token: Token): ParseState {
                if (!chompNewlines) nextParseState.visitNewline(token)
                return super.visitNewline(token)
            }

            override fun visitBlockCommentEnd(token: Token): ParseState {
                appendComment(token)
                return nextParseState
            }

            private fun appendComment(endToken: Token) {
                if (recipientOfComment == null) {
                    nextParseState.visitCommentText(startToken)
                    commentText.forEach {
                        nextParseState.visitCommentText(it)
                    }
                    nextParseState.visitCommentText(endToken)
                } else {
                    recipientOfComment.addComment(
                        commentText.joinToString("") { it.text }
                    )
                }
            }
        }

        private class PreDirective(
            context: Context,
            val priorParseState: ParseState
        ) : ParseState(context) {
            private inner class Directive(
                var quoteFirstArgument: Boolean = false,
                val callback: (List<String>) -> Unit
            ) : ParseState(context) {
                override fun visitText(token: Token): ParseState =
                    if (token.text.isNotBlank() && quoteFirstArgument) {
                        appendText(token)
                        quoteFirstArgument = false
                        this
                    } else checkForMacro(token)

                override fun visitNewline(token: Token): ParseState {
                    context.tokenizer.lineNumberForError -= 1

                    val str = tokensAsString.trim()
                    val args = str.split(Regex("\\s+")).let {
                        // "".split("\\s+") returns [""].
                        if (it == listOf("")) emptyList() else it
                    }
                    callback(args)
                    priorParseState.visitText(token)
                    return priorParseState
                }

                override fun visitEof(token: Token): ParseState =
                    visitNewline(token)
            }

            override fun visitText(token: Token): ParseState =
                when (token.text) {
                    "define" -> DefineDirective(context, token, priorParseState)
                    "undef" -> Directive(quoteFirstArgument = true) { context.doUndef(it) }
                    "if" -> Directive { context.doIf(it) }
                    "ifdef" -> Directive(quoteFirstArgument = true) { context.doIfdef(it) }
                    "ifndef" -> Directive(quoteFirstArgument = true) { context.doIfndef(it) }
                    "else" -> Directive { context.doElse(it) }
                    "elif" -> Directive { context.doElif(it) }
                    "endif" -> Directive { context.doEndif(it) }
                    "error", "pragma", "extension", "version" ->
                        throw context.glslError("unsupported directive #${token.text}")

                    "line" -> Directive(quoteFirstArgument = true) { context.doLine(it) }
                    else -> throw context.glslError("unknown directive #${token.text}")
                }
        }

        class DefineDirective(
            context: Context,
            val startToken: Token,
            val priorParseState: ParseState
        ) : ParseState(context) {
            enum class Mode { Initial, HaveName, InArgs, InPreReplacement, InReplacement }

            private var mode = Mode.Initial
            private var name: Token? = null
            private var args: MutableList<Token>? = null

            override fun visitText(token: Token): ParseState {
                when (mode) {
                    Mode.Initial -> {
                        if (token.text.isNotBlank()) {
                            name = token
                            mode = Mode.HaveName
                        }
                    }
                    Mode.HaveName -> {
                        // Left paren directly after name case handled by visitLeftParen().
                        if (token.text.isNotBlank()) {
                            mode = Mode.InReplacement
                            super.visitText(token)
                        } else {
                            mode = Mode.InPreReplacement
                        }
                    }
                    Mode.InArgs -> {
                        if (token.text.isNotBlank() && token.text.trim() != ",") {
                            args!!.add(token)
                        }
                    }
                    Mode.InPreReplacement -> {
                        if (token.text.isNotBlank()) {
                            mode = Mode.InReplacement
                            super.visitText(token)
                        }
                    }
                    Mode.InReplacement -> {
                        super.visitText(token)
                    }
                }
                return this
            }

            // Ignore comments.
            override fun visitCommentText(token: Token): ParseState = this

            override fun visitComment(token: Token): ParseState =
                Comment(context, token, null, this)
            override fun visitBlockCommentBegin(token: Token): ParseState =
                BlockComment(context, token, null, this, chompNewlines = true)

            override fun visitLeftParen(token: Token): ParseState {
                return if (mode == Mode.HaveName) {
                    mode = Mode.InArgs
                    args = arrayListOf()
                    this
                } else super.visitLeftParen(token)
            }

            override fun visitRightParen(token: Token): ParseState {
                return if (mode == Mode.InArgs) {
                    mode = Mode.InPreReplacement
                    this
                } else super.visitRightParen(token)
            }

            override fun visitNewline(token: Token): ParseState {
                context.tokenizer.lineNumberForError -= 1
                val name = name ?: throw context.glslError("#define with no macro name")
                if (context.outputEnabled) {
                    while (tokens.lastOrNull()?.text?.isBlank() == true) {
                        tokens.removeLast()
                    }
                    context.macros[name.text] = Macro(args, ArrayList(tokens))
                }
                priorParseState.visitText(token)
                return priorParseState
            }
        }

        class MacroExpansion(
            context: Context,
            private val priorParseState: ParseState,
            private val macro: Macro
        ) : ParseState(context) {
            private var parenCount = 0
            private var chompWhitespace = false
            private val args = mutableListOf<List<Token>>()
            private var nextArg = mutableListOf<Token>()

            override fun visitText(token: Token): ParseState {
                if (chompWhitespace && token.text.isBlank()) {
                    return this
                }

                chompWhitespace = false
                return if (parenCount == 0) {
                    macro.replacement.forEach {
                        priorParseState.visitText(it)
                    }
                    insertReplacement(listOf(token))
                } else {
                    context.checkForMacro(token, this)
                        ?: run {
                            nextArg.add(token)
                            this
                        }
                }
            }

            override fun visitLeftParen(token: Token): ParseState {
                parenCount++
                return if (parenCount == 1) {
                    chompWhitespace = true
                    this
                } else {
                    super.visitLeftParen(token)
                }
            }

            override fun visitComma(token: Token): ParseState {
                return if (parenCount == 1) {
                    args.add(nextArg)
                    nextArg = mutableListOf()
                    chompWhitespace = true
                    this
                } else {
                    super.visitComma(token)
                }
            }

            override fun visitRightParen(token: Token): ParseState {
                parenCount--

                return if (parenCount == 0) {
                    args.add(nextArg)
                    nextArg = mutableListOf()
                    chompWhitespace = true

                    val subs = (macro.params?.map { it.text } ?: emptyList()).zip(args).associate { it }
                    val substituted = macro.replacement.flatMap { toToken ->
                        subs[toToken.text] ?: listOf(toToken)
                    }
                    insertReplacement(substituted)
                } else {
                    super.visitRightParen(token)
                }
            }

            override fun visitNewline(token: Token): ParseState {
                return priorParseState
            }

            private fun insertReplacement(tokens: List<Token>): ParseState {
                return context.parse(tokens, priorParseState)
            }
        }
    }

    companion object {
        private val wordRegex = Regex("([A-Za-z][A-Za-z0-9_]*)")
        private const val maxMacroDepth = 10
        private val precisionStrings = setOf("lowp", "mediump", "highp")
        private val modifierStrings = setOf("const") + precisionStrings
        private val varDirections = setOf("in", "out", "inout")

        private fun Collection<Token>.contains(value: String): Boolean {
            return any { it.text == value }
        }
    }
}