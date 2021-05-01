package baaahs.gl.patch

import baaahs.gl.glsl.GlslExpr
import baaahs.gl.glsl.GlslType

interface Component {
    val title: String
    val outputVar: String?
    val resultType: GlslType

    val invokeFromMain: Boolean

    fun appendStructs(buf: StringBuilder)
    fun appendDeclarations(buf: StringBuilder)
    fun appendInvokeAndSet(buf: StringBuilder, injectionParams: Map<String, ContentType> = emptyMap())

    fun getExpression(prefix: String): GlslExpr

    fun getInit(): String? = null
}