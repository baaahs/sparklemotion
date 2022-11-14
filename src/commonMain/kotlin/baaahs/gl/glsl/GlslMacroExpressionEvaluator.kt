package baaahs.gl.glsl

import baaahs.util.Logger
import com.github.murzagalin.evaluator.Constant
import com.github.murzagalin.evaluator.Evaluator

object GlslMacroExpressionEvaluator {
    fun evaluate(expression: String): Boolean {
        val evaluator = Evaluator(
            emptyList(),
            listOf(
                Constant("__VERSION__", 330.0)
            )
        )
        return evaluator.evaluateBoolean(expression)
            .also {
                logger.info { "$expression -> $it" }
            }
    }

    private val logger = Logger<GlslMacroExpressionEvaluator>()
}