package baaahs.gl.glsl.antlr

import baaahs.gl.glsl.AnalysisException
import baaahs.gl.glsl.GlslCode
import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.TokenStream

class FunctionDefinitionVisitor(
    private val parseContext: ParseContext,
    ctx: GLSLParser.Function_definitionContext
) : GLSLParserBaseVisitor<FunctionDefinitionVisitor>() {
    lateinit var function: GlslCode.GlslFunction

    init { ctx.accept(this) }

    override fun visitFunction_definition(ctx: GLSLParser.Function_definitionContext): FunctionDefinitionVisitor {
        val prototype = parseContext.visitFunctionPrototype(ctx.function_prototype())
        val body = ctx.compound_statement_no_new_scope().fullText(parseContext.tokenStream)
        function = prototype.copy(
            fullText = body,
            isAbstract = false
        )
        return this
    }

    override fun defaultResult(): FunctionDefinitionVisitor = this
}

fun ParserRuleContext.fullText(tokens: TokenStream): String = buildString {
    if (childCount == 0) return@buildString

    return tokens.getText(start, stop)
        ?: throw AnalysisException("huh? can't tokens.getText($start, $stop) for $this")
}