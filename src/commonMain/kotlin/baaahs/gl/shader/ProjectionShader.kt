package baaahs.gl.shader

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.GlslCode
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
        InputPort("pixelCoordsTexture", GlslType.Sampler2D, "U/V Coordinates Texture", ContentType.PixelCoordinatesTexture),
        InputPort("resolution", GlslType.Vec2, "Resolution", ContentType.Resolution),
        InputPort("previewResolution", GlslType.Vec2, "Preview Resolution", ContentType.PreviewResolution),
        InputPort("rasterCoord", GlslType.Vec4, "Raster Coordinate", ContentType.RasterCoordinate)
    )

    override val outputPort get() = OutputPort(ContentType.UvCoordinateStream)

    override val shaderType: ShaderType
        get() = ShaderType.Projection

    override val entryPointName: String = "mainProjection"

    override val icon: Icon = CommonIcons.ProjectionShader

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>,
        entryPoint: GlslCode.GlslFunction
    ): String {
        return resultVar + " = " + namespace.qualify(entryPoint.name) + "(gl_FragCoord.xy)"
    }

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
            int rasterX = int(rasterCoord.x);
            int rasterY = int(rasterCoord.y);
            
            vec3 pixelCoord = texelFetch(pixelCoordsTexture, ivec2(rasterX, rasterY), 0).xyz;
            return project(pixelCoord);
        }
    """.trimIndent()
    override val title: String = "Projection"
}