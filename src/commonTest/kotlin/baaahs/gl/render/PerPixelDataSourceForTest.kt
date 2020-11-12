package baaahs.gl.render

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.fixtures.FloatsParamBuffer
import baaahs.fixtures.ParamBuffer
import baaahs.gl.GlContext
import baaahs.gl.data.*
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.Plugin
import baaahs.show.DataSource
import baaahs.show.UpdateMode

class PerPixelDataSourceForTest(val updateMode: UpdateMode) : DataSource {
    override val pluginPackage: String get() = error("not implemented")
    override val title: String get() = error("not implemented")
    override fun getType(): GlslType = GlslType.Float
    override fun getContentType(): ContentType = error("not implemented")

    val feeds = mutableListOf<TestFeed>()
    val engineFeeds = mutableListOf<TestEngineFeed>()
    val programFeeds = mutableListOf<TestEngineFeed.TestProgramFeed>()

    var counter = 0f

    override fun createFeed(showPlayer: ShowPlayer, plugin: Plugin, id: String): Feed =
        error("not implemented")

    override fun createFixtureFeed(): Feed = TestFeed().also { feeds.add(it) }

    inner class TestFeed : Feed, RefCounted by RefCounter() {
        var released = false
        override fun bind(gl: GlContext): EngineFeed = TestEngineFeed(gl).also { engineFeeds.add(it) }
        override fun release() = run { super.release(); released = released.truify() }
    }

    inner class TestEngineFeed(gl: GlContext) : PerPixelEngineFeed {
        var released = false
        override val updateMode: UpdateMode get() = this@PerPixelDataSourceForTest.updateMode
        override val buffer: FloatsParamBuffer = FloatsParamBuffer("---", 1, gl)

        override fun setOnBuffer(renderTarget: RenderTarget) = run {
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