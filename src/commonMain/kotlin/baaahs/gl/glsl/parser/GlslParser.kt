package baaahs.gl.glsl.parser

import baaahs.gl.glsl.GlslCode

class GlslParser {
    fun parse(src: String, fileName: String? = null): GlslCode {
        val context = Context()
        val initialState = UnidentifiedStatement(context)
        context.parse(src, initialState)
            .visitEof(Token("", -1))
        return GlslCode(src, context.statements, fileName)
    }

    companion object {
        private val wordRegex = Regex("([A-Za-z][A-Za-z0-9_]*)")
        const val maxMacroDepth = 10
        val precisionStrings = setOf("lowp", "mediump", "highp")
        val modifierStrings = setOf("const") + precisionStrings
        val varDirections = setOf("in", "out", "inout")
    }
}