package baaahs.gl.glsl.antlr

import org.antlr.v4.kotlinruntime.tree.TerminalNode

class TypeQualifierVisitor(
    private val parseContext: ParseContext,
    private val ctx: GLSLParser.Type_qualifierContext?
) : GLSLParserBaseVisitor<TypeQualifierVisitor>() {
    init { ctx?.accept(this) }

    val isConst: Boolean get() = any { it.storage_qualifier()?.CONST() }
    val isUniform: Boolean get() = any { it.storage_qualifier()?.UNIFORM() }
    val isVarying: Boolean get() = any { it.storage_qualifier()?.VARYING() }
    val isIn: Boolean get() = any { it.storage_qualifier()?.IN() }
    val isOut: Boolean get() = any { it.storage_qualifier()?.OUT() }
    val isInOut: Boolean get() = any { it.storage_qualifier()?.INOUT() }

    private fun any(block: (GLSLParser.Single_type_qualifierContext) -> TerminalNode?) =
        ctx?.single_type_qualifier()?.any { block.invoke(it) != null }
            ?: false

    override fun defaultResult(): TypeQualifierVisitor = this
}