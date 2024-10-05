package baaahs.gl.glsl.parser

class Comment(
    context: Context,
    private val startToken: Token,
    private val recipientOfComment: ParseState?,
    private val nextParseState: ParseState
) : ParseState(context) {
    private val commentText = mutableListOf<Token>()

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