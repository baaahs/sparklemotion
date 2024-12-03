package baaahs.gl.render

import baaahs.TestModel
import baaahs.describe
import baaahs.device.EnumeratedPixelLocations
import baaahs.device.FixtureType
import baaahs.device.PixelLocations
import baaahs.englishize
import baaahs.fixtures.*
import baaahs.geom.Vector3F
import baaahs.gl.*
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.ContentType.Companion.Color
import baaahs.gl.patch.ProgramLinker
import baaahs.gl.result.ResultStorage
import baaahs.gl.shader.InputPort
import baaahs.kotest.value
import baaahs.model.Model
import baaahs.only
import baaahs.plugin.SerializerRegistrar
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureOptions
import baaahs.show.*
import baaahs.show.Shader
import baaahs.show.live.LinkedPatch
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import baaahs.ui.View
import com.danielgergely.kgl.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

@Suppress("unused")
class ComponentRenderEngineSpec : DescribeSpec({
    describe<ComponentRenderEngine> {
        val gl by value { FakeGlContext() }
        val updateMode by value { UpdateMode.ONCE }
        val fixtureFeed by value { PerFixtureFeedForTest(updateMode) }
        val pixelFeed by value { PerPixelFeedForTest(updateMode) }
        val feed by value<Feed> { fixtureFeed }
        val fixtureType by value { FixtureTypeForTest(feed) }
        val maxFramebufferWidth by value { 64 }
        val renderEngine by value {
            ComponentRenderEngine(gl, fixtureType, minTextureWidth = 1, maxFramebufferWidth)
        }
        val texture by value { gl.textures.only("texture") }

        context("when engine has just been started") {
            beforeEach { renderEngine.run { /* No-op. */ } }

            it("should have no feeds open yet") {
                fixtureFeed.feeds.size.shouldBe(0)
                fixtureFeed.engineFeeds.size.shouldBe(0)
                fixtureFeed.programFeeds.size.shouldBe(0)
            }

            context("when the engine is released") {
                beforeEach { renderEngine.release() }

                it("should release FeedContexts and EngineFeedContexts") {
                    fixtureFeed.feeds.all { it.released }.shouldBeTrue()
                    fixtureFeed.engineFeeds.all { it.released }.shouldBeTrue()
                }
            }
        }

        context("when a program is compiled") {
            val shader by value {
                Shader(
                    "Title", """
                        uniform float time;
                
                        void main(void) { gl_FragColor = doesntMatter(gl_FragCoord); }
                    """.trimIndent()
                )
            }
            val openShader by value { testToolchain.openShader(shader) }
            val incomingLinks by value { mapOf("gl_FragCoord" to feed.link("coord")) }
            val linkedPatch by value {
                val rootNode = LinkedPatch(openShader, incomingLinks, Stream.Main, 0f)
                ProgramLinker(rootNode).buildLinkedProgram()
            }
            val program by value {
                renderEngine.compile(linkedPatch) { id, feed -> feed.open(FakeShowPlayer(), id) }.bind()
            }
            val initialProgram by value<Array<GlslProgram?>> { arrayOf(program) } // Array is a workaround for a bug in by value.
            val fakeGlProgram by value { gl.programs.only("program") }

            beforeEach { initialProgram.run { } }

            it("should bind EngineFeedContexts for each feed") {
                fixtureFeed.feeds.size.shouldBe(1)
                fixtureFeed.engineFeeds.size.shouldBe(1)
                fixtureFeed.programFeeds.size.shouldBe(1)
            }

            it("should bind program uniforms") {
                fakeGlProgram.uniformNames().shouldBe(setOf("perFixtureData"))
            }

            context("when the feed is per-pixel") {
                override(feed) { pixelFeed }

                it("should allocate a texture to hold per-pixel data") {
                    (texture.width to texture.height).shouldBe(1 to 1)
                    texture.internalFormat.shouldBe(GlContext.GL_R32F)
                    texture.format.shouldBe(GL_RED)
                    texture.type.shouldBe(GL_FLOAT)
                    texture.params[GL_TEXTURE_MIN_FILTER].shouldBe(GL_NEAREST)
                    texture.params[GL_TEXTURE_MAG_FILTER].shouldBe(GL_NEAREST)

                    texture.buffer?.size.shouldBe(1)
                }
            }

            context("when the program is released") {
                beforeEach { program.release() }

                it("should release FeedContexts and EngineFeedContexts") {
                    fixtureFeed.feeds.all { it.released }.shouldBeFalse()
                    fixtureFeed.engineFeeds.all { it.released }.shouldBeFalse()
                    fixtureFeed.programFeeds.all { it.released }.shouldBeTrue()
                }
            }

            context("when the engine is released") {
                beforeEach { renderEngine.release() }

                it("should release EngineFeedContexts") {
                    fixtureFeed.engineFeeds.all { it.released }.shouldBeTrue()
                }

                context("when the feed is per-pixel") {
                    override(feed) { pixelFeed }

                    it("should release the texture") {
                        texture.isDeleted.shouldBeTrue()
                    }
                }
            }

            context("when a fixture with maxFramebufferWidth pixels is added") {
                val fixture1Target by value {
                    renderEngine.addFixture(testFixture(fixtureType, maxFramebufferWidth, 0f))
                }

                val addFixture by value { { fixture1Target.run { } } }
                val renderPlan by value { renderPlanFor(initialProgram[0]!!, fixture1Target) }
                val drawTwoFrames by value {
                    {
                        renderEngine.setRenderPlan(renderPlan)
                        renderEngine.draw()
                        renderEngine.draw()
                    }
                }

                beforeEach { addFixture() }

                context("when two frames are rendered") {
                    beforeEach {
                        // Two frames so we can observe when uniforms are updated.
                        drawTwoFrames()
                    }

                    it("should generate a RenderTarget with appropriate rects") {
                        fixture1Target.component0Index.shouldBe(0)
                        fixture1Target.componentCount.shouldBe(maxFramebufferWidth)

                        fixture1Target.rect0Index.shouldBe(0)
                        fixture1Target.rects.shouldBe(
                            listOf(
                                Quad.Rect(0f, 0f, 1f, 64f)
                            )
                        )
                    }

                    context("when the feed is per-pixel") {
                        override(feed) { pixelFeed }

                        it("should allocate a texture to hold per-pixel data for all fixtures") {
                            (texture.width to texture.height)
                                .shouldBe(64 to 1)

                            fakeGlProgram.renders.map { it.findUniformTextureConfig("perPixelDataTexture").buffer }
                                .shouldContainExactly(
                                    listOf(10f, 11f, 12f, 13f, 14f, 15f, 16f, 17f, 18f, 19f, 20f, 21f, 22f, 23f, 24f, 25f, 26f, 27f, 28f, 29f, 30f, 31f, 32f, 33f, 34f, 35f, 36f, 37f, 38f, 39f, 40f, 41f, 42f, 43f, 44f, 45f, 46f, 47f, 48f, 49f, 50f, 51f, 52f, 53f, 54f, 55f, 56f, 57f, 58f, 59f, 60f, 61f, 62f, 63f, 64f, 65f, 66f, 67f, 68f, 69f, 70f, 71f, 72f, 73f),
                                    listOf(10f, 11f, 12f, 13f, 14f, 15f, 16f, 17f, 18f, 19f, 20f, 21f, 22f, 23f, 24f, 25f, 26f, 27f, 28f, 29f, 30f, 31f, 32f, 33f, 34f, 35f, 36f, 37f, 38f, 39f, 40f, 41f, 42f, 43f, 44f, 45f, 46f, 47f, 48f, 49f, 50f, 51f, 52f, 53f, 54f, 55f, 56f, 57f, 58f, 59f, 60f, 61f, 62f, 63f, 64f, 65f, 66f, 67f, 68f, 69f, 70f, 71f, 72f, 73f)
//                                    listOf(10f, 20f, 21f), // First frame, fixture1.
//                                    listOf(10f, 20f, 21f), // First frame, fixture2.
//                                    listOf(10f, 20f, 21f), // Second frame, fixture1.
//                                    listOf(10f, 20f, 21f)  // Second frame, fixture2.
                                )
                        }
                    }
                }
            }

            context("when fixtures are added") {
                val fixture1Target by value {
                    renderEngine.addFixture(testFixture(fixtureType, 1, 0f))
                }
                val fixture2Target by value {
                    renderEngine.addFixture(testFixture(fixtureType, 2, 0.1f))
                }
                val addTwoFixtures by value { { fixture1Target.run { }; fixture2Target.run { } } }
                val renderPlan by value { renderPlanFor(initialProgram[0]!!, fixture1Target, fixture2Target) }
                val drawTwoFrames by value {
                    {
                        renderEngine.setRenderPlan(renderPlan)
                        renderEngine.draw()
                        renderEngine.draw()
                    }
                }

                beforeEach { addTwoFixtures() }

                context("when two frames are rendered") {
                    beforeEach {
                        // Two frames so we can observe when uniforms are updated.
                        drawTwoFrames()
                    }

                    it("should set uniforms appropriately") {
                        fakeGlProgram.renders.map { it.uniforms["perFixtureData"] }
                            .shouldContainExactly(0, 0, 0, 0)
                    }

                    context("when updateMode is per frame") {
                        override(updateMode) { UpdateMode.PER_FRAME }
                        it("should set uniforms appropriately") {
                            fakeGlProgram.renders.map { it.uniforms["perFixtureData"] }
                                .shouldContainExactly(0, 0, 1, 1)
                        }
                    }

                    context("when updateMode is per fixture") {
                        override(updateMode) { UpdateMode.PER_FIXTURE }
                        it("should set uniforms appropriately") {
                            fakeGlProgram.renders.map { it.uniforms["perFixtureData"] }
                                .shouldContainExactly(0, 1, 2, 3)
                        }
                    }

                    context("when the feed is per-pixel") {
                        override(feed) { pixelFeed }

                        it("should allocate a texture to hold per-pixel data for all fixtures") {
                            (texture.width to texture.height)
                                .shouldBe(3 to 1)
                            fakeGlProgram.renders.map { it.findUniformTextureConfig("perPixelDataTexture").buffer }
                                .shouldContainExactly(
                                    listOf(10f, 20f, 21f), // First frame, fixture1.
                                    listOf(10f, 20f, 21f), // First frame, fixture2.
                                    listOf(10f, 20f, 21f), // Second frame, fixture1.
                                    listOf(10f, 20f, 21f)  // Second frame, fixture2.
                                )
                        }

                        context("when updateMode is per frame") {
                            override(updateMode) { UpdateMode.PER_FRAME }

                            it("should allocate a texture to hold per-pixel data for all fixtures") {
                                (texture.width to texture.height)
                                    .shouldBe(3 to 1)
                                fakeGlProgram.renders.map { it.findUniformTextureConfig("perPixelDataTexture").buffer }
                                    .shouldContainExactly(
                                        // 10f-41f are currently issued when the feed is first bound;
                                        // we could eliminate that overhead but probably not really worth it.
                                        listOf(30f, 40f, 41f), // First frame, fixture1.
                                        listOf(30f, 40f, 41f), // First frame, fixture2.
                                        listOf(50f, 60f, 61f), // Second frame, fixture1.
                                        listOf(50f, 60f, 61f)  // Second frame, fixture2.
                                    )
                            }
                        }

                        context("when per-pixel feed isn't provided by the fixture, and the fixtures were previously rendered to without the feed") {
                            override(fixtureType) { FixtureTypeForTest() }
                            override(initialProgram) { arrayOf<GlslProgram?>(null) }
                            override(drawTwoFrames) { {} }

                            beforeEach {
                                // This causes program to be compiled finally.
                                renderEngine.setRenderPlan(renderPlanFor(program, fixture1Target, fixture2Target))
                                renderEngine.draw()
                                renderEngine.draw()
                            }

                            it("should allocate a texture to hold per-pixel data for all fixtures") {
                                (texture.width to texture.height)
                                    .shouldBe(3 to 1)
                                fakeGlProgram.renders.map { it.findUniformTextureConfig("perPixelDataTexture").buffer }
                                    .shouldContainExactly(
                                        listOf(10f, 20f, 21f), // First frame, fixture1.
                                        listOf(10f, 20f, 21f), // First frame, fixture2.
                                        listOf(10f, 20f, 21f), // Second frame, fixture1.
                                        listOf(10f, 20f, 21f)  // Second frame, fixture2.
                                    )
                            }
                        }
                    }
                }
            }
        }

        describe("component mapping to framebuffer") {
            it("should start at startPix") {
                // ....
                // xxx.
                ComponentRenderEngine.mapFixtureComponentsToRects(4, 4, 3)
                    .shouldBe(
                        listOf(
                            Quad.Rect(1f, 0f, 2f, 3f)
                        )
                    )
            }

            it("should wrap as necessary") {
                // ...x
                // xxxx
                // xx..
                ComponentRenderEngine.mapFixtureComponentsToRects(3, 4, 7)
                    .shouldBe(listOf(
                        Quad.Rect(0f, 3f, 1f, 4f),
                        Quad.Rect(1f, 0f, 2f, 4f),
                        Quad.Rect(2f, 0f, 3f, 2f)
                    ))
            }

            it("should include multiple rows in a rect if possible") {
                // ...x
                // xxxx <-- these two rows
                // xxxx <--  are merged into one rect.
                // xx..
                ComponentRenderEngine.mapFixtureComponentsToRects(3, 4, 11)
                    .shouldBe(listOf(
                        Quad.Rect(0f, 3f, 1f, 4f),
                        Quad.Rect(1f, 0f, 3f, 4f),
                        Quad.Rect(3f, 0f, 4f, 2f)
                    ))
            }

            it("should include full first and last rows in a rect if possible") {
                // ....
                // xxxx <-- these three rows
                // xxxx <--  are merged into
                // xxxx <--   one rect.
                ComponentRenderEngine.mapFixtureComponentsToRects(4, 4, 12)
                    .shouldBe(listOf(
                        Quad.Rect(1f, 0f, 4f, 4f)
                    ))
            }
        }
    }
})

