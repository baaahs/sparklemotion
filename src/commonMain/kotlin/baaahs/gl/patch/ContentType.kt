package baaahs.gl.patch

import baaahs.gl.glsl.GlslType

class ContentType(
    val id: String,
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
        return ContentType("$id-stream", "$description Stream", glslType, true, suggest, defaultInitializer)
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
        val PixelCoordinatesTexture = ContentType("x-pixel-coords-texture", "Pixel Coordinates Texture", GlslType.Sampler2D, suggest = true)
        val PreviewResolution = ContentType("x-preview-resolution", "Preview Resolution", GlslType.Vec2, suggest = false)
        val RasterCoordinate = ContentType("x-raster-coord", "Raster Coordinate", GlslType.Vec2, suggest = false)
        val Resolution = ContentType("resolution", "Resolution", GlslType.Vec2, suggest = false)
        val Unknown = ContentType("x-unknown", "Unknown", GlslType.Void, suggest = false)

        val UvCoordinate = ContentType("uv-coord", "U/V Coordinate", GlslType.Vec2)
        val UvCoordinateStream = UvCoordinate.stream()
        val XyCoordinate = ContentType("xy-coord", "X/Y Coordinate", GlslType.Vec2)
        val ModelInfo = ContentType("model-info", "Model Info", GlslType.from("ModelInfo"))
        val Mouse = ContentType("mouse", "Mouse", GlslType.Vec2)
        val XyzCoordinate = ContentType("xyz-coord", "X/Y/Z Coordinate", GlslType.Vec3)
        val Color = ContentType("color", "Color", GlslType.Vec4) { type ->
            if (type == GlslType.Vec4) "vec4(0., 0., 0., 1.)" else type.defaultInitializer()
        }
        val ColorStream = Color.stream()
        val Time = ContentType("time", "Time", GlslType.Float)
        val Float = ContentType("float", "Float", GlslType.Float)
        val Int = ContentType("int", "Integer", GlslType.Int)
        val Media = ContentType("media", "Media", GlslType.Sampler2D)

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
            Media
        )
    }
}
