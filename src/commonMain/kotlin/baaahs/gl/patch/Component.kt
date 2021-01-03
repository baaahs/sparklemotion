package baaahs.gl.patch

import baaahs.gl.glsl.GlslType

interface Component {
    val title: String
    val outputVar: String?
    val resultType: GlslType

    fun appendStructs(buf: StringBuilder)
    fun appendDeclarations(buf: StringBuilder)
    fun appendInvokeAndSet(buf: StringBuilder, prefix: String)

    fun getExpression(): String
}