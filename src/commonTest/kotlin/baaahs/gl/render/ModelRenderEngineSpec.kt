package baaahs.gl.render

import baaahs.TestModel
import baaahs.describe
import baaahs.englishize
import baaahs.fixtures.DeviceType
import baaahs.fixtures.Fixture
import baaahs.fixtures.NullTransport
import baaahs.fixtures.ResultParam
import baaahs.geom.Vector3F
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.ProgramLinker
import baaahs.gl.renderPlanFor
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.PaintShader
import baaahs.gl.testPlugins
import baaahs.only
import baaahs.plugin.SerializerRegistrar
import baaahs.show.*
import baaahs.show.Shader
import baaahs.show.live.LinkedShaderInstance
import baaahs.show.live.link
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
        val fixtureDataSource by value { PerFixtureDataSourceForTest(updateMode) }
        val pixelDataSource by value { PerPixelDataSourceForTest(updateMode) }
        val dataSource by value<DataSource> { fixtureDataSource }
        val deviceType by value { DeviceTypeForTest(dataSource) }
        val renderEngine by value { ModelRenderEngine(gl, TestModel, deviceType, minTextureWidth = 1) }
        val texture by value { gl.textures.only("texture") }

        context("when engine has just been started") {
            beforeEachTest { renderEngine.run { /* No-op. */ } }

            it("should have no feeds open yet") {
                expect(fixtureDataSource.feeds.size).toBe(0)
                expect(fixtureDataSource.engineFeeds.size).toBe(0)
                expect(fixtureDataSource.programFeeds.size).toBe(0)
            }

            context("when the engine is released") {
                beforeEachTest { renderEngine.release() }

                it("should release Feeds and EngineFeeds") {
                    expect(fixtureDataSource.feeds.all { it.released }).toBe(true)
                    expect(fixtureDataSource.engineFeeds.all { it.released }).toBe(true)
                }
            }
        }

        context("when a program is compiled") {
            val shaderText by value {
                """
                    uniform float time;
            
                    void main(void) { gl_FragColor = doesntMatter(gl_FragCoord); }
                """.trimIndent()
            }
            val openShader by value { GlslAnalyzer(testPlugins()).openShader(shaderText) as PaintShader }
            val incomingLinks by value { mapOf("gl_FragCoord" to dataSource.link("coord")) }
            val linkedPatch by value {
                val rootNode = LinkedShaderInstance(openShader, incomingLinks, null, 0f)
                ProgramLinker(rootNode).buildLinkedPatch()
            }
            val program by value {
                renderEngine.compile(linkedPatch) { id, dataSource -> dataSource.createFeed(FakeShowPlayer(), id) }
            }
            val initialProgram by value<Array<GlslProgram?>> { arrayOf(program) } // Array is a workaround for a bug in by value.
            val fakeGlProgram by value { gl.programs.only("program") }

            beforeEachTest { initialProgram.run { } }

            it("should bind EngineFeeds for each data source") {
                expect(fixtureDataSource.feeds.size).toBe(1)
                expect(fixtureDataSource.engineFeeds.size).toBe(1)
                expect(fixtureDataSource.programFeeds.size).toBe(1)
            }

            it("should bind program uniforms") {
                expect(fakeGlProgram.uniformNames()).toBe(setOf("perFixtureData"))
            }

            context("when the data source is per-pixel") {
                override(dataSource) { pixelDataSource }

                it("should allocate a texture to hold per-pixel data") {
                    expect(texture.width to texture.height).toBe(1 to 1)
                    expect(texture.internalFormat).toBe(GlContext.GL_RGB32F)
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

                it("should release Feeds and EngineFeeds") {
                    expect(fixtureDataSource.feeds.all { it.released }).toBe(false)
                    expect(fixtureDataSource.engineFeeds.all { it.released }).toBe(false)
                    expect(fixtureDataSource.programFeeds.all { it.released }).toBe(true)
                }
            }

            context("when the engine is released") {
                beforeEachTest { renderEngine.release() }

                it("should release EngineFeeds") {
                    expect(fixtureDataSource.engineFeeds.all { it.released }).toBe(true)
                }

                context("when the data source is per-pixel") {
                    override(dataSource) { pixelDataSource }

                    it("should release the texture") {
                        expect(texture.isDeleted).toBe(true)
                        expect(gl.allocatedTextureUnits).isEmpty()
                    }
                }
            }

            context("when a fixtures are added") {
                val fixture1Target by value {
                    renderEngine.addFixture(testFixture(deviceType, 1, 0f))
                }
                val fixture2Target by value {
                    renderEngine.addFixture(testFixture(deviceType, 2, 0.1f))
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
                        override(dataSource) { pixelDataSource }

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
                            override(deviceType) { DeviceTypeForTest() }
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

private fun testFixture(deviceType: DeviceTypeForTest, pixelCount: Int, initial: Float = 0f) =
    Fixture(null, pixelCount, someVectors(pixelCount, initial), deviceType, transport = NullTransport)

private fun someVectors(count: Int, initial: Float = 0f): List<Vector3F> =
    (0 until count).map { Vector3F(initial + count / 10f, 0f, 0f) }

class DeviceTypeForTest(
    vararg val fixtureDataSources: DataSource,
    override val resultContentType: ContentType = ContentType.ColorStream,
    override val id: String = "testDevice",
    override val title: String = id.englishize()
) : DeviceType {
    override val dataSourceBuilders: List<DataSourceBuilder<*>>
        get() = fixtureDataSources.map { dataSource ->
            object : DataSourceBuilder<DataSource> {
                override val resourceName: String get() = "resName$dataSource"
                override val contentType: ContentType get() = dataSource.contentType
                override val serializer: SerializerRegistrar<DataSource> get() = TODO("not implemented")
                override fun build(inputPort: InputPort): DataSource = dataSource
            }
        }

    override val resultParams: List<ResultParam> get() = emptyList()
    override val errorIndicatorShader: Shader
        get() = Shader("Ω Guru Meditation Error Ω", ShaderType.Paint, "")
    override fun toString(): String = id
}

fun Boolean.truify(): Boolean {
    if (this) error("already true!")
    return true
}