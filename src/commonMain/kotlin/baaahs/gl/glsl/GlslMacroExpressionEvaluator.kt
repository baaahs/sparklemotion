package baaahs.gl.glsl

import baaahs.gl.glsl.parser.Token
import baaahs.util.Logger
import com.github.murzagalin.evaluator.Evaluator

object GlslMacroExpressionEvaluator {
    private val evaluator = Evaluator(emptyList(), emptyList())

    fun evaluate(expression: List<Token>): Boolean {
        val exprStr = expression
            .filter { it.text.isNotBlank() }
            .joinToString("") { it.text }

        return evaluator.evaluateBoolean(exprStr)
            .also { logger.debug { "$exprStr -> $it" } }
    }

    private val logger = Logger<GlslMacroExpressionEvaluator>()
}