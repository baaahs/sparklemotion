package baaahs.gl.render

import baaahs.device.FixtureType
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureTypeRenderPlan
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.PerPixelEngineFeedContext
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslCompilingProgram
import baaahs.gl.patch.LinkedProgram
import baaahs.gl.result.ResultBuffer
import baaahs.gl.result.ResultType
import baaahs.util.Logger
import com.danielgergely.kgl.GL_COLOR_BUFFER_BIT
import com.danielgergely.kgl.GL_DEPTH_BUFFER_BIT
import kotlin.math.max
import kotlin.math.min

class ModelRenderEngine(
    gl: GlContext,
    private val fixtureType: FixtureType,
    private val minTextureWidth: Int = 16,
    private val maxFramebufferWidth: Int = fbMaxPixWidth,
    private val resultDeliveryStrategy: ResultDeliveryStrategy = SyncResultDeliveryStrategy()
) : RenderEngine(gl) {
    private val renderTargetsToAdd: MutableList<FixtureRenderTarget> = mutableListOf()
    private val renderTargetsToRemove: MutableList<FixtureRenderTarget> = mutableListOf()
    var componentCount: Int = 0
    var nextComponentOffset: Int = 0
    var nextRectOffset: Int = 0

    private val renderTargets: MutableList<FixtureRenderTarget> = mutableListOf()
    private var renderPlan: FixtureTypeRenderPlan? = null

    private val resultStorage = gl.runInContext {
        fixtureType.createResultStorage(object : RenderResults {
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
        if (fixture.fixtureType != fixtureType) {
            throw IllegalArgumentException(
                "This RenderEngine can't accept ${fixture.fixtureType} devices, only $fixtureType."
            )
        }

        val rects = mapFixtureComponentsToRects(
            nextComponentOffset,
            maxFramebufferWidth,
            fixture
        )
        val renderTarget = FixtureRenderTarget(
            fixture, nextRectOffset, rects, fixture.componentCount, nextComponentOffset, resultStorage
        )
        nextComponentOffset += fixture.componentCount
        nextRectOffset += rects.size

        renderTargetsToAdd.add(renderTarget)
        return renderTarget
    }

    fun removeRenderTarget(renderTarget: FixtureRenderTarget) {
        renderTargetsToRemove.add(renderTarget)
    }

    override fun compile(linkedProgram: LinkedProgram, feedResolver: FeedResolver): GlslCompilingProgram {
        logger.debug { "Compiling ${linkedProgram.rootNode.title} for ${fixtureType::class.simpleName}"}
        return super.compile(linkedProgram, feedResolver)
    }

    override fun onBind(engineFeedContext: EngineFeedContext) {
        engineFeedContext.maybeResizeAndPopulate(arrangement, renderTargets)
    }

    private fun EngineFeedContext.maybeResizeAndPopulate(arrangement: Arrangement?, renderTargets: List<FixtureRenderTarget>) {
        if (this is PerPixelEngineFeedContext) {
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
            val newComponentCount = nextComponentOffset

            arrangement.release()

            renderTargets.addAll(renderTargetsToAdd)
            arrangement = gl.runInContext { Arrangement(newComponentCount, renderTargetsToAdd) }

            renderTargetsToAdd.clear()

            componentCount = newComponentCount
        }
    }

    fun setRenderPlan(renderPlan: FixtureTypeRenderPlan?) {
        this.renderPlan = renderPlan
    }

    override fun onRelease() {
        arrangement.release()
        resultStorage.release()
        frameBuffer.release()
    }

    fun logStatus() {
        logger.info { "Rendering $componentCount components for ${renderTargets.size} ${fixtureType.title} fixtures."}
    }

    val Int.bufWidth: Int get() = max(minTextureWidth, min(this, maxFramebufferWidth))
    val Int.bufHeight: Int get() = this / maxFramebufferWidth + 1
    val Int.bufSize: Int get() = bufWidth * bufHeight

    inner class Arrangement(val componentCount: Int, addedRenderTargets: List<FixtureRenderTarget>) {
        init {
            logger.info { "Creating ${fixtureType::class.simpleName} arrangement with $componentCount components" }
        }

        val pixWidth = componentCount.bufWidth
        val pixHeight = componentCount.bufHeight
        val safeWidth = max(pixWidth, 1.bufWidth)
        val safeHeight = max(pixHeight, 1.bufHeight)

        init {
            engineFeedContexts.values.forEach { engineFeed ->
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
            logger.debug { "Release arrangement with ${renderTargets.count()} fixtures and $componentCount components" }

            quad.release()
        }

        fun render() {
            engineFeedContexts.values.forEach { it.aboutToRenderFrame(renderTargets) }

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
        internal fun mapFixtureComponentsToRects(nextPix: Int, pixWidth: Int, fixture: Fixture): List<Quad.Rect> {
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

            var nextComponentOffset = nextPix
            var componentsLeft = fixture.componentCount
            val rects = mutableListOf<Quad.Rect>()
            while (componentsLeft > 0) {
                val rowPixelOffset = nextComponentOffset % pixWidth
                val rowPixelsLeft = pixWidth - rowPixelOffset
                val rowPixelsTaken = min(componentsLeft, rowPixelsLeft)
                rects.add(makeQuad(nextComponentOffset, rowPixelsTaken))

                nextComponentOffset += rowPixelsTaken
                componentsLeft -= rowPixelsTaken
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