package baaahs.gl.render

import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.PerPixelEngineFeedContext
import baaahs.gl.data.PerPixelProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.param.FloatsParamBuffer
import baaahs.gl.param.ParamBuffer
import baaahs.gl.patch.ContentType
import baaahs.show.Feed
import baaahs.show.UpdateMode
import baaahs.util.RefCounted
import baaahs.util.RefCounter

class PerPixelFeedForTest(val updateMode: UpdateMode) : Feed {
    override val pluginPackage: String get() = error("not implemented")
    override val title: String get() = "Per Pixel Feed For Test"
    override fun getType(): GlslType = GlslType.Float
    override val contentType: ContentType get() = ContentType.Unknown

    val feeds = mutableListOf<TestFeedContext>()
    val engineFeeds = mutableListOf<TestEngineFeedContext>()
    val programFeeds = mutableListOf<TestEngineFeedContext.TestProgramFeedContext>()

    var counter = 0f

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext =
        TestFeedContext(id).also { feeds.add(it) }

    inner class TestFeedContext(val id: String) : FeedContext, RefCounted by RefCounter() {
        var released = false
        override fun bind(gl: GlContext): EngineFeedContext = TestEngineFeedContext(gl).also { engineFeeds.add(it) }
        override fun onRelease() { released = released.truify() }
    }

    inner class TestEngineFeedContext(gl: GlContext) : PerPixelEngineFeedContext {
        var released = false
        override val updateMode: UpdateMode get() = this@PerPixelFeedForTest.updateMode
        override val buffer: FloatsParamBuffer = FloatsParamBuffer("---", 1, gl)

        override fun setOnBuffer(renderTarget: RenderTarget) = run {
            renderTarget as FixtureRenderTarget
            counter++
            buffer.scoped(renderTarget) { pixelIndex ->
                counter * 10 + pixelIndex
            }
            Unit
        }

        override fun bind(glslProgram: GlslProgram): PerPixelProgramFeedContext = TestProgramFeedContext(glslProgram).also { programFeeds.add(it) }
        override fun release() = run { super.release(); released = released.truify() }

        inner class TestProgramFeedContext(glslProgram: GlslProgram) : PerPixelProgramFeedContext(updateMode) {
            override val buffer: ParamBuffer get() = this@TestEngineFeedContext.buffer
            override val textureUniform = glslProgram.getTextureUniform("perPixelDataTexture") ?: error("no uniform")
            var released = false
            override fun release() = run { super.release(); released = released.truify() }
        }
    }
}