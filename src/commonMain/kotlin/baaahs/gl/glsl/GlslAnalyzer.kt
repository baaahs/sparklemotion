package baaahs.gl.glsl

import baaahs.gl.glsl.GlslCode.GlslFunction
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.gl.shader.dialect.GenericShaderDialect
import baaahs.gl.shader.dialect.MatchLevel
import baaahs.gl.shader.dialect.ShaderDialect
import baaahs.gl.shader.type.ShaderType
import baaahs.only
import baaahs.plugin.Plugins
import baaahs.show.Shader

class GlslAnalyzer(private val plugins: Plugins) {
    fun analyze(glslCode: GlslCode, shader: Shader? = null): ShaderAnalysis {
        val dialect = detectDialect(glslCode)
        return dialect.analyze(glslCode, plugins, shader)
    }

    fun openShader(glslCode: GlslCode, shader: Shader? = null): OpenShader {
        val shaderAnalysis = analyze(glslCode, shader)
        return openShader(shaderAnalysis)
    }

    fun openShader(shaderAnalysis: ShaderAnalysis): OpenShader {
        val shaderType = detectShaderType(shaderAnalysis)

        return with(shaderAnalysis) {
            if (shaderAnalysis.isValid) {
                OpenShader.Base(this.shader, shaderAnalysis.glslCode,
                    entryPoint!!, inputPorts, outputPorts.only(),
                    shaderType, shaderDialect)
            } else {
                OpenShader.Base(this.shader, shaderAnalysis.glslCode,
                    entryPoint ?: GlslFunction("invalid", GlslType.Void, emptyList(), ""),
                    inputPorts,
                    if (outputPorts.size == 1) outputPorts.first() else OutputPort(ContentType.Unknown),
                    shaderType,
                    shaderDialect,
                    errors
                )
            }
        }
    }

    fun detectDialect(glslCode: GlslCode): ShaderDialect =
        plugins.shaderDialects.all
            .map { it to it.match(glslCode, plugins) }
            .filter { (_, analyzer) -> analyzer.matchLevel != MatchLevel.NoMatch }
            .maxByOrNull { (_, analyzer) -> analyzer.matchLevel }?.first
            ?: GenericShaderDialect

    fun detectShaderType(shaderAnalysis: ShaderAnalysis) =
        plugins.shaderTypes.all
            .map { it to it.matches(shaderAnalysis) }
            .filter { (_, match) -> match != ShaderType.MatchLevel.NoMatch }
            .maxByOrNull { (_, match) -> match }?.first
            ?: ShaderType.Unknown
}