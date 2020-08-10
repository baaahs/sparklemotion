package baaahs.glshaders

import kotlinx.serialization.Serializable

@Serializable
data class ContentType(
    val description: String,
    private val defaultInitializer: ((GlslType) -> String)? = null
) {
    fun initializer(dataType: GlslType): String =
        defaultInitializer?.invoke(dataType) ?: dataType.defaultInitializer()

    companion object {
        val PixelCoordinatesTexture = ContentType("Pixel Coordinates Texture")
        val RasterCoordinate = ContentType("Raster Coordinate")
        val UvCoordinate = ContentType("U/V Coordinate")
        val XyCoordinate = ContentType("X/Y Coordinate")
        val ModelInfo = ContentType("Model Info")

        val Mouse = ContentType("Mouse")
        val XyzCoordinate = ContentType("X/Y/Z Coordinate")

        val Color = ContentType("Color") { type ->
            if (type == GlslType.Vec4) "vec4(0., 0., 0., 1.)" else type.defaultInitializer()
        }

        val Time = ContentType("Time")
        val Resolution = ContentType("Resolution")
        val PreviewResolution = ContentType("Preview Resolution")
        val Float = ContentType("Float")
        val Int = ContentType("Integer")
        val Media = ContentType("Media")
        val Unknown = ContentType("Unknown")
    }
}
