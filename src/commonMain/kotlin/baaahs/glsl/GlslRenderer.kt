package baaahs.glsl

import baaahs.Color
import baaahs.IdentifiedSurface
import baaahs.Surface
import baaahs.shaders.GlslShader
import de.fabmax.kool.KoolContext
import de.fabmax.kool.gl.glGetUniformLocation
import kotlin.math.max
import kotlin.math.min

abstract class GlslRenderer(
    val ctx: KoolContext,
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

    fun getUniformLocation(name: String, required: Boolean = false): Uniform {
        val loc = Uniform(glGetUniformLocation(program, name))
        if (loc == null && required)
            throw IllegalStateException("Couldn't find uniform $name")
        return Uniform(loc)
    }

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