package baaahs.glsl

import baaahs.*
import baaahs.glsl.GlslRenderer.GlConst.GL_RGBA8
import com.danielgergely.kgl.*
import kotlin.math.max
import kotlin.math.min

open class GlslRenderer(
    val gl: Kgl,
    private val contextSwitcher: ContextSwitcher,
    private val program: Program,
    private val uvTranslator: UvTranslator
) {
    private val surfacesToAdd: MutableList<GlslSurface> = mutableListOf()
    private val fbMaxPixWidth = 1024
    var pixelCount: Int = 0
    var nextPixelOffset: Int = 0
    var nextRectOffset: Int = 0

    private val glslSurfaces: MutableList<GlslSurface> = mutableListOf()

    private val uvCoordTextureId = program.obtainTextureId()
    private val rendererPlugins = program.plugins.mapNotNull { it.forRender() }

    var arrangement: Arrangement

    private val uvCoordsUniform: Uniform = gl { Uniform.find(program, "sm_uvCoords") ?: throw Exception("no sm_uvCoords uniform!")}
    private val resolutionUniform: Uniform? = gl { Uniform.find(program, "resolution") }
    private val timeUniform: Uniform? = gl { Uniform.find(program, "time") }

    val stats = Stats()

    init {
        gl { gl.clearColor(0f, .5f, 0f, 1f) }

        arrangement = createArrangement(0, FloatArray(0), glslSurfaces)
    }

    fun addSurface(surface: Surface): GlslSurface? {
        val surfacePixels = SurfacePixels(surface, nextPixelOffset)
        val rects = mapSurfaceToRects(nextPixelOffset, fbMaxPixWidth, surface)
        val glslSurface = GlslSurface(surfacePixels, Uniforms(), nextRectOffset, rects, uvTranslator)
        nextPixelOffset += surface.pixelCount
        nextRectOffset += glslSurface.rects.size

        surfacesToAdd.add(glslSurface)
        return glslSurface
    }

    inner class SurfacePixels(
        surface: Surface, pixel0Index: Int
    ) : baaahs.glsl.SurfacePixels(surface, pixel0Index) {
        override fun get(i: Int): Color = arrangement.getPixel(pixel0Index + i)
    }

    private fun createArrangement(pixelCount: Int, uvCoords: FloatArray, surfaceCount: List<GlslSurface>): Arrangement =
        Arrangement(pixelCount, uvCoords, surfaceCount.toList())

    fun draw() {
        withGlContext {
            program.bind()
            stats.addSurfacesMs += timeSync { incorporateNewSurfaces() }
            stats.bindFbMs += timeSync { arrangement.bindFramebuffer() }
            stats.renderMs += timeSync { render() }
            stats.readPxMs += timeSync { arrangement.copyToPixelBuffer() }
        }

        stats.frameCount++
    }

    private fun render() {
        val thisTime = (getTimeMillis() and 0x7ffffff).toFloat() / 1000.0f

        resolutionUniform?.set(1f, 1f)
        timeUniform?.set(thisTime)

        arrangement.bindUvCoordTexture(uvCoordsUniform)

        rendererPlugins.forEach { it.before() }

        gl.viewport(0, 0, arrangement.pixWidth, arrangement.pixHeight)
        gl.clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        arrangement.render()

        rendererPlugins.forEach { it.after() }

        gl.finish()

        val programLog = program.getInfoLog() ?: ""
        if (programLog.isNotEmpty()) println("ProgramInfoLog: $programLog")
    }

    protected fun incorporateNewSurfaces() {
        if (surfacesToAdd.isNotEmpty()) {
            val oldUvCoords = arrangement.uvCoords
            val newPixelCount = nextPixelOffset

            arrangement.release()

            val newUvCoords = FloatArray(newPixelCount.bufSize * 2)
            oldUvCoords.copyInto(newUvCoords)

            surfacesToAdd.forEach {
                val surface = it.pixels.surface
                val pixelLocations = LinearSurfacePixelStrategy.forSurface(surface)
                val uvTranslator = it.uvTranslator.forPixels(pixelLocations)

                var outOfBounds = 0
                var outOfBoundsU = 0
                var outOfBoundsV = 0
                for (i in 0 until uvTranslator.pixelCount) {
                    val uvOffset = (it.pixels.pixel0Index + i) * 2
                    val (u, v) = uvTranslator.getUV(i)
                    newUvCoords[uvOffset] = u     // u
                    newUvCoords[uvOffset + 1] = v // v

                    val uOut = u < 0f || u > 1f
                    val vOut = v < 0f || v > 1f
                    if (uOut || vOut) outOfBounds++
                    if (uOut) outOfBoundsU++
                    if (vOut) outOfBoundsV++
                }
                if (outOfBoundsU > 0 || outOfBoundsV > 0) {
                    logger.warn {
                        "Surface ${surface.describe()} has $outOfBounds points (of ${uvTranslator.pixelCount})" +
                                " outside the model (u=$outOfBoundsU v=$outOfBoundsV)"
                    }
                }
            }

            glslSurfaces.addAll(surfacesToAdd)
            surfacesToAdd.clear()

            arrangement = createArrangement(newPixelCount, newUvCoords, glslSurfaces)
            arrangement.bindUvCoordTexture(uvCoordsUniform)

            pixelCount = newPixelCount
            println("Now managing $pixelCount pixels.")
        }
    }

    fun release() {
        rendererPlugins.forEach { it.release() }
        arrangement.release()
    }

    companion object {
        private val logger = Logger("GlslRenderer")

        /** Resulting Rect is in pixel coordinates starting at (0,0) with Y increasing. */
        internal fun mapSurfaceToRects(nextPix: Int, pixWidth: Int, surface: Surface): List<Quad.Rect> {
            fun makeQuad(offsetPix: Int, widthPix: Int): Quad.Rect {
                val xStartPixel = offsetPix % pixWidth
                val yStartPixel = offsetPix / pixWidth
                val xEndPixel = xStartPixel + widthPix
                val yEndPixel = yStartPixel + 1
                return Quad.Rect(yStartPixel.toFloat(), xStartPixel.toFloat(), yEndPixel.toFloat(), xEndPixel.toFloat())
            }

            var nextPixelOffset = nextPix
            var pixelsLeft = surface.pixelCount
            val rects = mutableListOf<Quad.Rect>()
            while (pixelsLeft > 0) {
                val rowPixelOffset = nextPixelOffset % pixWidth
                val rowPixelsLeft = pixWidth - rowPixelOffset
                val rowPixelsTaken = min(pixelsLeft, rowPixelsLeft)
                rects.add(makeQuad(nextPixelOffset, rowPixelsTaken))

                nextPixelOffset += rowPixelsTaken
                pixelsLeft -= rowPixelsTaken
            }
            return rects
        }
    }

    inner class Arrangement(val pixelCount: Int, val uvCoords: FloatArray, val surfaces: List<GlslSurface>) {
        val pixWidth = pixelCount.bufWidth
        val pixHeight = pixelCount.bufHeight

        private val uniformSetters: List<UniformSetter> =
            program.params.map { param -> UniformSetter(program, param) }

        private val uvCoordTexture = gl { gl.createTexture() }
        private val frameBuffer = gl { gl.createFramebuffer() }
        private val renderBuffer = gl { gl.createRenderbuffer() }
        private val pixelBuffer = ByteBuffer(pixelCount.bufSize * 4)
        private val uvCoordsFloatBuffer = FloatBuffer(uvCoords)
        private val quad: Quad = gl { Quad(gl, program, surfaces.flatMap {
            it.rects.map { rect ->
                // Remap from pixel coordinates to normalized device coordinates.
               Quad.Rect(
                    -(rect.top / pixHeight * 2 - 1),
                    rect.left / pixWidth * 2 - 1,
                    -(rect.bottom / pixHeight * 2 - 1),
                    rect.right / pixWidth * 2 - 1
                )
            }
        }) }

        fun bindFramebuffer() {
            gl.checkForGlError()
            gl { gl.bindFramebuffer(GL_FRAMEBUFFER, frameBuffer) }

            gl { gl.bindRenderbuffer(GL_RENDERBUFFER, renderBuffer) }
//            logger.debug { "pixel count: $pixelCount ($pixWidth x $pixHeight = ${pixelCount.bufSize})" }
            gl { gl.renderbufferStorage(GL_RENDERBUFFER, GL_RGBA8, pixWidth, pixHeight) }
            gl { gl.framebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, renderBuffer) }

            val status = gl { gl.checkFramebufferStatus(GL_FRAMEBUFFER) }
            if (status != GL_FRAMEBUFFER_COMPLETE) {
                println(RuntimeException("FrameBuffer huh? $status").message)
            }
        }

        fun bindUvCoordTexture(uvCoordsLocation: Uniform) {
            gl { gl.activeTexture(GL_TEXTURE0 + uvCoordTextureId) }
            gl { gl.bindTexture(GL_TEXTURE_2D, uvCoordTexture) }
            gl { gl.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST) }
            gl { gl.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST) }
            gl {
                gl.texImage2D(
                    GL_TEXTURE_2D, 0,
                    GL_R32F, pixWidth * 2, pixHeight, 0,
                    GL_RED,
                    GL_FLOAT, uvCoordsFloatBuffer
                )
            }
            uvCoordsLocation.set(uvCoordTextureId)
        }

        fun getPixel(pixelIndex: Int): Color {
            val offset = pixelIndex * 4
            return Color(
                red = pixelBuffer[offset],
                green = pixelBuffer[offset + 1],
                blue = pixelBuffer[offset + 2],
                alpha = pixelBuffer[offset + 3]
            )
        }

        fun copyToPixelBuffer() {
            gl.readPixels(0, 0, pixWidth, pixHeight, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer)
        }

        fun release() {
            println("Release $this with $pixelCount pixels and ${uvCoords.size} uvs")

            quad.release()

            gl { gl.bindRenderbuffer(GL_RENDERBUFFER, null) }
            gl { gl.bindFramebuffer(GL_FRAMEBUFFER, null) }
            gl { gl.bindTexture(GL_TEXTURE_2D, null) }

            gl { gl.deleteFramebuffer(frameBuffer) }
            gl { gl.deleteRenderbuffer(renderBuffer) }
            gl { gl.deleteTexture(uvCoordTexture) }
        }

        fun render() {
            quad.prepareToRender {
                surfaces.forEach { surface ->
                    updateUniformsForSurface(surface)

                    surface.rects.indices.forEach { i ->
                        quad.renderRect(surface.rect0Index + i)
                    }
                }
            }
        }

        private fun updateUniformsForSurface(surface: GlslSurface) {
            program.params.forEachIndexed { paramIndex, param ->
                val value = surface.uniforms.values?.get(paramIndex)
                value?.let {
                    uniformSetters[paramIndex].set(value)
                }
            }
        }
    }

    val Int.bufWidth: Int get() = max(1, min(this, fbMaxPixWidth))
    val Int.bufHeight: Int get() = this / fbMaxPixWidth + 1
    val Int.bufSize: Int get() = bufWidth * bufHeight

    inner class Uniforms {
        var values: Array<Any?>? = null

        fun updateFrom(values: Array<Any?>) {
            this.values = values
        }
    }

    inline fun <T> gl(fn: () -> T): T {
        val result = fn.invoke()
        gl.checkForGlError()
        return result
    }

    private fun <T> withGlContext(fn: () -> T): T {
        return contextSwitcher.inContext { fn() }
    }

    interface ContextSwitcher {
        fun <T> inContext(fn: () -> T): T
    }

    class Stats {
        var addSurfacesMs = 0; internal set
        var bindFbMs = 0; internal set
        var renderMs = 0; internal set
        var readPxMs = 0; internal set
        var frameCount = 0; internal set

        fun dump() {
            println(
                "Render of $frameCount frames took: " +
                        "addSurface=${addSurfacesMs}ms " +
                        "bindFbMs=${bindFbMs}ms " +
                        "renderMs=${renderMs}ms " +
                        "readPxMs=${readPxMs}ms " +
                        "$this"
            )
        }

        fun reset() {
            addSurfacesMs = 0
            bindFbMs = 0
            renderMs = 0
            readPxMs = 0
            frameCount = 0
        }
    }

    object GlConst {
        val GL_RGBA8 = 0x8058
    }
}
