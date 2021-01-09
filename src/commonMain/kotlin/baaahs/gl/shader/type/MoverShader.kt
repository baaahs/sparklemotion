package baaahs.gl.shader.type

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.gl.patch.ContentType
import baaahs.gl.preview.MovingHeadPreviewBootstrapper
import baaahs.gl.preview.PreviewShaders
import baaahs.gl.preview.ShaderPreviewBootstrapper
import baaahs.gl.shader.OpenShader
import baaahs.plugin.core.MovingHeadParams
import baaahs.ui.Icon

object MoverShader : ShaderType {
    override val title: String = "Mover"

    override val icon: Icon = CommonIcons.None

    /**language=glsl*/
    override val template: String = """
        struct MovingHeadParams {
            float pan;
            float tilt;
            float colorWheel;
            float dimmer;
        };
        
        // @param params moving-head-params
        void main(out MovingHeadParams params) {
            params.pan = 0.;
            params.tilt = .5;
            params.colorWheel = 0.;
        }
    """.trimIndent()

    override val shaderPreviewBootstrapper: ShaderPreviewBootstrapper
        get() = MovingHeadPreviewBootstrapper

    override fun matches(shaderAnalysis: ShaderAnalysis): ShaderType.MatchLevel {
        return if (shaderAnalysis.outputIs(MovingHeadParams.contentType))
            ShaderType.MatchLevel.Match
        else ShaderType.MatchLevel.NoMatch
    }

    override fun pickPreviewShaders(openShader: OpenShader, previewShaders: PreviewShaders): List<OpenShader> {
        return listOf(openShader)
    }

    override fun previewResultContentType(): ContentType = MovingHeadParams.contentType
}