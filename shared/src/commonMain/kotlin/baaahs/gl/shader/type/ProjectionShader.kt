package baaahs.gl.shader.type

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.gl.patch.ContentType
import baaahs.gl.preview.PreviewShaders
import baaahs.gl.preview.ProjectionPreviewBootstrapper
import baaahs.gl.preview.ShaderPreviewBootstrapper
import baaahs.gl.shader.OpenShader
import baaahs.ui.Icon

object ProjectionShader : ShaderType {
    override val title: String = "Projection"

    override val icon: Icon = CommonIcons.ProjectionShader

    override val displayOrder: Float = 0f

    override val template: String = """
        struct ModelInfo {
            vec3 center;
            vec3 extents;
        };
        uniform ModelInfo modelInfo;

        // @return uv-coordinate
        // @param pixelLocation xyz-coordinate
        vec2 main(vec3 pixelLocation) {
            vec3 start = modelInfo.center - modelInfo.extents / 2.;
            vec3 rel = (pixelLocation - start) / modelInfo.extents;
            return rel.xy;
        }
    """.trimIndent()

    override val injectUvCoordinateForPreview: Boolean get() = false

    override val shaderPreviewBootstrapper: ShaderPreviewBootstrapper
        get() = ProjectionPreviewBootstrapper

    override fun matches(shaderAnalysis: ShaderAnalysis): ShaderType.MatchLevel {
        return if (shaderAnalysis.signatureMatches(ContentType.XyzCoordinate, ContentType.UvCoordinate))
            ShaderType.MatchLevel.Match
        else ShaderType.MatchLevel.NoMatch
    }

    override fun pickPreviewShaders(openShader: OpenShader, previewShaders: PreviewShaders): List<OpenShader> {
        return listOf(openShader, previewShaders.pixelUvIdentity)
    }
}