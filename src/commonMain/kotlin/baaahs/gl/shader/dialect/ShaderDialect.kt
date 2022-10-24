package baaahs.gl.shader.dialect

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.plugin.Plugins
import baaahs.show.Shader

interface ShaderDialect {
    val id: String
    val title: String

    fun match(glslCode: GlslCode, plugins: Plugins): ShaderAnalyzer
}

interface ShaderAnalyzer {
    val dialect: ShaderDialect
    val matchLevel: MatchLevel

    fun analyze(shader: Shader? = null): ShaderAnalysis
}

enum class MatchLevel {
    NoMatch,
    Poor,
    Good,
    Excellent
}