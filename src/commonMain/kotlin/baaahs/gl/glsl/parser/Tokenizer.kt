package baaahs.gl.glsl.parser

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
}

class Token(
    val text: String,
    /** 1-based line number. */
    val lineNumber: Int
) {
    override fun toString(): String = text

    companion object {
        fun Collection<Token>.contains(value: String): Boolean {
            return any { it.text == value }
        }
    }
}