package baaahs.gl.glsl.parser

class Tokenizer(
    text: String,
    tokenizationPattern: Regex = GLSL_TOKENIZATION
) : Sequence<Token> {
    var lineNumber = 1

    private val tokenStream = tokenizationPattern.findAll(text).map {
        val s = it.groupValues[0]
        Token(s, lineNumber).also {
            if (s == "\n") lineNumber++
        }
    }
    private val tokenIterators = mutableListOf(tokenStream.iterator())

    override fun iterator(): Iterator<Token> =
        object : Iterator<Token> {
            override fun hasNext(): Boolean {
                while (tokenIterators.size > 0) {
                    if (tokenIterators.last().hasNext()) {
                        return true
                    } else {
                        tokenIterators.removeLast()
                    }
                }
                return false
            }

            override fun next(): Token =
                tokenIterators.last().next()
        }

    fun push(tokens: List<Token>) {
        tokenIterators.add(tokens.iterator())
    }

    companion object {
        val GLSL_TOKENIZATION = Regex(
            "//" +               // Line comment
                    "|/\\*" +           // Block comment start
                    "|\\*/" +           // Block comment end
                    "|\n" +             // Newline
                    "|[^\\S\\r\\n]+" +  // Whitespace but *not* newline
                    "|\\w+" +
                    "|."
        )
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