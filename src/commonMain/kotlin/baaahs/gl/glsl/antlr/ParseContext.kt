package baaahs.gl.glsl.antlr

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import org.antlr.v4.kotlinruntime.TokenStream

class ParseContext(
    private val commentCollector: CommentCollector,
    val tokenStream: TokenStream
) {
    fun visitTypeSpecifier(ctx: GLSLParser.Type_specifierContext) =
        TypeSpecifierVisitor(this, ctx).type

    fun visitQualifiers(ctx: GLSLParser.Type_qualifierContext?) =
        TypeQualifierVisitor(this, ctx)

    fun visitFunctionPrototype(ctx: GLSLParser.Function_prototypeContext): GlslCode.GlslFunction =
        FunctionPrototypeVisitor(this, ctx).function

    fun visitFunctionDefinition(ctx: GLSLParser.Function_definitionContext): GlslCode.GlslFunction =
        FunctionDefinitionVisitor(this, ctx).function

    fun visitParameterDeclaration(ctx: GLSLParser.Parameter_declarationContext) =
        ParameterDeclarationVisitor(this, ctx).parameter

    fun commentsForLine(lineNumber: Int?): List<String> =
        commentCollector.commentsForLine(lineNumber ?: GlslError.NO_LINE)
}