package baaahs.gl.glsl.antlr

import org.antlr.v4.kotlinruntime.BufferedTokenStream
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.TokenStream

class CommentCollector(lexer: GLSLLexer) {
    val commentsByLine: Map<Int, List<Comment>>

    init {
        val tokenStream = BufferedTokenStream(lexer)

        val commentsByLine = mutableMapOf<Int, MutableList<Comment>>()
        var commentAccumulator = mutableListOf<Comment>()
        var lastToken: Token? = null

        tokenStream.forEach { token ->
            if (token.isComment) {
                val comment = Comment(
                    token.text ?: throw IllegalStateException("No text in comment token."),
                    token.startPoint().line,
                    token.startPoint().column
                )

                val lastTokenLine = lastToken?.line
                if (lastTokenLine == token.line) {
                    commentsByLine.getOrPut(lastTokenLine) { mutableListOf() }
                        .add(comment)
                } else {
                    commentAccumulator.add(comment)
                }
            } else if (token.isNewline && lastToken?.isComment == true) {
                // Ignore newlines that follow comments.
            } else {
                if (commentAccumulator.isNotEmpty()) {
                    commentsByLine[token.line] = commentAccumulator
                    commentAccumulator = mutableListOf()
                }
            }
            lastToken = token
            println("${token.startPoint().line},${token.startPoint().column} ${token.channel}: ${token.text}")
        }

        this.commentsByLine = commentsByLine
    }

    private val Token.isNewline get() =
        channel == Token.HIDDEN_CHANNEL && text == "\n"

    private val Token.isComment get() =
        channel == GLSLLexer.Channels.COMMENTS

    private inline fun TokenStream.forEach(block: (Token) -> Unit) {
        var token: Token? = LT(1) // Look at the current token (LT(1) gives the current token)
        while (token != null && token.type != Token.EOF) {
            block(token)

            consume()
            token = LT(1)
        }
    }

    fun commentsForLine(lineNumber: Int): List<String> =
        commentsByLine[lineNumber]?.map { it.stripCommentChars() } ?: emptyList()
}

data class Comment(val text: String, val line: Int, val column: Int) {
    fun stripCommentChars(): String =
        text.trim().removePrefix("//").trim()
}