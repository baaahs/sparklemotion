package baaahs.gl.render

import baaahs.fixtures.DeviceType
import baaahs.fixtures.Fixture
import baaahs.gl.GlContext
import baaahs.model.ModelInfo
import baaahs.timeSync
import baaahs.util.Logger
import com.danielgergely.kgl.GL_COLOR_BUFFER_BIT
import com.danielgergely.kgl.GL_DEPTH_BUFFER_BIT
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

open class RenderEngine(
    val gl: GlContext,
    private val modelInfo: ModelInfo,
    private val deviceType: DeviceType
) {
    private val fixturesToAdd: MutableList<FixtureRenderPlan> = mutableListOf()
    private val fixturesToRemove: MutableList<FixtureRenderPlan> = mutableListOf()
    var pixelCount: Int = 0
    var nextPixelOffset: Int = 0
    var nextRectOffset: Int = 0

    private val fixtureRenderPlans: MutableList<FixtureRenderPlan> = mutableListOf()

    private val paramBuffers = deviceType.params
        .mapIndexed { index, param -> param.allocate(gl, index) }
    private val resultBuffers = deviceType.resultParams
        .mapIndexed { index, deviceParam -> deviceParam.allocate(gl, index) }

    private val frameBuffer = gl.createFrameBuffer()
        .also { fb -> resultBuffers.forEach { it.attachTo(fb) } }
        .also { it.check() }

    var arrangement: Arrangement

    val stats = Stats()

    init {
        gl.runInContext { gl.check { clearColor(0f, .5f, 0f, 1f) } }

        arrangement = gl.runInContext {
            Arrangement(0, fixturesToAdd)
                .also { notifyListeners(it) }
        }
    }

    fun addFixture(fixture: Fixture): FixtureRenderPlan {
        if (fixture.deviceType != deviceType) {
            throw IllegalArgumentException(
                "This RenderEngine can't accept ${fixture.deviceType} devices, only $deviceType.")
        }

        val rects = mapFixturePixelsToRects(
            nextPixelOffset,
            fbMaxPixWidth,
            fixture
        )
        val fixtureRenderPlan = FixtureRenderPlan(
            fixture, nextRectOffset, rects, modelInfo, fixture.pixelCount, nextPixelOffset, resultBuffers
        )
        nextPixelOffset += fixture.pixelCount
        nextRectOffset += rects.size

        fixturesToAdd.add(fixtureRenderPlan)
        return fixtureRenderPlan
    }

    fun removeFixture(fixtureRenderPlan: FixtureRenderPlan) {
        fixturesToRemove.add(fixtureRenderPlan)
    }

    fun draw() {
        gl.runInContext {
            stats.prepareMs += timeSync { incorporateNewFixtures() }
            stats.renderMs += timeSync { render() }
            stats.finishMs += timeSync { gl.check { finish() } }
            stats.readPxMs += timeSync { copyResultsToCpuBuffer() }
        }

        stats.frameCount++
    }

    private fun render() {
        frameBuffer.bind()

        gl.setViewport(0, 0, arrangement.pixWidth, arrangement.pixHeight)
        gl.check { clearColor(0f, .5f, 0f, 1f) }
        gl.check { clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

        arrangement.render()
    }

    fun copyResultsToCpuBuffer() {
        resultBuffers.forEach { it.afterFrame(frameBuffer) }
    }

    private fun incorporateNewFixtures() {
        if (fixturesToRemove.isNotEmpty()) {
//            TODO("remove TBD")
        }

        if (fixturesToAdd.isNotEmpty()) {
            val newPixelCount = nextPixelOffset

            arrangement.release()

            fixtureRenderPlans.addAll(fixturesToAdd)
            arrangement = Arrangement(newPixelCount, fixturesToAdd)
                .also { notifyListeners(it) }

            fixturesToAdd.clear()

            pixelCount = newPixelCount
        }
    }

    fun release() {
        arrangement.release()
        paramBuffers.forEach { it.release() }
        resultBuffers.forEach { it.release() }
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
        fun onArrangementChange(arrangement: RenderEngine.Arrangement)
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

    inner class Arrangement(val pixelCount: Int, addedFixtures: List<FixtureRenderPlan>) {
        init {
            logger.info { "Creating ${deviceType::class.simpleName} arrangement with $pixelCount pixels" }
        }

        val pixWidth = pixelCount.bufWidth
        val pixHeight = pixelCount.bufHeight

        init {
            val safeWidth = max(pixWidth, 1.bufWidth)
            val safeHeight = max(pixHeight, 1.bufHeight)

            paramBuffers.forEach {
                it.resizeBuffer(safeWidth, safeHeight)
            }
            for (addedFixture in addedFixtures) {
                deviceType.initPixelParams(addedFixture, paramBuffers)
            }
            paramBuffers.forEach {
                it.resizeTexture(safeWidth, safeHeight)
            }

            resultBuffers.forEach {
                it.resize(safeWidth, safeHeight)
            }
        }

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

        fun release() {
            logger.debug { "Release $this with ${fixtureRenderPlans.count()} fixtures and $pixelCount pixels" }

            quad.release()
        }

        fun render() {
            fixtureRenderPlans
                .groupBy { it.program }
                .forEach { (program, fixtureRenderPlans) ->
                    if (program != null) {
                        // TODO: This ought to be merged with GlslProgram.bindings probably?
                        paramBuffers.forEach { it.bind(program).setOnProgram() }

                        gl.useProgram(program)
                        program.updateUniforms()

                        quad.prepareToRender(program.vertexAttribLocation) {
                            fixtureRenderPlans.forEach { fixtureRenderPlan ->
                                deviceType.setFixtureParamUniforms(fixtureRenderPlan, paramBuffers)

                                fixtureRenderPlan.rects.indices.forEach { i ->
                                    quad.renderRect(fixtureRenderPlan.rect0Index + i)
                                }
                            }
                        }
                    }
                }
        }
    }

    class Stats {
        var prepareMs = 0; internal set
        var renderMs = 0; internal set
        var finishMs = 0; internal set
        var readPxMs = 0; internal set
        var frameCount = 0; internal set

        fun dump() {
            val count = frameCount * 1f
            fun Int.pretty() = ((this / count * 10).roundToInt() / 10f).toString()
            println(
                "Average time drawing $frameCount frames:\n" +
                        " prepareMs=${prepareMs.pretty()}ms/frame\n" +
                        "  renderMs=${renderMs.pretty()}ms/frame\n" +
                        "  finishMs=${finishMs.pretty()}ms/frame\n" +
                        "  readPxMs=${readPxMs.pretty()}ms/frame\n" +
                        "   totalMs=${(prepareMs + renderMs + finishMs + readPxMs).pretty()}ms/frame"
            )
        }

        fun reset() {
            prepareMs = 0
            renderMs = 0
            finishMs = 0
            readPxMs = 0
            frameCount = 0
        }
    }
}
