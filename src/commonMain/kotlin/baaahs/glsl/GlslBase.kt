package baaahs.glsl

import baaahs.*
import baaahs.shaders.GlslShader
import kotlin.math.max
import kotlin.math.min

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
    val surfacesToAdd: MutableList<GlslSurface> = mutableListOf()
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
        uvCoordsLocation = getUniformLocation("sm_uvCoords")
        resolutionLocation = getUniformLocation("resolution")
        timeLocation = getUniformLocation("time")

    }

    fun addSurface(surface: Surface, uvTranslator: UvTranslator): GlslSurface? {
        if (surface is IdentifiedSurface && surface.pixelVertices != null) {
            val glslSurface = GlslSurface(
                createSurfacePixels(surface, nextPixelOffset),
                Uniforms(nextSurfaceOffset++),
                uvTranslator
            )
            surfacesToAdd.add(glslSurface)
            nextPixelOffset += surface.pixelCount
            return glslSurface
        }

        return null
    }

    abstract fun createSurfacePixels(surface: IdentifiedSurface, pixelOffset: Int): SurfacePixels

    abstract fun createInstance(pixelCount: Int, uvCoords: FloatArray, surfaceCount: Int): Instance

    abstract fun draw()

    abstract fun <T> withGlContext(fn: () -> T): T

    abstract fun getUniformLocation(name: String): Uniform

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

                for (i in 0 until surface.pixelCount) {
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
        abstract val adjustableUniforms: List<AdjustibleUniform>

        abstract fun bindFramebuffer()
        abstract fun bindUvCoordTexture(textureIndex: Int, uvCoordsLocation: Uniform)
        abstract fun getPixel(pixelIndex: Int): Color
        abstract fun copyToPixelBuffer()
        abstract fun release()

        fun bindUniforms() {
            adjustableUniforms.forEach { it.bind() }
        }

        fun setUniform(adjustableValue: GlslShader.AdjustableValue, surfaceOrdinal: Int, value: Any?) {
            adjustableUniforms[adjustableValue.ordinal].setValue(surfaceOrdinal, value)
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
    fun forSurface(surface: IdentifiedSurface): SurfaceUvTranslator

    interface SurfaceUvTranslator {
        fun getUV(pixelIndex: Int): Pair<Float, Float>
    }
}

object ScannerPixelCoordsUvTranslator : UvTranslator {
    override fun forSurface(surface: IdentifiedSurface): UvTranslator.SurfaceUvTranslator {
        val pixelVertices = surface.pixelVertices!!
        return object : UvTranslator.SurfaceUvTranslator {
            override fun getUV(pixelIndex: Int): Pair<Float, Float> {
                return pixelVertices[pixelIndex].x to pixelVertices[pixelIndex].y
            }
        }
    }
}

class ModelUvTranslator(val model: Model<*>) : UvTranslator {
    override fun forSurface(surface: IdentifiedSurface): UvTranslator.SurfaceUvTranslator {
        return object : UvTranslator.SurfaceUvTranslator {
            override fun getUV(pixelIndex: Int): Pair<Float, Float> {
                TODO("ModelUvTranslator.getUV not implemented")
            }
        }
    }

}

abstract class SurfacePixels(val surface: IdentifiedSurface, val pixel0Index: Int) : Pixels {
    override val size: Int = surface.pixelCount
    override fun set(i: Int, color: Color): Unit = TODO("set not implemented")
    override fun set(colors: Array<Color>): Unit = TODO("set not implemented")
}