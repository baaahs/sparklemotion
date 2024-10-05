package baaahs.gl.glsl.parser

class PreDirective(
    context: Context,
    val priorParseState: ParseState
) : ParseState(context) {
    private inner class Directive(
        var quoteFirstArgument: Boolean = false,
        val callback: (List<Token>) -> Unit
    ) : ParseState(context) {
        override fun visitText(token: Token): ParseState =
            if (token.text.isNotBlank() && quoteFirstArgument) {
                appendText(token)
                quoteFirstArgument = false
                this
            } else checkForMacro(token)

        override fun visitNewline(token: Token): ParseState {
//            val str = tokensAsString.trim()
//            val args = str.split(Regex("\\s+")).let {
                // "".split("\\s+") returns [""].
//                if (it == listOf("")) emptyList() else it
//            }
            callback(tokens.noWhitespace())
            priorParseState.visitText(token)
            return priorParseState
        }

        override fun visitEof(token: Token): ParseState =
            visitNewline(token)
    }

    override fun visitText(token: Token): ParseState =
        when (token.text) {
            "define" -> DefineDirective(context, token, priorParseState)
            "undef" -> Directive(quoteFirstArgument = true) { context.doUndef(token, it) }
            "if" -> Directive { context.doIf(token, it) }
            "ifdef" -> Directive(quoteFirstArgument = true) { context.doIfdef(token, it) }
            "ifndef" -> Directive(quoteFirstArgument = true) { context.doIfndef(token, it) }
            "else" -> Directive { context.doElse(token, it) }
            "elif" -> Directive { context.doElif(token, it) }
            "endif" -> Directive { context.doEndif(token, it) }
            "error", "pragma", "extension", "version" ->
                throw context.glslError(token, "unsupported directive #${token.text}")

            "line" -> Directive(quoteFirstArgument = true) { context.doLine(token, it) }
            else -> throw context.glslError(token, "unknown directive #${token.text}")
        }

    fun Collection<Token>.noWhitespace(): List<Token> =
        filter { it.text.isNotBlank() }
}