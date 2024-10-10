package baaahs.gl.render

import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslCompilingProgram
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.LinkedProgram
import baaahs.time
import baaahs.timeSync
import kotlin.math.roundToInt

abstract class RenderEngine(val gl: GlContext) {
    internal val engineFeedContexts = mutableMapOf<FeedContext, EngineFeedContext>()

    val stats = Stats()

    private val vertexShader = GlslProgram.vertexShader(gl)

    internal fun cachedEngineFeed(feedContext: FeedContext): EngineFeedContext {
        return engineFeedContexts.getOrPut(feedContext) { bindFeed(feedContext) }
    }

    open fun compile(linkedProgram: LinkedProgram, feedResolver: FeedResolver): GlslCompilingProgram {
        val fragShader = gl.createFragmentShader(linkedProgram.toFullGlsl(gl.glslVersion))
        val program = gl.compile(vertexShader, fragShader)
        return GlslCompilingProgram(linkedProgram, vertexShader, fragShader, program, this, feedResolver)
    }

    private fun bindFeed(feedContext: FeedContext): EngineFeedContext =
        feedContext.bind(gl).also { engineFeed -> onBind(engineFeed) }

    abstract fun onBind(engineFeedContext: EngineFeedContext)

    fun draw() {
        gl.runInContext {
            stats.prepareMs += timeSync {
                beforeRender()
                bindResults()
            }

            stats.renderMs += timeSync {
                render()
                afterRender()
            }
        }
    }

    suspend fun finish() {
        gl.asyncRunInContext {
            stats.readPxMs += time { awaitResults() }
        }
        stats.frameCount++
    }

    /** This is run from within a GL context. */
    abstract fun beforeRender()

    /** This is run from within a GL context. */
    abstract fun bindResults()

    /** This is run from within a GL context. */
    protected abstract fun render()

    /** This is run from within a GL context. */
    abstract fun afterRender()

    /** This is run from within a GL context. */
    abstract suspend fun awaitResults()

    fun release() {
        gl.runInContext {
            onRelease()
            engineFeedContexts.forEach { (feed, engineFeed) ->
                engineFeed.release()
                feed.release()
            }
        }
    }

    /** This is run from within a GL context. */
    abstract fun onRelease()

    class Stats {
        var prepareMs = 0; internal set
        var renderMs = 0; internal set
        var readPxMs = 0; internal set
        var frameCount = 0; internal set

        fun dump() {
            val count = frameCount * 1f
            fun Int.pretty() = ((this / count * 10).roundToInt() / 10f).toString()
            println(
                "Average time drawing $frameCount frames:\n" +
                        " prepareMs=${prepareMs.pretty()}ms/frame\n" +
                        "  renderMs=${renderMs.pretty()}ms/frame\n" +
                        "  readPxMs=${readPxMs.pretty()}ms/frame\n" +
                        "   totalMs=${(prepareMs + renderMs + readPxMs).pretty()}ms/frame"
            )
        }

        fun reset() {
            prepareMs = 0
            renderMs = 0
            readPxMs = 0
            frameCount = 0
        }
    }
}
