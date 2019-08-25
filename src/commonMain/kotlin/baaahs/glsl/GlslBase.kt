package baaahs.glsl

import baaahs.*
import baaahs.geom.Vector3F
import baaahs.shaders.GlslShader
import kotlin.math.*
import kotlin.random.Random

expect object GlslBase {
    val manager: GlslManager
}

interface GlslManager {
    fun createRenderer(program: String, adjustableValues: List<GlslShader.AdjustableValue>): GlslRenderer
}

abstract class GlslRenderer(
    val fragShader: String,
    val adjustableValues: List<GlslShader.AdjustableValue>
) {
    private val surfacesToAdd: MutableList<GlslSurface> = mutableListOf()
    var pixelCount: Int = 0
    var nextPixelOffset: Int = 0
    var nextSurfaceOffset: Int = 0

    val glslSurfaces: MutableList<GlslSurface> = mutableListOf()

    val uvCoordTextureIndex = 0
    var surfaceOrdinalTextureIndex = 1
    var nextTextureIndex = 2
    val adjustableValueUniformIndices = adjustableValues.map { nextTextureIndex++ }

    // uniforms
    internal var uvCoordsLocation: Uniform? = null
    internal var matLocation: Uniform? = null
    internal var resolutionLocation: Uniform? = null
    internal var timeLocation: Uniform? = null

    lateinit var instance: Instance

    fun findUniforms() {
        uvCoordsLocation = getUniformLocation("sm_uvCoords", required = true)
        resolutionLocation = getUniformLocation("resolution")
        timeLocation = getUniformLocation("time")
    }

    fun addSurface(surface: Surface, uvTranslator: UvTranslator): GlslSurface? {
        val glslSurface: GlslSurface
        if (surface is IdentifiedSurface) {
            if (surface.pixelLocations != null) {
                glslSurface = GlslSurface(
                    createSurfacePixels(surface, nextPixelOffset),
                    Uniforms(nextSurfaceOffset++),
                    uvTranslator
                )
                nextPixelOffset += surface.pixelCount
            } else {
                glslSurface = GlslSurface(
                    createSurfaceMonoPixel(surface, nextPixelOffset),
                    Uniforms(nextSurfaceOffset++),
                    uvTranslator
                )
                nextPixelOffset += 1
            }
        } else {
            glslSurface = GlslSurface(
                createSurfaceMonoPixel(surface, nextPixelOffset),
                Uniforms(nextSurfaceOffset++),
                uvTranslator
            )
            nextPixelOffset += 1
        }

        surfacesToAdd.add(glslSurface)
        return glslSurface
    }

    abstract fun createSurfacePixels(surface: Surface, pixelOffset: Int): SurfacePixels
    abstract fun createSurfaceMonoPixel(surface: Surface, pixelOffset: Int): SurfacePixels

    abstract fun createInstance(pixelCount: Int, uvCoords: FloatArray, surfaceCount: Int): Instance

    abstract fun draw()

    abstract fun <T> withGlContext(fn: () -> T): T

    abstract fun getUniformLocation(name: String, required: Boolean = false): Uniform

    protected fun incorporateNewSurfaces() {
        if (surfacesToAdd.isNotEmpty()) {
            val oldUvCoords = instance.uvCoords
            val newPixelCount = nextPixelOffset

            withGlContext {
                instance.release()
            }

            val newUvCoords = FloatArray(newPixelCount.bufSize * 2)
            oldUvCoords.copyInto(newUvCoords)

            surfacesToAdd.forEach {
                val surface = it.pixels.surface
                val uvTranslator = it.uvTranslator.forSurface(surface)

                for (i in 0 until uvTranslator.pixelCount) {
                    val uvOffset = (it.pixels.pixel0Index + i) * 2
                    val (u, v) = uvTranslator.getUV(i)
                    newUvCoords[uvOffset] = u     // u
                    newUvCoords[uvOffset + 1] = v // v
                }
            }

            withGlContext {
                instance = createInstance(newPixelCount, newUvCoords, nextSurfaceOffset)
                instance.bindUvCoordTexture(uvCoordTextureIndex, uvCoordsLocation!!)
            }

            pixelCount = newPixelCount
            println("Now managing $pixelCount pixels.")

            glslSurfaces.addAll(surfacesToAdd)
            surfacesToAdd.clear()
        }
    }

    interface AdjustibleUniform {
        fun bind()
        fun setValue(surfaceOrdinal: Int, value: Any?)
    }

    abstract inner class Instance(val pixelCount: Int, val uvCoords: FloatArray, val surfaceCount: Int) {
        abstract val adjustableUniforms: Map<Int, AdjustibleUniform>

        abstract fun bindFramebuffer()
        abstract fun bindUvCoordTexture(textureIndex: Int, uvCoordsLocation: Uniform)
        abstract fun getPixel(pixelIndex: Int): Color
        abstract fun copyToPixelBuffer()
        abstract fun release()

        fun bindUniforms() {
            adjustableUniforms.forEach { (key, value) -> value.bind() }
        }

        fun setUniform(adjustableValue: GlslShader.AdjustableValue, surfaceOrdinal: Int, value: Any?) {
            adjustableUniforms[adjustableValue.ordinal]!!.setValue(surfaceOrdinal, value)
        }
    }

    class Uniform(val locationInternal: Any?)

    val Int.bufWidth: Int get() = max(1, min(this, 1024))
    val Int.bufHeight: Int get() = this / 1024 + 1
    val Int.bufSize: Int get() = bufWidth * bufHeight

    inner class Uniforms(internal val surfaceOrdinal: Int) {
        fun updateFrom(values: Array<Any?>) {
            adjustableValues.forEach {
                instance.setUniform(it, surfaceOrdinal, values[it.ordinal])
            }
        }
    }
}

