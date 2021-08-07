package baaahs.gl.render

import baaahs.device.DeviceType
import baaahs.fixtures.DeviceTypeRenderPlan
import baaahs.fixtures.Fixture
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.PerPixelEngineFeed
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.result.ResultBuffer
import baaahs.gl.result.ResultType
import baaahs.model.ModelInfo
import baaahs.util.Logger
import com.danielgergely.kgl.GL_COLOR_BUFFER_BIT
import com.danielgergely.kgl.GL_DEPTH_BUFFER_BIT
import kotlin.math.max
import kotlin.math.min

class ModelRenderEngine(
    gl: GlContext,
    private val modelInfo: ModelInfo,
    private val deviceType: DeviceType,
    private val minTextureWidth: Int = 16,
    private val maxFramebufferWidth: Int = fbMaxPixWidth,
    private val resultDeliveryStrategy: ResultDeliveryStrategy = SyncResultDeliveryStrategy()
) : RenderEngine(gl) {
    private val renderTargetsToAdd: MutableList<FixtureRenderTarget> = mutableListOf()
    private val renderTargetsToRemove: MutableList<FixtureRenderTarget> = mutableListOf()
    var pixelCount: Int = 0
    var nextPixelOffset: Int = 0
    var nextRectOffset: Int = 0

    private val renderTargets: MutableList<FixtureRenderTarget> = mutableListOf()
    private var renderPlan: DeviceTypeRenderPlan? = null

    private val resultStorage = gl.runInContext {
        deviceType.createResultStorage(object : RenderResults {
            var index = 0

            override fun <T: ResultBuffer> allocate(title: String, resultType: ResultType<T>): T =
                resultType.createResultBuffer(gl, index++)
        })
    }

    private val frameBuffer = gl.runInContext {
        gl.createFrameBuffer()
            .also { fb -> resultStorage.attachTo(fb) }
            .also { it.check() }
    }

    var arrangement: Arrangement
    // Workaround for compile error on case-insensitive FS:
    init { arrangement = gl.runInContext { Arrangement(0, emptyList()) } }

    fun addFixture(fixture: Fixture): FixtureRenderTarget {
        if (fixture.deviceType != deviceType) {
            throw IllegalArgumentException(
                "This RenderEngine can't accept ${fixture.deviceType} devices, only $deviceType."
            )
        }

        val rects = mapFixturePixelsToRects(
            nextPixelOffset,
            maxFramebufferWidth,
            fixture
        )
        val renderTarget = FixtureRenderTarget(
            fixture, nextRectOffset, rects, modelInfo, fixture.pixelCount, nextPixelOffset, resultStorage
        )
        nextPixelOffset += fixture.pixelCount
        nextRectOffset += rects.size

        renderTargetsToAdd.add(renderTarget)
        return renderTarget
    }

    fun removeRenderTarget(renderTarget: FixtureRenderTarget) {
        renderTargetsToRemove.add(renderTarget)
    }

    override fun compile(linkedPatch: LinkedPatch, feedResolver: FeedResolver): GlslProgram {
        logger.info { "Compiling ${linkedPatch.rootNode.title} on ${deviceType::class.simpleName}"}
        return super.compile(linkedPatch, feedResolver)
    }

    override fun onBind(engineFeed: EngineFeed) {
        engineFeed.maybeResizeAndPopulate(arrangement, renderTargets)
    }

    private fun EngineFeed.maybeResizeAndPopulate(arrangement: Arrangement?, renderTargets: List<FixtureRenderTarget>) {
        if (this is PerPixelEngineFeed) {
            resize(arrangement?.safeWidth ?: 1, arrangement?.safeHeight ?: 1) {
                renderTargets.forEach { renderTarget ->
                    setOnBuffer(renderTarget)
                }
            }
        }
    }

    override fun beforeRender() {
        incorporateNewFixtures()
        resultDeliveryStrategy.beforeRender()
    }

    override fun bindResults() {
        frameBuffer.bind()
    }

    override fun render() {
        gl.setViewport(0, 0, arrangement.pixWidth, arrangement.pixHeight)
        gl.check { clearColor(.1f, .5f, 0f, 1f) }
        gl.check { clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

        arrangement.render()
    }

    override fun afterRender() {
        resultDeliveryStrategy.afterRender(frameBuffer, resultStorage)
    }

    override suspend fun awaitResults() {
        resultDeliveryStrategy.awaitResults(frameBuffer, resultStorage)
    }

    // This must be run from within a GL context.
    private fun incorporateNewFixtures() {
        if (renderTargetsToRemove.isNotEmpty()) {
//            TODO("remove TBD")
        }

        if (renderTargetsToAdd.isNotEmpty()) {
            val newPixelCount = nextPixelOffset

            arrangement.release()

            renderTargets.addAll(renderTargetsToAdd)
            arrangement = gl.runInContext { Arrangement(newPixelCount, renderTargetsToAdd) }

            renderTargetsToAdd.clear()

            pixelCount = newPixelCount
        }
    }

    fun setRenderPlan(renderPlan: DeviceTypeRenderPlan?) {
        this.renderPlan = renderPlan
    }

    override fun onRelease() {
        arrangement.release()
        resultStorage.release()
        frameBuffer.release()
    }

    fun logStatus() {
        logger.info { "Rendering $pixelCount pixels for ${renderTargets.size} ${deviceType.title} fixtures."}
    }

    val Int.bufWidth: Int get() = max(minTextureWidth, min(this, maxFramebufferWidth))
    val Int.bufHeight: Int get() = this / maxFramebufferWidth + 1
    val Int.bufSize: Int get() = bufWidth * bufHeight

    inner class Arrangement(val pixelCount: Int, addedRenderTargets: List<FixtureRenderTarget>) {
        init {
            logger.info { "Creating ${deviceType::class.simpleName} arrangement with $pixelCount pixels" }
        }

        val pixWidth = pixelCount.bufWidth
        val pixHeight = pixelCount.bufHeight
        val safeWidth = max(pixWidth, 1.bufWidth)
        val safeHeight = max(pixHeight, 1.bufHeight)

        init {
            engineFeeds.values.forEach { engineFeed ->
                engineFeed.maybeResizeAndPopulate(this, addedRenderTargets)
            }

            resultStorage.resize(safeWidth, safeHeight)
        }

        private val quad: Quad =
            Quad(gl, renderTargets.flatMap {
                it.rects.map { rect ->
                    // Remap from pixel coordinates to normalized device coordinates.
                    Quad.Rect(
                        rect.top,
                        rect.left,
                        rect.bottom,
                        rect.right
                    )
                }
            })

        fun release() {
            logger.debug { "Release arrangement with ${renderTargets.count()} fixtures and $pixelCount pixels" }

            quad.release()
        }

        fun render() {
            engineFeeds.values.forEach { it.aboutToRenderFrame(renderTargets) }

            renderPlan?.forEach { programRenderPlan ->
                val program = programRenderPlan.program
                if (program != null) {
                    gl.useProgram(program)
                    program.setPixDimens(arrangement.pixWidth, arrangement.pixHeight)
                    program.aboutToRenderFrame()

                    quad.prepareToRender(program.vertexAttribLocation) {
                        programRenderPlan.renderTargets.forEach { renderTarget ->
                            renderTarget as FixtureRenderTarget
                            renderTarget.usingProgram(program)
                            program.aboutToRenderFixture(renderTarget)
                            quad.renderRects(renderTarget)
                        }
                    }
                } else {
                    programRenderPlan.renderTargets.forEach { renderTarget ->
                        renderTarget as FixtureRenderTarget
                        renderTarget.usingProgram(null)
                    }
                }
            }
        }
    }

    companion object {
        private val logger = Logger<ModelRenderEngine>()
        private const val fbMaxPixWidth = 1024

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

        fun Quad.renderRects(renderTarget: FixtureRenderTarget) {
            renderTarget.rects.indices.forEach { i ->
                this.renderRect(renderTarget.rect0Index + i)
            }
        }
    }
}

interface RenderResults {
    fun <T : ResultBuffer> allocate(title: String, resultType: ResultType<T>): T
}