package baaahs.gl.shader

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.GlslType
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.gl.patch.ContentType
import baaahs.gl.preview.PreviewShaders
import baaahs.plugin.objectSerializer
import baaahs.show.ShaderType
import baaahs.ui.Icon
import kotlinx.serialization.SerialName

object DistortionShader : ShaderType {
    override val title: String = "Distortion"

    override val icon: Icon = CommonIcons.DistortionShader

    override val template: String = """
        uniform float scale; // @@Slider min=0.25 max=4 default=1

        vec2 main(vec2 uvIn) {
          return (uvIn - .5) / scale + .5;
        }
    """.trimIndent()

    override fun matches(shaderAnalysis: ShaderAnalysis): ShaderType.MatchLevel {
        return if (shaderAnalysis.outputPorts.firstOrNull()?.contentType == ContentType.UvCoordinate
            && shaderAnalysis.inputPorts.any { it.contentType == ContentType.UvCoordinate })
            ShaderType.MatchLevel.MatchAndFilter else ShaderType.MatchLevel.NoMatch
    }

    override fun pickPreviewShaders(openShader: OpenShader, previewShaders: PreviewShaders): List<OpenShader> {
        return listOf(previewShaders.screenCoordsProjection, openShader, previewShaders.smpteColorBars)
    }
}