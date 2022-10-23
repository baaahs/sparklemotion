package baaahs.gl.shader.dialect

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.plugin.Plugins
import baaahs.show.Shader

interface ShaderDialect {
    val id: String
    val title: String

    fun match(glslCode: GlslCode, plugins: Plugins): ShaderAnalyzer
    fun analyze(glslCode: GlslCode, plugins: Plugins, shader: Shader? = null): ShaderAnalysis
}

interface ShaderAnalyzer {
    val matchLevel: MatchLevel
}

class BaseShaderAnalyzer(
    override val matchLevel: MatchLevel
) : ShaderAnalyzer

enum class MatchLevel {
    NoMatch,
    Poor,
    Good,
    Excellent
}