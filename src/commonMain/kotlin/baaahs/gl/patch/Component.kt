package baaahs.gl.patch

import baaahs.gl.glsl.GlslExpr
import baaahs.gl.glsl.GlslType
import baaahs.gl.shader.InputPort

interface Component {
    val title: String
    val outputVar: String?
    val resultType: GlslType

    val invokeFromMain: Boolean

    val exportedStructs: List<GlslType.Struct> get() = emptyList()

    fun appendStructs(buf: ProgramBuilder, globalStructs: List<GlslType.Struct>) {}
    fun appendDeclarations(buf: ProgramBuilder, globalStructs: List<GlslType.Struct>) {}
    fun appendInvokeAndSet(buf: ProgramBuilder, injectionParams: Map<String, ContentType> = emptyMap()) {}
    fun appendInvokeAndReturn(buf: ProgramBuilder, inputPort: InputPort) = Unit

    fun getExpression(prefix: String): GlslExpr

    fun getInit(): String? = null
}