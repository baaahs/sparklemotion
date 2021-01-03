package baaahs.gl.shader.type

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.gl.preview.PreviewShaders
import baaahs.gl.shader.OpenShader
import baaahs.plugin.core.MovingHeadParams
import baaahs.ui.Icon

object MoverShader : ShaderType {
    override val title: String = "Mover"

    override val icon: Icon = CommonIcons.None

    override val template: String = """
        vec4 main() {
            return vec4(0., .5);
        }
    """.trimIndent()

    override fun matches(shaderAnalysis: ShaderAnalysis): ShaderType.MatchLevel {
        return if (shaderAnalysis.outputIs(MovingHeadParams.contentType))
            ShaderType.MatchLevel.Match
        else ShaderType.MatchLevel.NoMatch
    }

    override fun pickPreviewShaders(openShader: OpenShader, previewShaders: PreviewShaders): List<OpenShader> {
        return listOf(openShader)
    }
}