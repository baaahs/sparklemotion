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
import baaahs.util.unixMillis
import com.danielgergely.kgl.GL_COLOR_BUFFER_BIT
import com.danielgergely.kgl.GL_DEPTH_BUFFER_BIT
import kotlinx.datetime.Clock
import kotlin.math.max
import kotlin.math.min

class ComponentRenderEngine(
    gl: GlContext,
    private val fixtureType: FixtureType,
    private val minTextureWidth: Int = 16,
    private val maxFramebufferWidth: Int = fbMaxPixWidth,
    private val resultDeliveryStrategy: ResultDeliveryStrategy = SyncResultDeliveryStrategy()
) : FixtureRenderEngine(gl) {
    private val renderTargetsToAdd: MutableList<FixtureRenderTarget> = mutableListOf()
    private val renderTargetsToRemove: MutableList<FixtureRenderTarget> = mutableListOf()
    var componentCount: Int = 0
    private var nextComponentOffset: Int = 0
    private var nextRectOffset: Int = 0

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

    override fun addFixture(fixture: Fixture): FixtureRenderTarget {
        if (fixture.fixtureType != fixtureType) {
            throw IllegalArgumentException(
                "This RenderEngine can't accept ${fixture.fixtureType} devices, only $fixtureType."
            )
        }

        val rects = mapFixtureComponentsToRects(
            nextComponentOffset,
            maxFramebufferWidth,
            fixture.componentCount
        )
        val renderTarget = FixtureRenderTarget(
            fixture, nextRectOffset, rects, fixture.componentCount, nextComponentOffset, resultStorage,
            this@ComponentRenderEngine
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
        if ((Clock.System.now().unixMillis % 1000) < 500) {
            gl.check { clearColor(0f, 0f, 1/255f, 1f) }
        } else {
            gl.check { clearColor(0f, 0f, 0f, 1f) }
        }
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
//            TODO(question for xian): ("remove TBD"), how do we do this?
//            renderTargets.removeAll(renderTargetsToRemove)
            // ^^ doing this breaks rendering for some reason.

            // Let's just mark them as zombies for now.
            for (renderTarget in renderTargetsToRemove) {
                renderTarget.isZombie = true
            }

            // TODO: We should reclaim dead buffer space at some point too.
//            context: https://github.com/baaahs/sparklemotion/pull/613/files#r1748974029
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
        val actuallyRenderedComponentCount =
            renderTargets.filter { !it.isZombie }.sumOf { it.componentCount }
        logger.info {
            "Rendering $actuallyRenderedComponentCount components for ${renderTargets.size} ${fixtureType.title} fixtures" +
                " (with ${componentCount - actuallyRenderedComponentCount} zombie components)."
        }
    }

    val Int.bufWidth: Int get() = max(minTextureWidth, min(this, maxFramebufferWidth))
    val Int.bufHeight: Int get() = (this - 1) / maxFramebufferWidth + 1
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
        private val logger = Logger<ComponentRenderEngine>()
        private const val fbMaxPixWidth = 1024

        /**
         * Resulting Rects represent render coordinates which cover the pixels
         * corresponding to a fixture.
         *
         * @param startPix the pixel index for the start of the first component
         * @param fbPixWidth the width of the framebuffer in pixels
         * @param componentCount the number of components to map
         * @return a list of Rects corresponding to the components to the framebuffer
         **/
        internal fun mapFixtureComponentsToRects(
            startPix: Int,
            fbPixWidth: Int,
            componentCount: Int
        ): List<Quad.Rect> {
            fun makeRect(offsetPix: Int, widthPix: Int): Quad.Rect {
                val xStartPixel = offsetPix % fbPixWidth
                val yStartPixel = offsetPix / fbPixWidth
                val xEndPixel = xStartPixel + widthPix
                val yEndPixel = yStartPixel + 1
                return Quad.Rect(
                    yStartPixel.toFloat(),
                    xStartPixel.toFloat(),
                    yEndPixel.toFloat(),
                    xEndPixel.toFloat()
                )
            }

            fun Quad.Rect.isFullRow(): Boolean =
                left == 0f && right == fbPixWidth.toFloat()
            fun Quad.Rect.addRow(): Quad.Rect =
                Quad.Rect(top, left, bottom + 1, right)

            var nextComponentOffset = startPix
            var componentsLeft = componentCount
            val rects = mutableListOf<Quad.Rect>()
            var priorRect: Quad.Rect? = null
            while (componentsLeft > 0) {
                val rowPixelOffset = nextComponentOffset % fbPixWidth
                val rowPixelsLeft = fbPixWidth - rowPixelOffset
                val rowPixelsTaken = min(componentsLeft, rowPixelsLeft)
                val nextRect = makeRect(nextComponentOffset, rowPixelsTaken)
                if (priorRect?.isFullRow() == true && nextRect.isFullRow()) {
                    priorRect = priorRect.addRow()
                } else {
                    if (priorRect != null) rects.add(priorRect)
                    priorRect = nextRect
                }

                nextComponentOffset += rowPixelsTaken
                componentsLeft -= rowPixelsTaken
            }
            if (priorRect != null) rects.add(priorRect)
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