class GlslSurface(
    val pixels: SurfacePixels,
    val uniforms: GlslRenderer.Uniforms,
    val uvTranslator: UvTranslator
)

interface UvTranslator {
    fun forSurface(surface: Surface): SurfaceUvTranslator {
        return if (surface is IdentifiedSurface) {
            if (surface.pixelLocations != null) {
                forPixels(surface.pixelLocations)
            } else {
                val center = surface.modelSurface.allVertices().average()
                forPixels(listOf(center))
            }
        } else {
            forPixels(listOf(Vector3F(Random.nextFloat() - .5f, Random.nextFloat() - .5f, 1f)))
        }
    }

    fun forPixels(pixelLocations: List<Vector3F?>): SurfaceUvTranslator

    interface SurfaceUvTranslator {
        val pixelCount: Int
        fun getUV(pixelIndex: Int): Pair<Float, Float>
    }
}

private fun Collection<Vector3F>.average(): Vector3F {
    return reduce { acc, vector3F -> acc.plus(vector3F) }.dividedByScalar(size.toFloat())
}

object PanelSpaceUvTranslator : UvTranslator {
    override fun forPixels(pixelLocations: List<Vector3F?>): UvTranslator.SurfaceUvTranslator {
        return object : UvTranslator.SurfaceUvTranslator {
            override val pixelCount: Int = pixelLocations.size

            override fun getUV(pixelIndex: Int): Pair<Float, Float> {
                val vector3F = pixelLocations[pixelIndex]
                return (vector3F?.x ?: 0f) to (vector3F?.y ?: 0f)
            }
        }
    }
}

class ModelSpaceUvTranslator(val model: Model<*>) : UvTranslator {
    val modelCenter = model.modelCenter
    val modelExtents = model.modelExtents

    override fun forPixels(pixelLocations: List<Vector3F?>): UvTranslator.SurfaceUvTranslator {
        return object : UvTranslator.SurfaceUvTranslator {
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
}

abstract class SurfacePixels(val surface: Surface, val pixel0Index: Int) : Pixels {
    override val size: Int = surface.pixelCount
    override fun set(i: Int, color: Color): Unit = TODO("set not implemented")
    override fun set(colors: Array<Color>): Unit = TODO("set not implemented")
}