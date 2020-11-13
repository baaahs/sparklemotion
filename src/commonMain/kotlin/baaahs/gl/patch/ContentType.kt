package baaahs.gl.patch

import baaahs.gl.glsl.GlslType

class ContentType(
    val description: String,
    val glslType: GlslType,
    val isStream: Boolean = false,
    /** If false, this content type won't be suggested for matching GLSL types, it must be explicitly specified. */
    val suggest: Boolean = true,
    private val defaultInitializer: ((GlslType) -> String)? = null
) {
    fun initializer(dataType: GlslType): String =
        defaultInitializer?.invoke(dataType) ?: dataType.defaultInitializer()

    /**
     * ContentTypes where [isStream] is `true` describe content whose value is determined by,
     * and may be different for, every pixel.
     */
    fun stream(): ContentType {
        if (isStream) error("Already a stream!")
        return ContentType("$description Stream", glslType, true, suggest, defaultInitializer)
    }

    override fun toString(): String = "ContentType($description [$glslType])"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContentType) return false

        if (description != other.description) return false
        if (glslType != other.glslType) return false
        if (isStream != other.isStream) return false

        return true
    }

    override fun hashCode(): Int {
        var result = description.hashCode()
        result = 31 * result + glslType.hashCode()
        result = 31 * result + isStream.hashCode()
        return result
    }


    companion object {
        val PixelCoordinatesTexture = ContentType("Pixel Coordinates Texture", GlslType.Sampler2D, suggest = true)
        val PreviewResolution = ContentType("Preview Resolution", GlslType.Vec2, suggest = false)
        val RasterCoordinate = ContentType("Raster Coordinate", GlslType.Vec2, suggest = false)
        val Resolution = ContentType("Resolution", GlslType.Vec2, suggest = false)
        val Unknown = ContentType("Unknown", GlslType.Void, suggest = false)

        val UvCoordinate = ContentType("U/V Coordinate", GlslType.Vec2)
        val UvCoordinateStream = UvCoordinate.stream()
        val XyCoordinate = ContentType("X/Y Coordinate", GlslType.Vec2)
        val ModelInfo = ContentType("Model Info", GlslType.from("ModelInfo"))
        val Mouse = ContentType("Mouse", GlslType.Vec2)
        val XyzCoordinate = ContentType("X/Y/Z Coordinate", GlslType.Vec3)
        val Color = ContentType("Color", GlslType.Vec4) { type ->
            if (type == GlslType.Vec4) "vec4(0., 0., 0., 1.)" else type.defaultInitializer()
        }
        val ColorStream = Color.stream()
        val Time = ContentType("Time", GlslType.Float)
        val Float = ContentType("Float", GlslType.Float)
        val Int = ContentType("Integer", GlslType.Int)
        val Media = ContentType("Media", GlslType.Sampler2D)

        val PanAndTilt = ContentType("Pan & Tilt", GlslType.Vec4)

        val coreTypes = listOf(
            PixelCoordinatesTexture,
            PreviewResolution,
            RasterCoordinate,
            Resolution,
            Unknown,

            UvCoordinate,
            UvCoordinateStream,
            XyCoordinate,
            ModelInfo,
            Mouse,
            XyzCoordinate,
            Color,
            ColorStream,
            Time,
            Float,
            Int,
            Media,

            PanAndTilt
        )
    }
}
