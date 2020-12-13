package baaahs.gl.shader

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.objectSerializer
import baaahs.show.ShaderType
import baaahs.ui.Icon
import kotlinx.serialization.SerialName

@SerialName("baaahs.Core:Projection")
object ProjectionShader : ShaderPrototype("baaahs.Core:Projection") {
    override val serializerRegistrar = objectSerializer(id, this)

    override val wellKnownInputPorts = listOf(
        InputPort("resolution", GlslType.Vec2, "Resolution", ContentType.Resolution),
        InputPort("previewResolution", GlslType.Vec2, "Preview Resolution", ContentType.PreviewResolution)
    )

    override val defaultInputPortsByType: Map<GlslType, InputPort>
        get() = listOf(
            InputPort("location", GlslType.Vec3, "Pixel XYZ Coordinate", ContentType.XyzCoordinate)
        ).associateBy { it.type  }

    override val outputPort get() = OutputPort(ContentType.UvCoordinate)

    override val shaderType: ShaderType
        get() = ShaderType.Projection

    override val entryPointName: String = "mainProjection"

    override val icon: Icon = CommonIcons.ProjectionShader

    override val template: String = """
        uniform sampler2D pixelCoordsTexture;
        
        struct ModelInfo {
            vec3 center;
            vec3 extents;
        };
        uniform ModelInfo modelInfo;

        vec2 project(vec3 pixelLocation) {
            vec3 start = modelInfo.center - modelInfo.extents / 2.;
            vec3 rel = (pixelLocation - start) / modelInfo.extents;
            return rel.xy;
        }
        
        vec2 mainProjection(vec2 rasterCoord) {
            return texelFetch(ds_pixelLocation_texture, ivec2(rasterCoord.xy), 0).xyz;
        }
    """.trimIndent()
    override val title: String = "Projection"
}