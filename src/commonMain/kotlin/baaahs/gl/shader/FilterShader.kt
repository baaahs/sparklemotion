package baaahs.gl.shader

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.gl.patch.ContentType
import baaahs.gl.preview.PreviewShaders
import baaahs.show.ShaderType
import baaahs.ui.Icon

object FilterShader : ShaderType {
    override val title: String = "Filter"

    override val icon: Icon = CommonIcons.FilterShader

    override val template: String = """
        // @return color
        // @param inColor color
        vec4 main(vec4 inColor) {
            return inColor;
        }
    """.trimIndent()

    override fun matches(shaderAnalysis: ShaderAnalysis): ShaderType.MatchLevel {
        return if (shaderAnalysis.signatureMatches(ContentType.Color, ContentType.Color))
            ShaderType.MatchLevel.MatchAndFilter
        else ShaderType.MatchLevel.NoMatch
    }

    override fun pickPreviewShaders(openShader: OpenShader, previewShaders: PreviewShaders): List<OpenShader> {
        return listOf(previewShaders.screenCoordsProjection, openShader, previewShaders.smpteColorBars)
    }
}