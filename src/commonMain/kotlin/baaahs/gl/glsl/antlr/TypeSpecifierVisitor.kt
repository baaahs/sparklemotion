package baaahs.gl.glsl.antlr

import baaahs.gl.glsl.GlslType

class TypeSpecifierVisitor(
    private val parseContext: ParseContext,
    ctx: GLSLParser.Type_specifierContext
) : GLSLParserBaseVisitor<TypeSpecifierVisitor>() {
    lateinit var type: GlslType

    init { ctx.accept(this) }

    override fun visitType_specifier_nonarray(ctx: GLSLParser.Type_specifier_nonarrayContext): TypeSpecifierVisitor {
        super.visitType_specifier_nonarray(ctx)
        if (!::type.isInitialized) {
            type = GlslType.from(ctx.text)
        }
        return this
    }

    override fun visitStruct_specifier(ctx: GLSLParser.Struct_specifierContext): TypeSpecifierVisitor {
        val name = ctx.IDENTIFIER()?.text ?: throw glslError("No identifier.", ctx)
        val fields = ctx.struct_declaration_list().struct_declaration().flatMap { structDeclaration ->
//          TODO:  val qualifier = parseContext.visitQualifiers(structDeclaration.type_qualifier())
            val type = parseContext.visitTypeSpecifier(structDeclaration.type_specifier())
            structDeclaration.struct_declarator_list().struct_declarator().map { structDeclarator ->
                GlslType.Field(
                    structDeclarator.IDENTIFIER().text,
                    type
                )
            }
        }
        this.type = GlslType.Struct(name, fields)
        return this
    }

    override fun visitArray_specifier(ctx: GLSLParser.Array_specifierContext): TypeSpecifierVisitor {
        ctx.dimension().map {
            it.constant_expression()?.text ?: throw glslError("No array size.", ctx)
        }.forEach { size ->
            type = type.arrayOf(size.toInt())
        }
        return super.visitArray_specifier(ctx)
    }

    override fun defaultResult(): TypeSpecifierVisitor = this

}