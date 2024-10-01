package baaahs.gl.glsl.parser

abstract class ParseState(val context: Context) : State<ParseState> {
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
}