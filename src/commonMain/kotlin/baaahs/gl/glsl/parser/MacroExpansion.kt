package baaahs.gl.glsl.parser

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