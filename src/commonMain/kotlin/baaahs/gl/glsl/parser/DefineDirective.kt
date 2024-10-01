package baaahs.gl.glsl.parser

class DefineDirective(
    context: Context,
    private val startToken: Token,
    private val priorParseState: ParseState
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