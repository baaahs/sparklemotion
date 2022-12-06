package baaahs.gl.render

import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.FeedContext
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslProgramImpl
import baaahs.gl.patch.LinkedProgram
import baaahs.show.DataSource
import baaahs.time
import baaahs.timeSync
import kotlin.math.roundToInt

abstract class RenderEngine(val gl: GlContext) {
    internal val engineFeeds = mutableMapOf<FeedContext, EngineFeed>()

    val stats = Stats()

    private fun cachedEngineFeed(feedContext: FeedContext): EngineFeed {
        return engineFeeds.getOrPut(feedContext) { bindFeed(feedContext) }
    }

    open fun compile(linkedProgram: LinkedProgram, feedResolver: FeedResolver): GlslProgram {
        return GlslProgramImpl(gl, linkedProgram) { id: String, dataSource: DataSource ->
            val feed = feedResolver.openFeed(id, dataSource)
            feed?.let { cachedEngineFeed(it)}
        }
    }

    private fun bindFeed(feedContext: FeedContext): EngineFeed =
        feedContext.bind(gl).also { engineFeed -> onBind(engineFeed) }

    abstract fun onBind(engineFeed: EngineFeed)

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
            engineFeeds.forEach { (feed, engineFeed) ->
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
