package baaahs.gl.glsl.parser

class PreDirective(
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