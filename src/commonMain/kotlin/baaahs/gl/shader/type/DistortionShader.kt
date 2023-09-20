package baaahs.gl.shader.type

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.gl.patch.ContentType
import baaahs.gl.preview.PreviewShaders
import baaahs.gl.preview.QuadPreviewBootstrapper
import baaahs.gl.preview.ShaderPreviewBootstrapper
import baaahs.gl.shader.OpenShader
import baaahs.ui.Icon

object DistortionShader : ShaderType {
    override val title: String = "Distortion"

    override val icon: Icon = CommonIcons.DistortionShader

    override val displayOrder: Float = 0.5f

    override val template: String = """
        uniform float scale; // @@Slider min=0.25 max=4 default=1

        vec2 main(vec2 uvIn) {
          return (uvIn - .5) / scale + .5;
        }
    """.trimIndent()

    override val shaderPreviewBootstrapper: ShaderPreviewBootstrapper
        get() = QuadPreviewBootstrapper

    override fun matches(shaderAnalysis: ShaderAnalysis): ShaderType.MatchLevel {
        return if (shaderAnalysis.signatureMatches(ContentType.UvCoordinate, ContentType.UvCoordinate))
            ShaderType.MatchLevel.MatchAndFilter
        else ShaderType.MatchLevel.NoMatch
    }

    override fun pickPreviewShaders(openShader: OpenShader, previewShaders: PreviewShaders): List<OpenShader> {
        return listOf(previewShaders.screenCoordsProjection, openShader, previewShaders.backgroundImage)
    }
}