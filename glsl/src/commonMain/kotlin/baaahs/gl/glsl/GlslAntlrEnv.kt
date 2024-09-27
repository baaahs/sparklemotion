package baaahs.gl.glsl

import baaahs.gl.glsl.antlr.GLSLLexer
import baaahs.gl.glsl.antlr.GLSLParser
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.IntStream

class GlslAntlrEnv(
    val src: String,
    val fileName: String? = null
) {
    val lexer = GLSLLexer(CharStreams.fromString(src, fileName ?: IntStream.UNKNOWN_SOURCE_NAME))
    val tokens = CommonTokenStream(lexer)
    val parser = GLSLParser(tokens)
}