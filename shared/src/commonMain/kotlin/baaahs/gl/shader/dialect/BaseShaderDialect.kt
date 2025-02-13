package baaahs.gl.shader.dialect

import baaahs.gl.shader.ShaderStatementRewriter
import baaahs.gl.shader.ShaderSubstitutions

abstract class BaseShaderDialect(
    override val id: String
) : ShaderDialect {
    override fun buildStatementRewriter(substitutions: ShaderSubstitutions) =
        ShaderStatementRewriter(substitutions)
}