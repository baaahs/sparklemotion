package baaahs.gl.glsl.parser

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType

class Function(
    tokensSoFar: List<Token>,
    context: Context
) : Statement(context) {
    private val returnType: GlslType
    val name: Token
    val params = arrayListOf<GlslCode.GlslParam>()
    private var isAbstract = true

    init {
        val leadingModifierCount =
            tokensSoFar.indexOfFirst { !GlslParser.precisionStrings.contains(it.text) }

        if (tokensSoFar.size - leadingModifierCount != 2)
            throw context.glslError("unexpected tokens $tokensSoFar in function")

        returnType = context.findType(tokensSoFar[leadingModifierCount].text)
        name = tokensSoFar[leadingModifierCount + 1]
    }

    override fun visitLeftCurlyBrace(token: Token): ParseState {
        isAbstract = false
        return super.visitLeftCurlyBrace(token)
    }

    override fun createStatement(): GlslCode.GlslStatement =
        GlslCode.GlslFunction(name.text, returnType, params, tokensAsString, lineNumber, comments, isAbstract)

    inner class Params(
        recipientOfNextComment: Statement? = null
    ) : Statement(context, recipientOfNextComment) {
        var qualifier: String? = null
        var type: GlslType? = null
        var name: String? = null
        private var inArraySize = false

        override fun appendText(value: Token) {
            this@Function.appendText(value)
        }

        override fun visitText(token: Token): ParseState {
            val trimmed = token.text.trim()
            when {
                trimmed.isEmpty() -> {} // Ignore.

                GlslParser.modifierStrings.contains(trimmed) -> {} // Ignore.

                inArraySize -> {
                    val macro = context.macros[trimmed]
                    // Very confused about this, but without it we get double macro expansion in the name:
                    if (macro == null) name += trimmed
                }

                qualifier == null && (GlslParser.varDirections.contains(trimmed)) -> {
                    qualifier = trimmed
                }

                type == null -> type = context.findType(trimmed)

                name == null -> name = trimmed

                else -> throw context.glslError("Unexpected token \"$trimmed\".")
            }

            return super.visitText(token)
        }

        override fun visitComma(token: Token): ParseState {
            addParam()
            appendText(token)
            return Params(this)
        }

        override fun visitLeftBracket(token: Token): ParseState {
            if (inArraySize) error("Already in param array size.")
            inArraySize = true
            name += "["
            return super.visitText(token)
        }

        override fun visitRightBracket(token: Token): ParseState {
            if (!inArraySize) error("Not in param array size.")
            inArraySize = false
            name += "]"
            return super.visitText(token)
        }

        override fun visitRightParen(token: Token): ParseState {
            addParam()
            appendText(token)
            return this@Function
        }

        override fun visitNewline(token: Token): ParseState {
            appendText(token)
            receiveSubsequentComments()
            return this
        }

        override fun createStatement(): GlslCode.GlslStatement {
            error("huh?")
        }

        fun addParam() {
            val type = type
            val name = name

            if (type == GlslType.Void) return
            if (name == null && type == null) return

            if (type == null) {
                throw context.glslError("No type for parameter in ${this@Function.name}().")
            } else if (name == null) {
                throw context.glslError("No name for parameter in ${this@Function.name}().")
            }

            params.add(
                GlslCode.GlslParam(
                    name,
                    type,
                    isIn = qualifier == null || qualifier == "in" || qualifier == "inout",
                    isOut = qualifier == "out" || qualifier == "inout",
                    lineNumber = lineNumber,
                    comments = comments
                )
            )
        }
    }
}