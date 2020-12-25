package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.plugin.Plugins
import baaahs.show.Shader

abstract class ShaderDialect(val id: String) {

    abstract val title: String

    abstract fun matches(glslCode: GlslCode): MatchLevel

    abstract fun analyze(glslCode: GlslCode, plugins: Plugins, shader: Shader? = null): ShaderAnalysis
}

enum class MatchLevel {
    NoMatch,
    Poor,
    Good,
    Excellent
}