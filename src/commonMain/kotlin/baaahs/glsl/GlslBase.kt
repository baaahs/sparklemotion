package baaahs.glsl

import baaahs.Color
import baaahs.IdentifiedSurface
import baaahs.Pixels
import baaahs.Surface
import kotlin.math.max
import kotlin.math.min

expect object GlslBase {
    val manager : GlslManager
}

interface GlslManager {
    fun createRenderer(program: String): GlslRenderer
}

abstract class GlslRenderer {
    val surfacePixelsToAdd: MutableList<SurfacePixels> = mutableListOf()
    var pixelCount: Int = 0
    var nextPixelOffset: Int = 0

    val uvCoordTextureIndex = 0

    // uniforms
    internal var uvCoordsLocation: Uniform? = null
    internal var matLocation: Uniform? = null
    internal var resolutionLocation: Uniform? = null
    internal var timeLocation: Uniform? = null

    lateinit var instance: Instance

    fun findUniforms() {
        uvCoordsLocation = getUniformLocation("sm_uvCoords")
        matLocation = getUniformLocation("viewProjMatrix")
        resolutionLocation = getUniformLocation("resolution")
        timeLocation = getUniformLocation("time")

    }

    fun addSurface(surface: Surface): Pixels {
        if (surface is IdentifiedSurface && surface.pixelVertices != null) {
            val surfacePixels = createSurfacePixels(surface, nextPixelOffset)
            surfacePixelsToAdd.add(surfacePixels)
            nextPixelOffset += surface.pixelCount
            return surfacePixels
        }

        return object : Pixels {
            override val size: Int get() = 0

            override fun get(i: Int): Color = TODO("get not implemented")

            override fun set(i: Int, color: Color): Unit = TODO("set not implemented")

            override fun set(colors: Array<Color>): Unit = TODO("set not implemented")
        }
    }

    abstract fun createSurfacePixels(surface: IdentifiedSurface, pixelOffset: Int): SurfacePixels

    abstract fun createInstance(pixelCount: Int, uvCoords: FloatArray): Instance

    abstract fun draw()

    abstract fun <T> withGlContext(fn: () -> T): T

    abstract fun getUniformLocation(name: String): Uniform

    protected fun maybeAddSurfacePixels() {
        if (surfacePixelsToAdd.isNotEmpty()) {
            val oldUvCoords = instance.uvCoords
            val newPixelCount = nextPixelOffset

            withGlContext {
                instance.release()
            }

            val newUvCoords = FloatArray(newPixelCount.bufSize * 2)
            oldUvCoords.copyInto(newUvCoords)

            surfacePixelsToAdd.forEach {
                val surface = it.surface
                val pixelVertices = surface.pixelVertices!!

                for (i in 0 until surface.pixelCount) {
                    val uvOffset = (it.pixel0Index + i) * 2
                    newUvCoords[uvOffset] = pixelVertices[i].x     // u
                    newUvCoords[uvOffset + 1] = pixelVertices[i].y // v
                }
            }

            withGlContext {
                instance = createInstance(newPixelCount, newUvCoords)
                instance.bindUvCoordTexture(uvCoordTextureIndex, uvCoordsLocation!!)
            }

            pixelCount = newPixelCount
            println("Now managing $pixelCount pixels.")

            surfacePixelsToAdd.clear()
        }
    }

    abstract class Instance(val pixelCount: Int, val uvCoords: FloatArray) {
        abstract fun bindFramebuffer()
        abstract fun bindUvCoordTexture(textureIndex: Int, uvCoordsLocation: Uniform)
        abstract fun getPixel(pixelIndex: Int): Color
        abstract fun copyToPixelBuffer()
        abstract fun release()
    }

    class Uniform(val locationInternal: Any?)

    val Int.bufWidth: Int get() = max(1, min(this, 1024))
    val Int.bufHeight: Int get() = this / 1024 + 1
    val Int.bufSize: Int get() = bufWidth * bufHeight
}

abstract class SurfacePixels(val surface: IdentifiedSurface, val pixel0Index: Int) : Pixels {
    override val size: Int = surface.pixelCount
    override fun set(i: Int, color: Color): Unit = TODO("set not implemented")
    override fun set(colors: Array<Color>): Unit = TODO("set not implemented")
}