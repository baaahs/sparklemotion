package baaahs.gl.patch

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslExpr
import baaahs.gl.glsl.GlslType

class ContentType(
    val id: String,
    val title: String,
    val glslType: GlslType,
    /** If false, this content type won't be suggested for matching GLSL types, it must be explicitly specified. */
    val suggest: Boolean = true,
    private val typeAdaptations: Map<GlslType, (GlslExpr) -> GlslExpr> = emptyMap(),
    /** If [glslType] is a [GlslType.Struct], we currently need to provide a hint for converting it to a vector. */
    val outputRepresentation: GlslType = glslType,
    val description: String? = null,
    private val defaultInitializer: ((GlslType) -> GlslExpr)? = null
) {
    fun initializer(dataType: GlslType): GlslExpr =
        defaultInitializer?.invoke(dataType)
            ?: officialDefaultInitializer(dataType)

    /**
     * Since [dataType] here comes from a shader declaration, it doesn't have a good defaultInitializer,
     * so we should use the one from our declared [glslType] instead.
     */
    private fun officialDefaultInitializer(dataType: GlslType) =
        (if (dataType == glslType) glslType else dataType).defaultInitializer

    fun adapt(expression: GlslExpr, toType: GlslType): GlslExpr {
        return typeAdaptations[toType]?.invoke(expression)
            ?: expression
    }

    fun isUnknown(): Boolean {
        return id == "unknown" || id.startsWith("unknown/")
    }

    /**
     * OpenGL doesn't support struct buffers directly, so emit any struct results to scalar arrays.
     */
    fun appendResultAsScalars(buf: ProgramBuilder, varName: String) {
        buf.append(glslType.outputRepresentationGlsl(varName));
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
        fun unknown(type: GlslType): ContentType {
            return ContentType("unknown/${type.glslLiteral}", "Unknown ${type.glslLiteral}", suggest = false, glslType = type)
        }

        @Deprecated("Obsolete")
        val PixelCoordinatesTexture =
            ContentType("pixel-coords-texture", "Pixel Coordinates Texture", GlslType.Sampler2D, suggest = true)
        val PreviewResolution = ContentType("preview-resolution", "Preview Resolution", GlslType.Vec2, suggest = false)
        val RasterCoordinate = ContentType("raster-coordinate", "Raster Coordinate", GlslType.Vec4, suggest = false)
        val Resolution = ContentType("resolution", "Resolution", GlslType.Vec2, suggest = false)
        val Unknown = ContentType("unknown", "Unknown", GlslType.Void, suggest = false)

        val UvCoordinate = ContentType(
            "uv-coordinate", "U/V Coordinate", GlslType.Vec2,
            typeAdaptations = mapOf(GlslType.Vec4 to { GlslExpr("${it.s}.xy") }),
            description = "U/V coordinate of the pixel being rendered, with [0, 0] being bottom-left and [1, 1] being top-right."
        )
        val XyCoordinate = ContentType("xy-coordinate", "X/Y Coordinate", GlslType.Vec2)
        val ModelInfo = ContentType("model-info", "Model Info", MoreTypes.ModelInfo.glslType)
        val Mouse = ContentType("mouse", "Mouse", GlslType.Vec4)
        val XyzCoordinate = ContentType("xyz-coordinate", "X/Y/Z Coordinate", GlslType.Vec3)
        val Color = ContentType("color", "Color", GlslType.Vec4) { type ->
            if (type == GlslType.Vec4) GlslExpr("vec4(0., 0., 0., 1.)") else type.defaultInitializer
        }
        val Time = ContentType("time", "Time", GlslType.Float)
        val TimeDelta = ContentType("time-delta", "Time Delta", GlslType.Float,
            description = "Elapsed seconds since the last frame was rendered; 0 for the first frame.")
        val Date = ContentType("date", "Date", GlslType.Vec4,
            description = "The first element of the vector is the year, the second element is the month, the third element is the day, and the fourth element is the time (in seconds) within the day.")
        val PassIndex = ContentType("pass-index", "Pass Index", GlslType.Int,
            description = "For multipass renders, this would indicate which pass we're on. For now, always 0.")
        val FrameIndex = ContentType("frame-index", "Frame Index", GlslType.Int,
            description = "Starting from 0.")

        val PixelIndex = ContentType("pixel-index", "Pixel Index", GlslType.Int,
            description = "Pixel index within fixture, starting from zero.")
        val PixelCount = ContentType("pixel-count", "Pixel Index", GlslType.Int,
            description = "Pixel count for this fixture.")

        val Boolean = ContentType("boolean", "Boolean", GlslType.Bool)
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
            XyCoordinate,
            ModelInfo,
            Mouse,
            XyzCoordinate,
            Color,
            Time,
            TimeDelta,
            Date,
            PassIndex,
            FrameIndex,

            PixelIndex,
            PixelCount,

            Boolean,
            Float,
            Int,
            Media
        )
    }
}

object MoreTypes {
    object ModelInfo {
        val struct = GlslCode.GlslStruct(
            "ModelInfo",
            mapOf(
                "center" to GlslType.Vec3,
                "extents" to GlslType.Vec3,
            ),
            fullText = """
                    struct ModelInfo {
                        vec3 center;            
                        vec3 extents;
                    }
                """.trimIndent(),
            varName = null
        )
        val glslType = GlslType.Struct(struct)
    }
}
