package baaahs.gl.patch

import baaahs.gl.glsl.GlslType

class ContentType(
    val description: String,
    val glslType: String,
    private val defaultInitializer: ((GlslType) -> String)? = null
) {
    fun initializer(dataType: GlslType): String =
        defaultInitializer?.invoke(dataType) ?: dataType.defaultInitializer()

    companion object {
        val PixelCoordinatesTexture = ContentType("Pixel Coordinates Texture", "sampler2D")
        val RasterCoordinate = ContentType("Raster Coordinate", "vec2")
        val UvCoordinate = ContentType("U/V Coordinate", "vec2")
        val XyCoordinate = ContentType("X/Y Coordinate", "vec2")
        val ModelInfo = ContentType("Model Info", "struct")

        val Mouse = ContentType("Mouse", "vec2")
        val XyzCoordinate = ContentType("X/Y/Z Coordinate", "vec3")

        val Color = ContentType("Color", "vec4") { type ->
            if (type == GlslType.Vec4) "vec4(0., 0., 0., 1.)" else type.defaultInitializer()
        }

        val Time = ContentType("Time", "float")
        val Resolution = ContentType("Resolution", "vec2")
        val PreviewResolution = ContentType("Preview Resolution", "vec2")
        val Float = ContentType("Float", "float")
        val Int = ContentType("Integer", "int")
        val Media = ContentType("Media", "sampler2D")
        val Unknown = ContentType("Unknown", "void")
    }
}
