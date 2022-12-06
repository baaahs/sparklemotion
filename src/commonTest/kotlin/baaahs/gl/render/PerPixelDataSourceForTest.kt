package baaahs.gl.render

import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.FeedContext
import baaahs.gl.data.PerPixelEngineFeed
import baaahs.gl.data.PerPixelProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.param.FloatsParamBuffer
import baaahs.gl.param.ParamBuffer
import baaahs.gl.patch.ContentType
import baaahs.show.DataSource
import baaahs.show.UpdateMode
import baaahs.util.RefCounted
import baaahs.util.RefCounter

class PerPixelDataSourceForTest(val updateMode: UpdateMode) : DataSource {
    override val pluginPackage: String get() = error("not implemented")
    override val title: String get() = "Per Pixel Data Source For Test"
    override fun getType(): GlslType = GlslType.Float
    override val contentType: ContentType get() = ContentType.Unknown

    val feeds = mutableListOf<TestFeedContext>()
    val engineFeeds = mutableListOf<TestEngineFeed>()
    val programFeeds = mutableListOf<TestEngineFeed.TestProgramFeed>()

    var counter = 0f

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext =
        TestFeedContext(id).also { feeds.add(it) }

    inner class TestFeedContext(val id: String) : FeedContext, RefCounted by RefCounter() {
        var released = false
        override fun bind(gl: GlContext): EngineFeed = TestEngineFeed(gl).also { engineFeeds.add(it) }
        override fun onRelease() { released = released.truify() }
    }

    inner class TestEngineFeed(gl: GlContext) : PerPixelEngineFeed {
        var released = false
        override val updateMode: UpdateMode get() = this@PerPixelDataSourceForTest.updateMode
        override val buffer: FloatsParamBuffer = FloatsParamBuffer("---", 1, gl)

        override fun setOnBuffer(renderTarget: RenderTarget) = run {
            renderTarget as FixtureRenderTarget
            counter++
            buffer.scoped(renderTarget) { pixelIndex ->
                counter * 10 + pixelIndex
            }
            Unit
        }

        override fun bind(glslProgram: GlslProgram): PerPixelProgramFeed = TestProgramFeed(glslProgram).also { programFeeds.add(it) }
        override fun release() = run { super.release(); released = released.truify() }

        inner class TestProgramFeed(glslProgram: GlslProgram) : PerPixelProgramFeed(updateMode) {
            override val buffer: ParamBuffer get() = this@TestEngineFeed.buffer
            override val uniform = glslProgram.getUniform("perPixelDataTexture") ?: error("no uniform")
            var released = false
            override fun release() = run { super.release(); released = released.truify() }
        }
    }
}