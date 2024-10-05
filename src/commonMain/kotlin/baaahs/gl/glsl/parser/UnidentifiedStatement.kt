package baaahs.gl.glsl.parser

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.parser.Token.Companion.contains

class UnidentifiedStatement(
    context: Context,
    recipientOfNextComment: Statement? = null
) : Statement(context, recipientOfNextComment) {
    private val tokensSoFar = mutableListOf<Token>()

    override fun visitLeftParen(token: Token): ParseState {
        return if (context.outputEnabled && braceNestLevel == 0 && !tokensSoFar.contains("=")) {
            val fn = Function(tokensSoFar, context)
            fn.copyFrom(this)
            fn.appendText(token)
            fn.Params()
        } else {
            super.visitLeftParen(token)
        }
    }

    override fun visitText(token: Token): ParseState {
        return if (token.text == "struct") {
            Struct(context)
                .copyFrom(this)
                .apply { appendText(token) }
        } else {
            if (context.outputEnabled && token.text.isNotBlank()) {
                tokensSoFar.add(token)
            }

            if (tokens.isEmpty() && token.text.isBlank()) {
                this
            } else {
                super.visitText(token)
            }
        }
    }

    override fun createStatement(): GlslCode.GlslStatement {
        return asSpecialOrNull()
            ?: asVarOrNull()
            ?: GlslCode.GlslOther("unknown", tokensAsString, lineNumber)
    }
}