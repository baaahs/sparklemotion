package baaahs.gl.render

import baaahs.*
import baaahs.device.FixtureType
import baaahs.fixtures.*
import baaahs.geom.Vector3F
import baaahs.gl.*
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.ContentType.Companion.Color
import baaahs.gl.patch.ProgramLinker
import baaahs.gl.result.ResultStorage
import baaahs.gl.shader.InputPort
import baaahs.model.Model
import baaahs.plugin.SerializerRegistrar
import baaahs.scene.MutableFixtureConfig
import baaahs.show.*
import baaahs.show.Shader
import baaahs.show.live.LinkedPatch
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import com.danielgergely.kgl.*
import org.spekframework.spek2.Spek

@Suppress("unused")
object ModelRenderEngineSpec : Spek({
    describe<ModelRenderEngine> {
        val gl by value { FakeGlContext() }
        val updateMode by value { UpdateMode.ONCE }
        val fixtureFeed by value { PerFixtureFeedForTest(updateMode) }
        val pixelDataSource by value { PerPixelFeedForTest(updateMode) }
        val feed by value<Feed> { fixtureFeed }
        val fixtureType by value { FixtureTypeForTest(feed) }
        val maxFramebufferWidth by value { 64 }
        val renderEngine by value {
            ModelRenderEngine(gl, fixtureType, minTextureWidth = 1, maxFramebufferWidth)
        }
        val texture by value { gl.textures.only("texture") }

        context("when engine has just been started") {
            beforeEachTest { renderEngine.run { /* No-op. */ } }

            it("should have no feeds open yet") {
                expect(fixtureFeed.feeds.size).toBe(0)
                expect(fixtureFeed.engineFeeds.size).toBe(0)
                expect(fixtureFeed.programFeeds.size).toBe(0)
            }

            context("when the engine is released") {
                beforeEachTest { renderEngine.release() }

                it("should release FeedContexts and EngineFeedContexts") {
                    expect(fixtureFeed.feeds.all { it.released }).toBe(true)
                    expect(fixtureFeed.engineFeeds.all { it.released }).toBe(true)
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
                renderEngine.compile(linkedPatch) { id, dataSource -> dataSource.open(FakeShowPlayer(), id) }
            }
            val initialProgram by value<Array<GlslProgram?>> { arrayOf(program) } // Array is a workaround for a bug in by value.
            val fakeGlProgram by value { gl.programs.only("program") }

            beforeEachTest { initialProgram.run { } }

            it("should bind EngineFeedContexts for each data source") {
                expect(fixtureFeed.feeds.size).toBe(1)
                expect(fixtureFeed.engineFeeds.size).toBe(1)
                expect(fixtureFeed.programFeeds.size).toBe(1)
            }

            it("should bind program uniforms") {
                expect(fakeGlProgram.uniformNames()).toBe(setOf("perFixtureData"))
            }

            context("when the data source is per-pixel") {
                override(feed) { pixelDataSource }

                it("should allocate a texture to hold per-pixel data") {
                    expect(texture.width to texture.height).toBe(1 to 1)
                    expect(texture.internalFormat).toBe(GlContext.GL_R32F)
                    expect(texture.format).toBe(GL_RED)
                    expect(texture.type).toBe(GL_FLOAT)
                    expect(texture.params[GL_TEXTURE_MIN_FILTER]).toBe(GL_NEAREST)
                    expect(texture.params[GL_TEXTURE_MAG_FILTER]).toBe(GL_NEAREST)

                    val buffer = texture.buffer
                    println("buffer = $buffer")
//                    expect(300) { buffer. }
                }
            }

            context("when the program is released") {
                beforeEachTest { program.release() }

                it("should release FeedContexts and EngineFeedContexts") {
                    expect(fixtureFeed.feeds.all { it.released }).toBe(false)
                    expect(fixtureFeed.engineFeeds.all { it.released }).toBe(false)
                    expect(fixtureFeed.programFeeds.all { it.released }).toBe(true)
                }
            }

            context("when the engine is released") {
                beforeEachTest { renderEngine.release() }

                it("should release EngineFeedContexts") {
                    expect(fixtureFeed.engineFeeds.all { it.released }).toBe(true)
                }

                context("when the data source is per-pixel") {
                    override(feed) { pixelDataSource }

                    it("should release the texture") {
                        expect(texture.isDeleted).toBe(true)
                        expect(gl.allocatedTextureUnits).isEmpty()
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

                beforeEachTest { addFixture() }

                context("when two frames are rendered") {
                    beforeEachTest {
                        // Two frames so we can observe when uniforms are updated.
                        drawTwoFrames()
                    }

                    it("should generate a RenderTarget with appropriate rects") {
                        expect(fixture1Target.component0Index).toEqual(0)
                        expect(fixture1Target.componentCount).toEqual(maxFramebufferWidth)

                        expect(fixture1Target.rect0Index).toEqual(0)
                        expect(fixture1Target.rects).toEqual(listOf(
                            Quad.Rect(0f, 0f, 1f, 64f)
                        ))
                    }

                    context("when the data source is per-pixel") {
                        override(feed) { pixelDataSource }

                        it("should allocate a texture to hold per-pixel data for all fixtures") {
                            expect(texture.width to texture.height)
                                .toBe(64 to 2)
                            expect(fakeGlProgram.renders.map { it.textureBuffers.only("texture buffer") })
                                .containsExactly(
                                    listOf(10f, 11f, 12f, 13f, 14f, 15f, 16f, 17f, 18f, 19f, 20f, 21f, 22f, 23f, 24f, 25f, 26f, 27f, 28f, 29f, 30f, 31f, 32f, 33f, 34f, 35f, 36f, 37f, 38f, 39f, 40f, 41f, 42f, 43f, 44f, 45f, 46f, 47f, 48f, 49f, 50f, 51f, 52f, 53f, 54f, 55f, 56f, 57f, 58f, 59f, 60f, 61f, 62f, 63f, 64f, 65f, 66f, 67f, 68f, 69f, 70f, 71f, 72f, 73f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
                                    listOf(10f, 11f, 12f, 13f, 14f, 15f, 16f, 17f, 18f, 19f, 20f, 21f, 22f, 23f, 24f, 25f, 26f, 27f, 28f, 29f, 30f, 31f, 32f, 33f, 34f, 35f, 36f, 37f, 38f, 39f, 40f, 41f, 42f, 43f, 44f, 45f, 46f, 47f, 48f, 49f, 50f, 51f, 52f, 53f, 54f, 55f, 56f, 57f, 58f, 59f, 60f, 61f, 62f, 63f, 64f, 65f, 66f, 67f, 68f, 69f, 70f, 71f, 72f, 73f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
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

                beforeEachTest { addTwoFixtures() }

                context("when two frames are rendered") {
                    beforeEachTest {
                        // Two frames so we can observe when uniforms are updated.
                        drawTwoFrames()
                    }

                    it("should set uniforms appropriately") {
                        expect(fakeGlProgram.renders.map { it.uniforms["perFixtureData"] })
                            .containsExactly(0,0,0,0)
                    }

                    context("when updateMode is per frame") {
                        override(updateMode) { UpdateMode.PER_FRAME }
                        it("should set uniforms appropriately") {
                            expect(fakeGlProgram.renders.map { it.uniforms["perFixtureData"] })
                                .containsExactly(0,0,1,1)
                        }
                    }

                    context("when updateMode is per fixture") {
                        override(updateMode) { UpdateMode.PER_FIXTURE }
                        it("should set uniforms appropriately") {
                            expect(fakeGlProgram.renders.map { it.uniforms["perFixtureData"] })
                                .containsExactly(0,1,2,3)
                        }
                    }

                    context("when the data source is per-pixel") {
                        override(feed) { pixelDataSource }

                        it("should allocate a texture to hold per-pixel data for all fixtures") {
                            expect(texture.width to texture.height)
                                .toBe(3 to 1)
                            expect(fakeGlProgram.renders.map { it.textureBuffers.only("texture buffer") })
                                .containsExactly(
                                    listOf(10f, 20f, 21f), // First frame, fixture1.
                                    listOf(10f, 20f, 21f), // First frame, fixture2.
                                    listOf(10f, 20f, 21f), // Second frame, fixture1.
                                    listOf(10f, 20f, 21f)  // Second frame, fixture2.
                                )
                        }

                        context("when updateMode is per frame") {
                            override(updateMode) { UpdateMode.PER_FRAME }

                            it("should allocate a texture to hold per-pixel data for all fixtures") {
                                expect(texture.width to texture.height)
                                    .toBe(3 to 1)
                                expect(fakeGlProgram.renders.map { it.textureBuffers.only("texture buffer") })
                                    .containsExactly(
                                        // 10f-41f are currently issued when the feed is first bound;
                                        // we could eliminate that overhead but probably not really worth it.
                                        listOf(30f, 40f, 41f), // First frame, fixture1.
                                        listOf(30f, 40f, 41f), // First frame, fixture2.
                                        listOf(50f, 60f, 61f), // Second frame, fixture1.
                                        listOf(50f, 60f, 61f)  // Second frame, fixture2.
                                    )
                            }
                        }

                        context("when per-pixel data source isn't provided by the fixture, and the fixtures were previously rendered to without the data source") {
                            override(fixtureType) { FixtureTypeForTest() }
                            override(initialProgram) { arrayOf<GlslProgram?>(null) }
                            override(drawTwoFrames) { {} }

                            beforeEachTest {
                                // This causes program to be compiled finally.
                                renderEngine.setRenderPlan(renderPlanFor(program, fixture1Target, fixture2Target))
                                renderEngine.draw()
                                renderEngine.draw()
                            }

                            it("should allocate a texture to hold per-pixel data for all fixtures") {
                                expect(texture.width to texture.height)
                                    .toBe(3 to 1)
                                expect(fakeGlProgram.renders.map { it.textureBuffers.only("texture buffer") })
                                    .containsExactly(
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
    }
})

private fun testFixture(fixtureType: FixtureTypeForTest, pixelCount: Int, initial: Float = 0f) =
    fixtureType.createFixture(
        null, pixelCount,
        fixtureType.Config(pixelLocations = someVectors(pixelCount, initial), bytesPerComponent = 3),
        "test fixture", NullTransport, TestModel
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
        get() = fixtureFeeds.map { dataSource ->
            object : FeedBuilder<Feed> {
                override val title: String get() = dataSource.title
                override val description: String get() = "Description"
                override val resourceName: String get() = "resName$dataSource"
                override val contentType: ContentType get() = dataSource.contentType
                override val serializerRegistrar: SerializerRegistrar<Feed> get() = TODO("not implemented")
                override fun build(inputPort: InputPort): Feed = dataSource
            }
        }

    override var defaultConfig: FixtureConfig = Config(bytesPerComponent = 3)
    override val emptyConfig: FixtureConfig = Config(bytesPerComponent = 3)

    override val errorIndicatorShader: Shader
        get() = Shader("Ω Guru Meditation Error Ω", "")

    override fun createResultStorage(renderResults: RenderResults): ResultStorage = ResultStorage.Empty
    override fun createFixture(
        modelEntity: Model.Entity?,
        componentCount: Int,
        fixtureConfig: FixtureConfig,
        name: String,
        transport: Transport,
        model: Model
    ): Fixture = DtftFixture(
        modelEntity, componentCount, name, transport,
        (fixtureConfig as Config).pixelLocations ?: emptyList()
    )

    override fun toString(): String = id

    inner class Config(
        override val componentCount: Int? = null,
        override val bytesPerComponent: Int,
        val pixelLocations: List<Vector3F>? = null
    ) : FixtureConfig {
        override val fixtureType: FixtureType
            get() = this@FixtureTypeForTest

        override fun edit(): MutableFixtureConfig = TODO("not implemented")

        override fun plus(other: FixtureConfig?): FixtureConfig =
            if (other == null) this
            else plus(other as Config)

        operator fun plus(other: Config): Config = Config(
            other.componentCount ?: componentCount,
            other.bytesPerComponent ?: bytesPerComponent,
            other.pixelLocations ?: pixelLocations
        )

        override fun preview(): ConfigPreview = TODO("not implemented")
    }

    inner class DtftFixture(
        modelEntity: Model.Entity?,
        pixelCount: Int,
        name: String,
        transport: Transport,
        val pixelLocations: List<Vector3F>
    ) : Fixture(modelEntity, pixelCount, name, transport) {
        override val fixtureType: FixtureType
            get() = this@FixtureTypeForTest
        override val remoteConfig: RemoteConfig
            get() = TODO("not implemented")
    }
}

fun Boolean.truify(): Boolean {
    if (this) error("already true!")
    return true
}