package baaahs.gl.render

import baaahs.fixtures.DeviceType
import baaahs.fixtures.DeviceTypeRenderPlan
import baaahs.fixtures.Fixture
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.PerPixelEngineFeed
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.LinkedPatch
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
    private val minTextureWidth: Int = 16
) : RenderEngine(gl) {
    private val renderTargetsToAdd: MutableList<RenderTarget> = mutableListOf()
    private val renderTargetsToRemove: MutableList<RenderTarget> = mutableListOf()
    var pixelCount: Int = 0
    var nextPixelOffset: Int = 0
    var nextRectOffset: Int = 0

    private val renderTargets: MutableList<RenderTarget> = mutableListOf()
    private var renderPlan: DeviceTypeRenderPlan? = null

    private val resultBuffers = gl.runInContext {
        deviceType.resultParams
            .mapIndexed { index, deviceParam -> deviceParam.allocate(gl, index) }
    }

    private val frameBuffer = gl.runInContext {
        gl.createFrameBuffer()
            .also { fb -> resultBuffers.forEach { it.attachTo(fb) } }
            .also { it.check() }
    }

    var arrangement: Arrangement
    // Workaround for compile error on case-insensitive FS:
    init { arrangement = gl.runInContext { Arrangement(0, emptyList()) } }

    fun addFixture(fixture: Fixture): RenderTarget {
        if (fixture.deviceType != deviceType) {
            throw IllegalArgumentException(
                "This RenderEngine can't accept ${fixture.deviceType} devices, only $deviceType."
            )
        }

        val rects = mapFixturePixelsToRects(
            nextPixelOffset,
            fbMaxPixWidth,
            fixture
        )
        val renderTarget = RenderTarget(
            fixture, nextRectOffset, rects, modelInfo, fixture.pixelCount, nextPixelOffset, resultBuffers
        )
        nextPixelOffset += fixture.pixelCount
        nextRectOffset += rects.size

        renderTargetsToAdd.add(renderTarget)
        return renderTarget
    }

    fun removeRenderTarget(renderTarget: RenderTarget) {
        renderTargetsToRemove.add(renderTarget)
    }

    override fun compile(linkedPatch: LinkedPatch, feedResolver: FeedResolver): GlslProgram {
        logger.info { "Compiling ${linkedPatch.shaderInstance.shader.title} on ${deviceType::class.simpleName}"}
        return super.compile(linkedPatch, feedResolver)
    }

    override fun onBind(engineFeed: EngineFeed) {
        engineFeed.maybeResizeAndPopulate(arrangement, renderTargets)
    }

    private fun EngineFeed.maybeResizeAndPopulate(arrangement: Arrangement?, renderTargets: List<RenderTarget>) {
        if (this is PerPixelEngineFeed) {
            resize(arrangement?.safeWidth ?: 1, arrangement?.safeHeight ?: 1) {
                renderTargets.forEach { renderTarget ->
                    setOnBuffer(renderTarget)
                }
            }
        }
    }

    override fun beforeFrame() {
        incorporateNewFixtures()
    }

    override fun bindResults() {
        if (resultBuffers.isNotEmpty()) {
            frameBuffer.bind()
        }
    }

    override fun render() {
        gl.setViewport(0, 0, arrangement.pixWidth, arrangement.pixHeight)
        gl.check { clearColor(.1f, .2f, 0f, 1f) }
        gl.check { clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

        arrangement.render()
    }

    override fun afterFrame() {
        copyResultsToCpuBuffer()
    }

    private fun copyResultsToCpuBuffer() {
        resultBuffers.forEach { it.afterFrame(frameBuffer) }
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
        resultBuffers.forEach { it.release() }
        frameBuffer.release()
    }

    val Int.bufWidth: Int get() = max(minTextureWidth, min(this, fbMaxPixWidth))
    val Int.bufHeight: Int get() = this / fbMaxPixWidth + 1
    val Int.bufSize: Int get() = bufWidth * bufHeight

    inner class Arrangement(val pixelCount: Int, addedRenderTargets: List<RenderTarget>) {
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

            resultBuffers.forEach {
                it.resize(safeWidth, safeHeight)
            }
        }

        private val quad: Quad =
            Quad(gl, renderTargets.flatMap {
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
            logger.debug { "Release arrangement with ${renderTargets.count()} fixtures and $pixelCount pixels" }

            quad.release()
        }

        fun render() {
            engineFeeds.values.forEach { it.aboutToRenderFrame(renderTargets) }

            renderPlan?.forEach { programRenderPlan ->
                val program = programRenderPlan.program
                if (program != null) {
                    gl.useProgram(program)
                    program.aboutToRenderFrame()

                    quad.prepareToRender(program.vertexAttribLocation) {
                        programRenderPlan.renderTargets.forEach { renderTarget ->
                            renderTarget.usingProgram(program)
                            program.aboutToRenderFixture(renderTarget)
                            quad.renderRects(renderTarget)
                        }
                    }
                } else {
                    programRenderPlan.renderTargets.forEach { renderTarget ->
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

        fun Quad.renderRects(renderTarget: RenderTarget) {
            renderTarget.rects.indices.forEach { i ->
                this.renderRect(renderTarget.rect0Index + i)
            }
        }
    }
}