private fun testFixture(fixtureType: FixtureTypeForTest, pixelCount: Int, initial: Float = 0f) =
    Fixture(
        null, pixelCount,
        "test fixture",
        NullTransport, fixtureType,
        fixtureType.Options(pixelCount, 3, someVectors(pixelCount, initial))
            .toConfig(null, TestModel)
    )

private fun someVectors(count: Int, initial: Float = 0f): List<Vector3F> =
    (0 until count).map { Vector3F(initial + count / 10f, 0f, 0f) }

class FixtureTypeForTest(
    vararg val fixtureFeeds: Feed,
    override val resultContentType: ContentType = Color,
    override val id: String = "testDevice",
    override val title: String = id.englishize(),
    override val likelyPipelines: List<Pair<ContentType, ContentType>> =
        with(ContentType) {
            listOf(
                XyzCoordinate to UvCoordinate,
                UvCoordinate to Color
            )
        }
) : FixtureType {
    override val feedBuilders: List<FeedBuilder<*>>
        get() = fixtureFeeds.map { feed ->
            object : FeedBuilder<Feed> {
                override val title: String get() = feed.title
                override val description: String get() = "Description"
                override val resourceName: String get() = "resName$feed"
                override val contentType: ContentType get() = feed.contentType
                override val serializerRegistrar: SerializerRegistrar<Feed> get() = TODO("not implemented")
                override fun build(inputPort: InputPort): Feed = feed
            }
        }

    override var defaultOptions: FixtureOptions = Options(bytesPerComponent = 3)
    override val emptyOptions: FixtureOptions = Options(bytesPerComponent = 3)

    override val errorIndicatorShader: Shader
        get() = Shader("Ω Guru Meditation Error Ω", "")

    override fun createResultStorage(renderResults: RenderResults): ResultStorage = ResultStorage.Empty

    override fun toString(): String = id

    inner class MutableOptions(
        var componentCount: Int?,
        var bytesPerComponent: Int,
        var pixelLocations: List<Vector3F>?
    ) : MutableFixtureOptions {
        override val fixtureType: FixtureType
            get() = this@FixtureTypeForTest

        override fun build(): FixtureOptions =
            Options(componentCount, bytesPerComponent, pixelLocations)

        override fun getEditorView(editingController: EditingController<*>): View =
            TODO("not implemented")
    }

    inner class Options(
        override val componentCount: Int? = null,
        override val bytesPerComponent: Int,
        val pixelLocations: List<Vector3F>? = null
    ) : FixtureOptions {
        override val fixtureType: FixtureType
            get() = this@FixtureTypeForTest

        override fun edit(): MutableFixtureOptions =
            MutableOptions(componentCount, bytesPerComponent, pixelLocations)

        override fun plus(other: FixtureOptions?): FixtureOptions =
            if (other == null) this
            else plus(other as Options)

        operator fun plus(other: Options): Options = Options(
            other.componentCount ?: componentCount,
            other.bytesPerComponent,
            other.pixelLocations ?: pixelLocations,
        )

        override fun preview(): ConfigPreview = TODO("not implemented")

        override fun toConfig(entity: Model.Entity?, model: Model, defaultComponentCount: Int?): FixtureConfig {
            val pixelCount = componentCount ?: defaultComponentCount ?: error("Component count not specified.")
            return Config(
                pixelCount,
                bytesPerComponent,
                EnumeratedPixelLocations(pixelLocations ?: emptyList()),
                this@FixtureTypeForTest
            )
        }
    }

    data class Config(
        override val componentCount: Int,
        override val bytesPerComponent: Int,
        val pixelLocations: PixelLocations,
        override val fixtureType: FixtureType
    ) : FixtureConfig
}

fun Boolean.truify(): Boolean {
    if (this) error("already true!")
    return true
}