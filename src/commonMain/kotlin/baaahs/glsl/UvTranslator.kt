package baaahs.glsl

import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

abstract class UvTranslator(val id: Id) {
    enum class Id {
        PANEL_SPACE_UV_TRANSLATOR {
            override fun parse(reader: ByteArrayReader): UvTranslator =
                PanelSpaceUvTranslator
        },

        CYLINDRICAL_MODEL_UV_TRANSLATOR {
            override fun parse(reader: ByteArrayReader): UvTranslator =
                CylindricalModelSpaceUvTranslator.parse(reader)
        },

        LINEAR_MODEL_UV_TRANSLATOR {
            override fun parse(reader: ByteArrayReader): UvTranslator =
                LinearModelSpaceUvTranslator.parse(reader)
        };

        abstract fun parse(reader: ByteArrayReader): UvTranslator

        companion object {
            val values = values()
            fun get(i: Byte): Id {
                if (i > values.size || i < 0) {
                    throw Throwable("bad index for UvTranslator.Id: $i")
                }
                return values[i.toInt()]
            }
        }
    }

    abstract fun forPixels(pixelLocations: List<Vector3F?>): SurfaceUvTranslator

    fun serialize(writer: ByteArrayWriter) {
        writer.writeByte(id.ordinal.toByte())
        serializeConfig(writer)
    }

    abstract fun serializeConfig(writer: ByteArrayWriter)

    companion object {
        fun parse(reader: ByteArrayReader): UvTranslator {
            val uvTranslatorId = reader.readByte()
            val uvTranslatorType = Id.get(uvTranslatorId)
            return uvTranslatorType.parse(reader)
        }
    }

    interface SurfaceUvTranslator {
        val pixelCount: Int
        fun getUV(pixelIndex: Int): Pair<Float, Float>
    }
}

object PanelSpaceUvTranslator :
    UvTranslator(Id.PANEL_SPACE_UV_TRANSLATOR) {

    override fun forPixels(pixelLocations: List<Vector3F?>): SurfaceUvTranslator {
        return object : SurfaceUvTranslator {
            override val pixelCount: Int = pixelLocations.size

            override fun getUV(pixelIndex: Int): Pair<Float, Float> {
                val vector3F = pixelLocations[pixelIndex]
                return (vector3F?.x ?: 0f) to (vector3F?.y ?: 0f)
            }
        }
    }

    override fun serializeConfig(writer: ByteArrayWriter) {}
}

class CylindricalModelSpaceUvTranslator(
    val modelCenter: Vector3F,
    val modelExtents: Vector3F
) : UvTranslator(Id.CYLINDRICAL_MODEL_UV_TRANSLATOR) {
    constructor(model: Model<*>) : this(model.modelCenter, model.modelExtents)

    override fun forPixels(pixelLocations: List<Vector3F?>): SurfaceUvTranslator {
        return object : SurfaceUvTranslator {
            override val pixelCount: Int = pixelLocations.size

            override fun getUV(pixelIndex: Int): Pair<Float, Float> {
                val pixelLocation = pixelLocations[pixelIndex] ?: modelCenter

                val normalDelta = pixelLocation.minus(modelCenter).normalize()
                var theta = atan2(abs(normalDelta.z), normalDelta.x) // theta in range [-π,π]
                if (theta < 0.0f) theta += (2.0f * PI.toFloat()) // theta in range [0,2π)
                val u = theta / (2.0f * PI.toFloat()) // u in range [0,1)
                val v = (pixelLocation.minus(modelCenter).y + modelExtents.y / 2.0f) / modelExtents.y
                return u to v
            }
        }
    }

    override fun serializeConfig(writer: ByteArrayWriter) {
        modelCenter.serialize(writer)
        modelExtents.serialize(writer)
    }

    companion object {
        fun parse(reader: ByteArrayReader): CylindricalModelSpaceUvTranslator {
            val modelCenter = Vector3F.parse(reader)
            val modelExtents = Vector3F.parse(reader)
            return CylindricalModelSpaceUvTranslator(modelCenter, modelExtents)
        }
    }
}

class LinearModelSpaceUvTranslator(
    val modelCenter: Vector3F,
    val modelBounds: Pair<Vector3F, Vector3F>
) : UvTranslator(Id.LINEAR_MODEL_UV_TRANSLATOR) {
    constructor(model: Model<*>) : this(model.modelCenter, model.modelBounds)

    override fun forPixels(pixelLocations: List<Vector3F?>): SurfaceUvTranslator {
        return object : SurfaceUvTranslator {
            override val pixelCount: Int = pixelLocations.size

            override fun getUV(pixelIndex: Int): Pair<Float, Float> {
                val (min, max) = modelBounds
                val pixelLocation = (pixelLocations[pixelIndex] ?: modelCenter) - min
                val extents = max - min
                val normalized = pixelLocation / extents // in range [0,1)
                return normalized.x to normalized.y
            }
        }
    }

    override fun serializeConfig(writer: ByteArrayWriter) {
        modelCenter.serialize(writer)
        modelBounds.serialize(writer)
    }

    companion object {
        fun parse(reader: ByteArrayReader): LinearModelSpaceUvTranslator {
            val modelCenter = Vector3F.parse(reader)
            val modelBounds = Vector3F.parse(reader) to Vector3F.parse(reader)
            return LinearModelSpaceUvTranslator(modelCenter, modelBounds)
        }

        fun Pair<Vector3F, Vector3F>.serialize(writer: ByteArrayWriter) {
            first.serialize(writer)
            second.serialize(writer)
        }
    }
}