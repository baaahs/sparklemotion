package baaahs.show

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.gl.preview.PreviewShaders
import baaahs.gl.shader.OpenShader
import baaahs.show.mutable.MutableShader
import baaahs.ui.Icon

interface ShaderType {
    val title: String
    val icon: Icon
    val template: String
    val injectUvCoordinateForPreview: Boolean get() = true

    fun newShaderFromTemplate(): MutableShader {
        return MutableShader("Untitled $title Shader", template)
    }

    fun matches(shaderAnalysis: ShaderAnalysis): MatchLevel

    fun pickPreviewShaders(openShader: OpenShader, previewShaders: PreviewShaders): List<OpenShader>

    object Unknown : ShaderType {
        override val title: String get() = "Unknown"
        override val icon: Icon get() = CommonIcons.UnknownShader
        override val template: String get() = error("n/a")

        override fun matches(shaderAnalysis: ShaderAnalysis): MatchLevel = MatchLevel.NoMatch

        override fun pickPreviewShaders(openShader: OpenShader, previewShaders: PreviewShaders): List<OpenShader> {
            return listOf(openShader)
        }
    }

    enum class MatchLevel {
        NoMatch,
        Match,
        MatchAndFilter // This is dumb, figure out something better.
    }
}