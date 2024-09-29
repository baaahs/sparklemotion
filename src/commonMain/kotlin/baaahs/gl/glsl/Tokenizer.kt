package baaahs.gl.glsl

class Tokenizer {
    var lineNumber = 1
    var lineNumberForError = 1

    fun tokenize(text: String): Sequence<String> {
        val tokenizationPattern = Regex(
            "//" +               // Line comment
                    "|/\\*" +           // Block comment start
                    "|\\*/" +           // Block comment end
                    "|\n" +             // Newline
                    "|[^\\S\\r\\n]+" +  // Whitespace but *not* newline
                    "|\\w+" +
                    "|."
        )
        return tokenizationPattern.findAll(text).map { it.groupValues[0] }
    }

    fun <S: State<S>> processTokens(
        text: String,
        initialState: S,
        freezeLineNumber: Boolean = false
    ): S {
        var state = initialState
        tokenize(text).forEach { token ->
            if (!freezeLineNumber && token == "\n") lineNumber++
            lineNumberForError = lineNumber

            if (token.isNotEmpty()) state = state.visit(token)
        }
        return state
    }

    interface State<S: State<S>> {
        fun visit(token: String): S
    }
}