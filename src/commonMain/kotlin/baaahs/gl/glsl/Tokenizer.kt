package baaahs.gl.glsl

class Tokenizer {
    var lineNumber = 1
    var lineNumberForError = 1

    fun tokenize(text: String): Sequence<Token> {
        val tokenizationPattern = Regex(
            "//" +               // Line comment
                    "|/\\*" +           // Block comment start
                    "|\\*/" +           // Block comment end
                    "|\n" +             // Newline
                    "|[^\\S\\r\\n]+" +  // Whitespace but *not* newline
                    "|\\w+" +
                    "|."
        )
        return tokenizationPattern.findAll(text).map {
            val s = it.groupValues[0]
            Token(s, lineNumber).also {
                if (s == "\n") lineNumber++
            }
        }
    }

    fun <S: State<S>> processTokens(text: String, initialState: S): S =
        processTokens(tokenize(text), initialState)

    fun <S: State<S>> processTokens(
        tokens: Sequence<Token>,
        initialState: S
    ): S {
        var state = initialState
        tokens.forEach { token ->
            if (token.text.isNotEmpty())
                state = state.visit(token)
        }
        return state
    }
}

class Token(
    val text: String,
    /** 1-based line number. */
    val lineNumber: Int
) {
    override fun toString(): String = text
}