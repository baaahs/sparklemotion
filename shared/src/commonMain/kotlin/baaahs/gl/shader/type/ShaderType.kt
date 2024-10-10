package baaahs.gl.shader.type

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.gl.patch.ContentType
import baaahs.gl.preview.PreviewShaders
import baaahs.gl.preview.QuadPreviewBootstrapper
import baaahs.gl.preview.ShaderPreviewBootstrapper
import baaahs.gl.shader.OpenShader
import baaahs.show.mutable.MutableShader
import baaahs.ui.Icon

interface ShaderType {
    val title: String
    val icon: Icon
    val displayOrder: Float
    val template: String
    val injectUvCoordinateForPreview: Boolean get() = true
    val shaderPreviewBootstrapper: ShaderPreviewBootstrapper

    fun newShaderFromTemplate(): MutableShader {
        return MutableShader("Untitled $title Shader", template)
    }

    fun matches(shaderAnalysis: ShaderAnalysis): MatchLevel

    fun pickPreviewShaders(openShader: OpenShader, previewShaders: PreviewShaders): List<OpenShader>

    fun previewResultContentType(): ContentType = ContentType.Color


    fun ShaderAnalysis.anyInputIs(contentType: ContentType) =
        inputPorts.any { it.contentType == contentType && it.pluginRef == null }

    fun ShaderAnalysis.outputIs(contentType: ContentType) =
        outputPorts.firstOrNull()?.contentType == contentType

    fun ShaderAnalysis.signatureMatches(inputContentType: ContentType, outputContentType: ContentType) =
        outputIs(outputContentType) && anyInputIs(inputContentType)

    object Unknown : ShaderType {
        override val title: String get() = "Unknown"
        override val icon: Icon get() = CommonIcons.UnknownShader
        override val displayOrder: Float = Float.MAX_VALUE
        override val template: String get() = error("n/a")
        override val shaderPreviewBootstrapper: ShaderPreviewBootstrapper
            get() = QuadPreviewBootstrapper // TODO: Something else, what?

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