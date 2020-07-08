package baaahs.glsl

import baaahs.Color
import baaahs.Logger
import baaahs.Surface
import baaahs.glsl.GlslRenderer.GlConst.GL_RGBA8
import baaahs.model.ModelInfo
import baaahs.timeSync
import com.danielgergely.kgl.*
import kotlin.math.max
import kotlin.math.min

open class GlslRenderer(
    val gl: GlslContext,
    private val modelInfo: ModelInfo
) {
    private val surfacesToAdd: MutableList<RenderSurface> = mutableListOf()
    private val surfacesToRemove: MutableList<RenderSurface> = mutableListOf()
    private val fbMaxPixWidth = 1024
    var pixelCount: Int = 0
    var nextPixelOffset: Int = 0
    var nextRectOffset: Int = 0

    private val renderSurfaces: MutableList<RenderSurface> = mutableListOf()

    var arrangement: Arrangement

    val stats = Stats()

    init {
        gl.runInContext { gl.check { clearColor(0f, .5f, 0f, 1f) } }

        arrangement = gl.runInContext {
            createArrangement(0, FloatArray(0), renderSurfaces)
                .also { notifyListeners(it) }
        }
    }

    fun addSurface(surface: Surface): RenderSurface {
        val surfacePixels = SurfacePixels(surface, nextPixelOffset)
        val rects = mapSurfaceToRects(nextPixelOffset, fbMaxPixWidth, surface)
        val renderSurface = RenderSurface(surfacePixels, nextRectOffset, rects, modelInfo)
        nextPixelOffset += surface.pixelCount
        nextRectOffset += renderSurface.rects.size

        surfacesToAdd.add(renderSurface)
        return renderSurface
    }

    fun removeSurface(renderSurface: RenderSurface) {
        surfacesToRemove.add(renderSurface)
    }

    inner class SurfacePixels(
        surface: Surface, pixel0Index: Int
    ) : baaahs.glsl.SurfacePixels(surface, pixel0Index) {
        override fun get(i: Int): Color = arrangement.getPixel(pixel0Index + i)
    }

    private fun createArrangement(pixelCount: Int, pixelCoords: FloatArray, surfaceCount: List<RenderSurface>): Arrangement =
        Arrangement(pixelCount, pixelCoords, surfaceCount.toList())

    fun draw() {
        gl.runInContext {
            stats.addSurfacesMs += timeSync { incorporateNewSurfaces() }
            stats.bindFbMs += timeSync { arrangement.bindFramebuffer() }
            stats.renderMs += timeSync { render() }
            stats.readPxMs += timeSync { arrangement.copyToPixelBuffer() }
        }

        stats.frameCount++
    }

    private fun render() {
        gl.check { viewport(0, 0, arrangement.pixWidth, arrangement.pixHeight) }
        gl.check { clearColor(0f, .5f, 0f, 1f) }
        gl.check { clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

        arrangement.render()

        gl.check { finish() }
    }

    protected fun incorporateNewSurfaces() {
        if (surfacesToRemove.isNotEmpty()) {
//            TODO("remove TBD")
        }

        if (surfacesToAdd.isNotEmpty()) {
            val oldPixelCoords = arrangement.pixelCoords
            val newPixelCount = nextPixelOffset

            arrangement.release()

            val newPixelCoords = FloatArray(newPixelCount.bufSize * 3)
            oldPixelCoords.copyInto(newPixelCoords)

            surfacesToAdd.forEach {
                putPixelCoords(it, newPixelCoords)
            }

            renderSurfaces.addAll(surfacesToAdd)
            surfacesToAdd.clear()

            arrangement = createArrangement(newPixelCount, newPixelCoords, renderSurfaces)
                .also { notifyListeners(it) }

            pixelCount = newPixelCount
            println("Now managing $pixelCount pixels.")
        }
    }

    private fun putPixelCoords(renderSurface: RenderSurface, newPixelCoords: FloatArray) {
        val surface = renderSurface.pixels.surface
        val pixelLocations = LinearSurfacePixelStrategy.forSurface(surface)

        pixelLocations.forEachIndexed { i, pixelLocation ->
            val bufOffset = (renderSurface.pixels.pixel0Index + i) * 3
            val (x, y, z) = pixelLocation ?: renderSurface.modelInfo.center
            newPixelCoords[bufOffset] = x     // x
            newPixelCoords[bufOffset + 1] = y // y
            newPixelCoords[bufOffset + 2] = z // z
        }
    }

    fun release() {
        arrangement.release()
    }

    private fun notifyListeners(arrangement: Arrangement) {
        renderSurfaces
            .mapNotNull { it.program }
            .distinct()
            .flatMap { it.arrangementListeners }
            .distinct()
            .forEach { it.onArrangementChange(arrangement) }
    }

    interface ArrangementListener {
        fun onArrangementChange(arrangement: Arrangement)
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

    inner class Arrangement(
        val pixelCount: Int,
        val pixelCoords: FloatArray,
        val surfaces: List<RenderSurface>
    ) {
        init {
            println("Creating arrangement with $pixelCount")
        }
        val pixWidth = pixelCount.bufWidth
        val pixHeight = pixelCount.bufHeight

        private val renderBuffer = gl.createRenderBuffer()
        private val pixelBuffer = ByteBuffer(pixelCount.bufSize * 4)

        private val quad: Quad = Quad(gl, surfaces.flatMap {
            it.rects.map { rect ->
                // Remap from pixel coordinates to normalized device coordinates.
                Quad.Rect(
                    -(rect.top / pixHeight * 2 - 1),
                    rect.left / pixWidth * 2 - 1,
                    -(rect.bottom / pixHeight * 2 - 1),
                    rect.right / pixWidth * 2 - 1
                )
            }
        })

        fun bindFramebuffer() {
            renderBuffer.bind(GL_RGBA8, pixWidth, pixHeight, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER)
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
            gl.check { readPixels(0, 0, pixWidth, pixHeight, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer) }
        }

        fun release() {
            logger.debug { "Release $this with ${surfaces.count()} surfaces and $pixelCount pixels" }

            quad.release()

            renderBuffer.release()
        }

        fun render() {
            surfaces.groupBy { it.program }.forEach { (program, surfaces) ->
                if (program != null) {
                    gl.useProgram(program)
                    program.updateUniforms()

                    quad.prepareToRender(program.vertexAttribLocation) {
                        surfaces.forEach { surface ->
                            surface.rects.indices.forEach { i ->
                                quad.renderRect(surface.rect0Index + i)
                            }
                        }
                    }
                }
            }
        }
    }

    val Int.bufWidth: Int get() = max(1, min(this, fbMaxPixWidth))
    val Int.bufHeight: Int get() = this / fbMaxPixWidth + 1
    val Int.bufSize: Int get() = bufWidth * bufHeight

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
