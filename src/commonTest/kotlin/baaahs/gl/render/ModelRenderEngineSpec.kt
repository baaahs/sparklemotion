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
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.renderPlanFor
import baaahs.gl.shader.PaintShader
import baaahs.only
import baaahs.plugin.Plugins
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.ShaderType
import baaahs.show.UpdateMode
import baaahs.show.live.LiveShaderInstance
import baaahs.show.live.link
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeKgl.Companion.contents
import com.danielgergely.kgl.*
import org.spekframework.spek2.Spek
import kotlin.test.expect

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

            it("should bind EngineFeeds for each data source") {
                expect(1) { fixtureDataSource.feeds.size }
                expect(1) { fixtureDataSource.engineFeeds.size }
                expect(0) { fixtureDataSource.programFeeds.size }
            }

            context("when the data source is per-pixel") {
                override(dataSource) { pixelDataSource }

                it("should allocate a texture to hold per-pixel data") {
                    expect(1 to 1) { texture.width to texture.height }
                    expect(List(1) { 0f }) { (texture.buffer as FloatBuffer).contents() }
                }
            }

            context("when the engine is released") {
                beforeEachTest { renderEngine.release() }

                it("should release Feeds and EngineFeeds") {
                    expect(true) { fixtureDataSource.feeds.all { it.released } }
                    expect(true) { fixtureDataSource.engineFeeds.all { it.released } }
                }

                context("when the data source is per-pixel") {
                    override(dataSource) { pixelDataSource }

                    it("should release the texture") {
                        expect(true) { texture.isDeleted }
                        expect(emptyMap()) { gl.allocatedTextureUnits }
                    }
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
            val openShader by value { GlslAnalyzer(Plugins.safe()).openShader(shaderText) as PaintShader }
            val incomingLinks by value { mapOf("gl_FragCoord" to dataSource.link("coord")) }
            val linkedPatch by value {
                val liveShaderInstance = LiveShaderInstance(openShader, incomingLinks, null, 0f)
                LinkedPatch(liveShaderInstance)
            }
            val program by value {
                renderEngine.compile(linkedPatch) { _, dataSource -> dataSource.createFixtureFeed() }
            }
            val initialProgram by value<Array<GlslProgram?>> { arrayOf(program) } // Array is a workaround for a bug in by value.
            val fakeGlProgram by value { gl.programs.only("program") }

            beforeEachTest { initialProgram.run { } }

            it("should bind EngineFeeds for each data source") {
                expect(1) { fixtureDataSource.feeds.size }
                expect(1) { fixtureDataSource.engineFeeds.size }
                expect(1) { fixtureDataSource.programFeeds.size }
            }

            it("should bind program uniforms") {
                expect(setOf("perFixtureData")) { fakeGlProgram.uniformNames() }
            }

            context("when the data source is per-pixel") {
                override(dataSource) { pixelDataSource }

                it("should allocate a texture to hold per-pixel data") {
                    expect(1 to 1) { texture.width to texture.height }
                    expect(GlContext.GL_RGB32F) { texture.internalFormat }
                    expect(GL_RED) { texture.format }
                    expect(GL_FLOAT) { texture.type }
                    expect(GL_NEAREST) { texture.params[GL_TEXTURE_MIN_FILTER] }
                    expect(GL_NEAREST) { texture.params[GL_TEXTURE_MAG_FILTER] }

                    val buffer = texture.buffer
                    println("buffer = $buffer")
//                    expect(300) { buffer. }
                }
            }

            context("when the program is released") {
                beforeEachTest { program.release() }

                it("should release Feeds and EngineFeeds") {
                    expect(false) { fixtureDataSource.feeds.all { it.released } }
                    expect(false) { fixtureDataSource.engineFeeds.all { it.released } }
                    expect(true) { fixtureDataSource.programFeeds.all { it.released } }
                }
            }

            context("when the engine is released") {
                beforeEachTest { renderEngine.release() }

                it("should release Feeds and EngineFeeds") {
                    expect(true) { fixtureDataSource.feeds.all { it.released } }
                    expect(true) { fixtureDataSource.engineFeeds.all { it.released } }
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
                        expect(listOf(0, 0, 0, 0)) { fakeGlProgram.renders.map { it.uniforms["perFixtureData"] } }
                    }

                    context("when updateMode is per frame") {
                        override(updateMode) { UpdateMode.PER_FRAME }
                        it("should set uniforms appropriately") {
                            expect(listOf(0, 0, 1, 1)) { fakeGlProgram.renders.map { it.uniforms["perFixtureData"] } }
                        }
                    }

                    context("when updateMode is per fixture") {
                        override(updateMode) { UpdateMode.PER_FIXTURE }
                        it("should set uniforms appropriately") {
                            expect(listOf(0, 1, 2, 3)) { fakeGlProgram.renders.map { it.uniforms["perFixtureData"] } }
                        }
                    }

                    context("when the data source is per-pixel") {
                        override(dataSource) { pixelDataSource }

                        it("should allocate a texture to hold per-pixel data for all fixtures") {
                            expect(3 to 1) { texture.width to texture.height }
                            expect(
                                listOf(
                                    listOf(10f, 20f, 21f), // First frame, fixture1.
                                    listOf(10f, 20f, 21f), // First frame, fixture2.
                                    listOf(10f, 20f, 21f), // Second frame, fixture1.
                                    listOf(10f, 20f, 21f)  // Second frame, fixture2.
                                )
                            ) { fakeGlProgram.renders.map { it.textureBuffers.only("texture buffer") } }
                        }

                        context("when updateMode is per frame") {
                            override(updateMode) { UpdateMode.PER_FRAME }

                            it("should allocate a texture to hold per-pixel data for all fixtures") {
                                expect(3 to 1) { texture.width to texture.height }
                                expect(
                                    listOf(
                                        // 10f-41f are currently issued when the feed is first bound;
                                        // we could eliminate that overhead but probably not really worth it.
                                        listOf(30f, 40f, 41f), // First frame, fixture1.
                                        listOf(30f, 40f, 41f), // First frame, fixture2.
                                        listOf(50f, 60f, 61f), // Second frame, fixture1.
                                        listOf(50f, 60f, 61f)  // Second frame, fixture2.
                                    )
                                ) { fakeGlProgram.renders.map { it.textureBuffers.only("texture buffer") } }
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
                                expect(3 to 1) { texture.width to texture.height }
                                expect(
                                    listOf(
                                        listOf(10f, 20f, 21f), // First frame, fixture1.
                                        listOf(10f, 20f, 21f), // First frame, fixture2.
                                        listOf(10f, 20f, 21f), // Second frame, fixture1.
                                        listOf(10f, 20f, 21f)  // Second frame, fixture2.
                                    )
                                ) { fakeGlProgram.renders.map { it.textureBuffers.only("texture buffer") } }
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
    vararg fixtureDataSources: DataSource,
    override val resultContentType: ContentType = ContentType.ColorStream,
    override val id: String = "testDevice",
    override val title: String = id.englishize()
) : DeviceType {
    override val dataSources: List<DataSource> = fixtureDataSources.toList()
    override val resultParams: List<ResultParam> get() = emptyList()
    override val errorIndicatorShader: Shader
        get() = Shader("Ω Guru Meditation Error Ω", ShaderType.Paint, "")
    override fun toString(): String = id
}

fun Boolean.truify(): Boolean {
    if (this) error("already true!")
    return true
}