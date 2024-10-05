package baaahs.gl.glsl.parser

class BlockComment(
    context: Context,
    private val startToken: Token,
    private val recipientOfComment: ParseState?,
    private val nextParseState: ParseState,
    val chompNewlines: Boolean = false
) : ParseState(context) {
    private val commentText = mutableListOf<Token>()

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