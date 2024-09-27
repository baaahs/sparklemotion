package baaahs.gl.glsl.antlr

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType

class FunctionPrototypeVisitor(
    private val parseContext: ParseContext,
    ctx: GLSLParser.Function_prototypeContext
) : GLSLParserBaseVisitor<FunctionPrototypeVisitor>() {
    lateinit var function: GlslCode.GlslFunction

    init { ctx.accept(this) }

    override fun visitFunction_prototype(ctx: GLSLParser.Function_prototypeContext): FunctionPrototypeVisitor {
        val returnType = TypeSpecifierVisitor(parseContext, ctx.fully_specified_type().type_specifier()).type
        val name = ctx.IDENTIFIER().text
        val params = ctx.function_parameters()?.parameter_declaration()?.map {
            parseContext.visitParameterDeclaration(it)
        }?.filterNot { it.type == GlslType.Void } ?: emptyList()
        val lineNumber = ctx.position?.start?.line
        function = GlslCode.GlslFunction(
            name,
            returnType,
            params,
            ctx.text,
            lineNumber,
            isAbstract = true,
            comments = parseContext.commentsForLine(lineNumber)

        )
        return this
    }

    override fun defaultResult(): FunctionPrototypeVisitor = this
}