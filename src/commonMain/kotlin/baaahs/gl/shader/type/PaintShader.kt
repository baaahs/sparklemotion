package baaahs.gl.shader.type

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.gl.patch.ContentType
import baaahs.gl.preview.PreviewShaders
import baaahs.gl.shader.OpenShader
import baaahs.ui.Icon

object PaintShader : ShaderType {
    override val title: String = "Filter"

    override val icon: Icon = CommonIcons.FilterShader

    override val template: String = """
        // @return color
        // @param uvIn uv-coordinate
        vec4 main(vec2 uvIn) {
            return vec4(uvIn, 0., 1.);
        }
    """.trimIndent()

    override fun matches(shaderAnalysis: ShaderAnalysis): ShaderType.MatchLevel {
        return if (shaderAnalysis.outputIs(ContentType.Color))
            ShaderType.MatchLevel.Match
        else ShaderType.MatchLevel.NoMatch
    }

    override fun pickPreviewShaders(openShader: OpenShader, previewShaders: PreviewShaders): List<OpenShader> {
        return listOf(previewShaders.screenCoordsProjection, openShader)
    }
}