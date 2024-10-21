package baaahs.gl.shader.dialect

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.gl.shader.InputPort
import baaahs.plugin.Pluggable
import baaahs.plugin.Plugins
import baaahs.show.Shader

interface ShaderDialect : Pluggable {
    val id: String
    val title: String
    val wellKnownInputPorts: List<InputPort>

    fun match(glslCode: GlslCode, plugins: Plugins): ShaderAnalyzer
}

interface ShaderAnalyzer {
    val dialect: ShaderDialect
    val matchLevel: MatchLevel

    fun analyze(existingShader: Shader? = null): ShaderAnalysis
}

enum class MatchLevel {
    NoMatch,
    Poor,
    Good,
    Excellent
}