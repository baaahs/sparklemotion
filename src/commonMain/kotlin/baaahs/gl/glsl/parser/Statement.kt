package baaahs.gl.glsl.parser

import baaahs.gl.glsl.GlslCode

abstract class Statement(
    context: Context,
    private var recipientOfNextComment: Statement? = null
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