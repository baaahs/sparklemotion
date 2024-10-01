package baaahs.gl.glsl.parser

import baaahs.gl.glsl.GlslCode

class GlslParser {
    fun parse(src: String, fileName: String? = null): GlslCode {
        return GlslCode(src, findStatements(src), fileName)
    }

    internal fun findStatements(glslSrc: String): List<GlslCode.GlslStatement> {
        val context = Context()
        context.parse(glslSrc, ParseState.initial(context))
            .visitEof(Token("", -1))
        return context.statements
    }

    companion object {
        private val wordRegex = Regex("([A-Za-z][A-Za-z0-9_]*)")
        const val maxMacroDepth = 10
        val precisionStrings = setOf("lowp", "mediump", "highp")
        val modifierStrings = setOf("const") + precisionStrings
        val varDirections = setOf("in", "out", "inout")
    }
}