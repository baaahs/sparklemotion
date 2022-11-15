package baaahs.gl.glsl

import baaahs.util.Logger
import com.github.murzagalin.evaluator.Evaluator

object GlslMacroExpressionEvaluator {
    private val evaluator = Evaluator(emptyList(), emptyList())

    fun evaluate(expression: String): Boolean {
        return evaluator.evaluateBoolean(expression)
            .also { logger.debug { "$expression -> $it" } }
    }

    private val logger = Logger<GlslMacroExpressionEvaluator>()
}