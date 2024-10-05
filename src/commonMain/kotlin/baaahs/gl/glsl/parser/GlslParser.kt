package baaahs.gl.glsl.parser

import baaahs.gl.glsl.GlslCode

class GlslParser {
    fun parse(src: String, fileName: String? = null): GlslCode {
        val tokenizer = Tokenizer(src)
        val context = Context(tokenizer)
        val initialState = UnidentifiedStatement(context)

        processTokens(tokenizer, initialState)
            .visitEof(Token("", -1))
        return GlslCode(src, context.statements, fileName)
    }

    private fun <S: State<S>> processTokens(
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

    companion object {
        private val wordRegex = Regex("([A-Za-z][A-Za-z0-9_]*)")
        const val maxMacroDepth = 10
        val precisionStrings = setOf("lowp", "mediump", "highp")
        val modifierStrings = setOf("const") + precisionStrings
        val varDirections = setOf("in", "out", "inout")
    }
}