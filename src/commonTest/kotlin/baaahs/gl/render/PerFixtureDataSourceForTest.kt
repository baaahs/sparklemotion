package baaahs.gl.render

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.show.DataSource
import baaahs.show.UpdateMode

class PerFixtureDataSourceForTest(val updateMode: UpdateMode) : DataSource {
    override val pluginPackage: String get() = error("not implemented")
    override val title: String get() = "Per Fixture Data Source For Test"
    override fun getType(): GlslType = GlslType.Float
    override val contentType: ContentType get() = ContentType.Unknown

    val feeds = mutableListOf<TestFeed>()
    val engineFeeds = mutableListOf<TestEngineFeed>()
    val programFeeds = mutableListOf<TestProgramFeed>()

    var counter = 0

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed =
        TestFeed(id).also { feeds.add(it) }

    inner class TestFeed(val id: String) : Feed, RefCounted by RefCounter() {
        var released = false
        override fun bind(gl: GlContext): EngineFeed = TestEngineFeed().also { engineFeeds.add(it) }
        override fun onRelease() { released = released.truify() }
    }

    inner class TestEngineFeed : EngineFeed {
        var released = false
        override fun bind(glslProgram: GlslProgram): ProgramFeed = TestProgramFeed(glslProgram).also { programFeeds.add(it) }
        override fun release() = run { super.release(); released = released.truify() }
    }

    inner class TestProgramFeed(glslProgram: GlslProgram) : ProgramFeed {
        override val updateMode: UpdateMode get() = this@PerFixtureDataSourceForTest.updateMode

        val uniform = glslProgram.getUniform("perFixtureData")
        var released = false

        override fun setOnProgram() = run {
            uniform?.set(counter++)
        } as Unit

        override fun setOnProgram(renderTarget: RenderTarget) = setOnProgram()

        override fun release() = run { super.release(); released = released.truify() }
    }
}