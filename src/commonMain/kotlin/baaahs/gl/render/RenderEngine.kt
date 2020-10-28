package baaahs.gl.render

import baaahs.fixtures.Fixture
import baaahs.geom.Vector3F
import baaahs.gl.GlContext
import baaahs.gl.render.RenderEngine.GlConst.GL_RGBA8
import baaahs.model.ModelInfo
import baaahs.timeSync
import baaahs.util.Logger
import com.danielgergely.kgl.*
import kotlin.math.max
import kotlin.math.min

open class RenderEngine(
    val gl: GlContext,
    private val modelInfo: ModelInfo,
    private val resultFormat: ResultFormat = BytesResultFormat
) {
    private val fixturesToAdd: MutableList<FixtureRenderPlan> = mutableListOf()
    private val fixturesToRemove: MutableList<FixtureRenderPlan> = mutableListOf()
    var pixelCount: Int = 0
    var nextPixelOffset: Int = 0
    var nextRectOffset: Int = 0

    private val fixtureRenderPlans: MutableList<FixtureRenderPlan> = mutableListOf()

    private val renderBuffer = gl.createRenderBuffer()
        .also { it.storage(resultFormat.renderPixelFormat, 1.bufWidth, 1.bufHeight) }

    private val frameBuffer = gl.createFrameBuffer()
        .also { it.attach(renderBuffer, GL_COLOR_ATTACHMENT0)}
        .also { it.check() }

    var arrangement: Arrangement

    val stats = Stats()

    init {
        gl.runInContext { gl.check { clearColor(0f, .5f, 0f, 1f) } }

        arrangement = gl.runInContext {
            createArrangement(0, FloatArray(0), fixtureRenderPlans)
                .also { notifyListeners(it) }
        }
    }

    fun addFixture(fixture: Fixture): FixtureRenderPlan {
        val rects = mapFixturePixelsToRects(
            nextPixelOffset,
            fbMaxPixWidth,
            fixture
        )
        val renderResult =
            resultFormat.createRenderResult(this, fixture.pixelCount, nextPixelOffset)
        val renderSurface =
            FixtureRenderPlan(fixture, renderResult, nextRectOffset, rects, modelInfo)
        nextPixelOffset += fixture.pixelCount
        nextRectOffset += renderSurface.rects.size

        fixturesToAdd.add(renderSurface)
        return renderSurface
    }

    fun removeFixture(fixtureRenderPlan: FixtureRenderPlan) {
        fixturesToRemove.add(fixtureRenderPlan)
    }

    private fun createArrangement(pixelCount: Int, pixelCoords: FloatArray, fixtureCount: List<FixtureRenderPlan>): Arrangement =
        Arrangement(pixelCount, pixelCoords, fixtureCount.toList())

    fun draw() {
        gl.runInContext {
            stats.addFixturesMs += timeSync { incorporateNewSurfaces() }
            stats.bindFbMs += timeSync { arrangement.bindFramebuffer() }
            stats.renderMs += timeSync { render() }
            stats.readPxMs += timeSync { arrangement.copyToPixelBuffer() }
        }

        stats.frameCount++
    }

    private fun render() {
        gl.setViewport(0, 0, arrangement.pixWidth, arrangement.pixHeight)
        gl.check { clearColor(0f, .5f, 0f, 1f) }
        gl.check { clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

        arrangement.render()

        gl.check { finish() }
    }

    protected fun incorporateNewSurfaces() {
        if (fixturesToRemove.isNotEmpty()) {
//            TODO("remove TBD")
        }

        if (fixturesToAdd.isNotEmpty()) {
            val oldPixelCoords = arrangement.pixelCoords
            val newPixelCount = nextPixelOffset

            arrangement.release()

            val newPixelCoords = FloatArray(newPixelCount.bufSize * 3)
            oldPixelCoords.copyInto(newPixelCoords)

            fixturesToAdd.forEach {
                val fixture = it.fixture
                val pixelLocations = fixture.pixelLocations
                val pixel0Index = it.renderResult.bufferOffset
                val defaultPixelLocation = it.modelInfo.center
                fillPixelLocations(newPixelCoords, pixel0Index, pixelLocations, defaultPixelLocation)
            }

            fixtureRenderPlans.addAll(fixturesToAdd)
            fixturesToAdd.clear()

            arrangement = createArrangement(newPixelCount, newPixelCoords, fixtureRenderPlans)
                .also { notifyListeners(it) }

            pixelCount = newPixelCount
            println("Now managing $pixelCount pixels.")
        }
    }

    private fun fillPixelLocations(
        newPixelCoords: FloatArray,
        pixel0Index: Int,
        pixelLocations: List<Vector3F?>,
        defaultPixelLocation: Vector3F
    ) {
        pixelLocations.forEachIndexed { i, pixelLocation ->
            val bufOffset = (pixel0Index + i) * 3
            val (x, y, z) = pixelLocation ?: defaultPixelLocation
            newPixelCoords[bufOffset] = x     // x
            newPixelCoords[bufOffset + 1] = y // y
            newPixelCoords[bufOffset + 2] = z // z
        }
    }

    fun release() {
        arrangement.release()

        renderBuffer.release()
        frameBuffer.release()
    }

    private fun notifyListeners(arrangement: Arrangement) {
        fixtureRenderPlans
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
        private val logger = Logger("RenderEngine")

        /** Resulting Rect is in pixel coordinates starting at (0,0) with Y increasing. */
        internal fun mapFixturePixelsToRects(nextPix: Int, pixWidth: Int, fixture: Fixture): List<Quad.Rect> {
            fun makeQuad(offsetPix: Int, widthPix: Int): Quad.Rect {
                val xStartPixel = offsetPix % pixWidth
                val yStartPixel = offsetPix / pixWidth
                val xEndPixel = xStartPixel + widthPix
                val yEndPixel = yStartPixel + 1
                return Quad.Rect(
                    yStartPixel.toFloat(),
                    xStartPixel.toFloat(),
                    yEndPixel.toFloat(),
                    xEndPixel.toFloat()
                )
            }

            var nextPixelOffset = nextPix
            var pixelsLeft = fixture.pixelCount
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

        private const val fbMaxPixWidth = 1024
        val Int.bufWidth: Int get() = max(16, min(this, fbMaxPixWidth))
        val Int.bufHeight: Int get() = this / fbMaxPixWidth + 1
        val Int.bufSize: Int get() = bufWidth * bufHeight
    }

    inner class Arrangement(
        val pixelCount: Int,
        val pixelCoords: FloatArray,
        val fixtureRenderPlans: List<FixtureRenderPlan>
    ) {
        init {
            println("Creating arrangement with $pixelCount")
        }
        val pixWidth = pixelCount.bufWidth
        val pixHeight = pixelCount.bufHeight

        init {
            renderBuffer.storage(
                resultFormat.renderPixelFormat,
                max(pixWidth, 1.bufWidth),
                max(pixHeight, 1.bufHeight)
            )
        }

        internal val resultBuffer = resultFormat.createBuffer(pixelCount.bufSize)

        private val quad: Quad =
            Quad(gl, fixtureRenderPlans.flatMap {
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
            frameBuffer.bind()
        }

        fun copyToPixelBuffer() {
            gl.check {
                readPixels(0, 0, pixWidth, pixHeight,
                    resultFormat.readPixelFormat, resultFormat.readType, resultBuffer)
            }
        }

        fun release() {
            logger.debug { "Release $this with ${fixtureRenderPlans.count()} fixtures and $pixelCount pixels" }

            quad.release()
        }

        fun render() {
            fixtureRenderPlans.groupBy { it.program }.forEach { (program, fixtures) ->
                if (program != null) {
                    gl.useProgram(program)
                    program.updateUniforms()

                    quad.prepareToRender(program.vertexAttribLocation) {
                        fixtures.forEach { fixture ->
                            fixture.rects.indices.forEach { i ->
                                quad.renderRect(fixture.rect0Index + i)
                            }
                        }
                    }
                }
            }
        }
    }

    class Stats {
        var addFixturesMs = 0; internal set
        var bindFbMs = 0; internal set
        var renderMs = 0; internal set
        var readPxMs = 0; internal set
        var frameCount = 0; internal set

        fun dump() {
            println(
                "Render of $frameCount frames took: " +
                        "addFixtures=${addFixturesMs}ms " +
                        "bindFbMs=${bindFbMs}ms " +
                        "renderMs=${renderMs}ms " +
                        "readPxMs=${readPxMs}ms " +
                        "$this"
            )
        }

        fun reset() {
            addFixturesMs = 0
            bindFbMs = 0
            renderMs = 0
            readPxMs = 0
            frameCount = 0
        }
    }

    object GlConst {
        val GL_RGBA8 = 0x8058
        val GL_RG32F = 0x8230
        val GL_RGBA32F = 0x8814
    }

    interface ResultFormat {
        val renderPixelFormat: Int
        val readPixelFormat: Int
        val readType: Int

        fun createRenderResult(renderEngine: RenderEngine, size: Int, nextPixelOffset: Int): RenderResult
        fun createBuffer(size: Int): Buffer
    }

    object BytesResultFormat : ResultFormat {
        override val renderPixelFormat: Int
            get() = GL_RGBA8
        override val readPixelFormat: Int
            get() = GL_RGBA
        override val readType: Int
            get() = GL_UNSIGNED_BYTE

        override fun createRenderResult(renderEngine: RenderEngine, size: Int, nextPixelOffset: Int): RenderResult {
            return FixturePixels(renderEngine, size, nextPixelOffset)
        }

        override fun createBuffer(size: Int): Buffer {
            return ByteBuffer(size * 4)
        }
    }
}
