package baaahs.gl.glsl.antlr

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType

class ParameterDeclarationVisitor(
    private val parseContext: ParseContext,
    ctx: GLSLParser.Parameter_declarationContext
) : GLSLParserBaseVisitor<ParameterDeclarationVisitor>() {
    lateinit var parameter: GlslCode.GlslParam

    init { ctx.accept(this) }

    override fun visitParameter_declaration(ctx: GLSLParser.Parameter_declarationContext): ParameterDeclarationVisitor {
        val qualifiers = TypeQualifierVisitor(parseContext, ctx.type_qualifier())
        val typeSpecifier = ctx.parameter_type_specifier()?.type_specifier()
            ?: ctx.parameter_declarator()?.type_specifier()
            ?: throw glslError("No type specifier.", ctx)
        val type = parseContext.visitTypeSpecifier(typeSpecifier)
        val name = if (type == GlslType.Void) "void" else
            ctx.parameter_declarator()?.IDENTIFIER()?.text
            ?: throw glslError("No identifier.", ctx)
        parameter = GlslCode.GlslParam(
            name, type,
            isIn = qualifiers.isIn,
            isOut = qualifiers.isOut,
            lineNumber = ctx.position?.start?.line,
            comments = parseContext.commentsForLine(ctx.position?.start?.line)
        )
        return this
    }

    override fun defaultResult(): ParameterDeclarationVisitor = this
}