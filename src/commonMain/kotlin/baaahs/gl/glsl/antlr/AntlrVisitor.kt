package baaahs.gl.glsl.antlr

import baaahs.gl.glsl.AnalysisException
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslException
import org.antlr.v4.kotlinruntime.ParserRuleContext

class AntlrVisitor(
    private val parseContext: ParseContext
) : GLSLParserBaseVisitor<AntlrVisitor>() {
    private val statements = mutableListOf<GlslCode.GlslStatement>()
    private val uniqueTokens = mutableSetOf<String>()

    override fun defaultResult(): AntlrVisitor = this

    override fun visitSingle_declaration(ctx: GLSLParser.Single_declarationContext): AntlrVisitor {
        val qualifiers = parseContext.visitQualifiers(ctx.fully_specified_type().type_qualifier())
        val name = ctx.typeless_declaration()?.IDENTIFIER()?.text
            ?: throw glslError("No identifier.", ctx)
        val type = parseContext.visitTypeSpecifier(ctx.fully_specified_type().type_specifier())

        val lineNumber = ctx.position?.start?.line
        statements.add(GlslCode.GlslVar(
            name,
            type,
            fullText = ctx.text,
            isConst = qualifiers.isConst,
            isUniform = qualifiers.isUniform,
            isVarying = qualifiers.isVarying,
            initExpr = ctx.typeless_declaration()?.initializer()?.text,
            lineNumber,
            comments = parseContext.commentsForLine(lineNumber)
        ))

        return super.visitSingle_declaration(ctx)
    }

    override fun visitFunction_prototype(ctx: GLSLParser.Function_prototypeContext): AntlrVisitor {
        statements.add(parseContext.visitFunctionPrototype(ctx))
        return this
    }

    override fun visitFunction_definition(ctx: GLSLParser.Function_definitionContext): AntlrVisitor {
        statements.add(parseContext.visitFunctionDefinition(ctx))
        return this
    }

    fun getGlslCode(src: String, fileName: String?): GlslCode {
        return GlslCode(src, statements, fileName)
    }
}

fun glslError(message: String, ctx: ParserRuleContext): GlslException =
    AnalysisException(message, ctx.position?.start?.line ?: GlslError.NO_LINE)