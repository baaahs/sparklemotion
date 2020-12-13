package baaahs.gl.patch

import baaahs.gl.glsl.GlslType

class ContentType(
    val id: String,
    val title: String,
    val glslType: GlslType,
    /** If false, this content type won't be suggested for matching GLSL types, it must be explicitly specified. */
    val suggest: Boolean = true,
    private val typeAdaptations: Map<GlslType, (String) -> String> = emptyMap(),
    private val defaultInitializer: ((GlslType) -> String)? = null
) {
    fun initializer(dataType: GlslType): String =
        defaultInitializer?.invoke(dataType) ?: dataType.defaultInitializer()

    fun adapt(expression: String, toType: GlslType): String {
        return typeAdaptations[toType]?.invoke(expression)
            ?: expression
    }

    override fun toString(): String = "ContentType($id [$glslType])"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContentType) return false

        if (id != other.id) return false
        if (title != other.title) return false
        if (glslType != other.glslType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + glslType.hashCode()
        return result
    }


    companion object {
        @Deprecated("Obsolete")
        val PixelCoordinatesTexture =
            ContentType("pixel-coords-texture", "Pixel Coordinates Texture", GlslType.Sampler2D, suggest = true)
        val PreviewResolution = ContentType("preview-resolution", "Preview Resolution", GlslType.Vec2, suggest = false)
        val RasterCoordinate = ContentType("raster-coordinate", "Raster Coordinate", GlslType.Vec4, suggest = false)
        val Resolution = ContentType("resolution", "Resolution", GlslType.Vec2, suggest = false)
        val Unknown = ContentType("unknown", "Unknown", GlslType.Void, suggest = false)

        val UvCoordinate = ContentType(
            "uv-coordinate", "U/V Coordinate", GlslType.Vec2,
            typeAdaptations = mapOf(GlslType.Vec4 to { "$it.xy" })
        )
        val XyCoordinate = ContentType("xy-coordinate", "X/Y Coordinate", GlslType.Vec2)
        val ModelInfo = ContentType("model-info", "Model Info", GlslType.from("ModelInfo"))
        val Mouse = ContentType("mouse", "Mouse", GlslType.Vec2)
        val XyzCoordinate = ContentType("xyz-coordinate", "X/Y/Z Coordinate", GlslType.Vec3)
        val Color = ContentType("color", "Color", GlslType.Vec4) { type ->
            if (type == GlslType.Vec4) "vec4(0., 0., 0., 1.)" else type.defaultInitializer()
        }
        val Time = ContentType("time", "Time", GlslType.Float)
        val Float = ContentType("float", "Float", GlslType.Float)
        val Int = ContentType("int", "Integer", GlslType.Int)
        val Media = ContentType("media", "Media", GlslType.Sampler2D)

        val PanAndTilt = ContentType("pan-tilt", "Pan & Tilt", GlslType.Vec4)

        val coreTypes = listOf(
            PixelCoordinatesTexture,
            PreviewResolution,
            RasterCoordinate,
            Resolution,
            Unknown,

            UvCoordinate,
            XyCoordinate,
            ModelInfo,
            Mouse,
            XyzCoordinate,
            Color,
            Time,
            Float,
            Int,
            Media,

            PanAndTilt
        )
    }
}
