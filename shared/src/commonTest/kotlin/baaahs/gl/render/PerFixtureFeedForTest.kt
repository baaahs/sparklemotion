package baaahs.gl.render

import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.show.Feed
import baaahs.show.FeedOpenContext
import baaahs.show.UpdateMode
import baaahs.util.RefCounted
import baaahs.util.RefCounter

class PerFixtureFeedForTest(val updateMode: UpdateMode) : Feed {
    override val pluginPackage: String get() = error("not implemented")
    override val title: String get() = "Per Fixture Feed For Test"
    override fun getType(): GlslType = GlslType.Float
    override val contentType: ContentType get() = ContentType.Unknown

    val feeds = mutableListOf<TestFeedContext>()
    val engineFeeds = mutableListOf<TestEngineFeedContext>()
    val programFeeds = mutableListOf<TestProgramFeedContext>()

    var counter = 0

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext =
        TestFeedContext(id).also { feeds.add(it) }

    inner class TestFeedContext(val id: String) : FeedContext, RefCounted by RefCounter() {
        var released = false
        override fun bind(gl: GlContext): EngineFeedContext = TestEngineFeedContext().also { engineFeeds.add(it) }
        override fun onRelease() { released = released.truify() }
    }

    inner class TestEngineFeedContext : EngineFeedContext {
        var released = false
        override fun bind(glslProgram: GlslProgram): ProgramFeedContext = TestProgramFeedContext(glslProgram).also { programFeeds.add(it) }
        override fun release() = run { super.release(); released = released.truify() }
    }

    inner class TestProgramFeedContext(glslProgram: GlslProgram) : ProgramFeedContext {
        override val updateMode: UpdateMode get() = this@PerFixtureFeedForTest.updateMode

        val uniform = glslProgram.getIntUniform("perFixtureData")
        var released = false

        override fun setOnProgram() = run {
            uniform?.set(counter++)
        } as Unit

        override fun setOnProgram(renderTarget: RenderTarget) = setOnProgram()

        override fun release() = run { super.release(); released = released.truify() }
    }
}