package baaahs.fixtures

import baaahs.*
import baaahs.geom.Vector3F
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.render.DeviceTypeForTest
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.RenderManager
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.gl.testToolchain
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.mapper.MappingResults
import baaahs.model.Model
import baaahs.shaders.fakeFixture
import baaahs.show.Shader
import baaahs.show.live.FakeOpenShader
import baaahs.show.live.ShowOpener
import baaahs.show.mutable.MutableShow
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import kotlin.random.Random

object FixtureManagerSpec : Spek({
    describe<FixtureManager> {
        val modelEntities by value { emptyList<Model.Entity>() }
        val model by value { fakeModel(modelEntities) }
        val renderManager by value { RenderManager(model) { FakeGlContext() } }
        val renderTargets by value { linkedMapOf<Fixture, FixtureRenderTarget>() }
        val resultsByBrainId by value { mutableMapOf<BrainId, MappingResults.Info>() }
        val resultsBySurfaceName by value { mutableMapOf<String, MappingResults.Info>() }
        val mappingResults by value { FakeMappingResults(resultsByBrainId, resultsBySurfaceName) }
        val surfacePixelStrategy by value { LinearSurfacePixelStrategy(Random(1)) }

        // Maintain stable fixture order for test:
        val fixtureManager by value { FixtureManager(renderManager, model, mappingResults, surfacePixelStrategy, renderTargets) }

        context("when fixtures of multiple types have been added") {
            val fogginess by value { ContentType("fogginess", "Fogginess", GlslType.Float) }
            val fogMachineDevice by value { DeviceTypeForTest(id = "fogMachine", resultContentType = fogginess) }

            val deafeningness by value { ContentType("deafeningness", "Deafeningness", GlslType.Float) }
            val vuzuvelaDevice by value { DeviceTypeForTest(id = "vuzuvela", resultContentType = deafeningness) }

            val fogMachine1 by value { fakeFixture(1, FakeModelEntity("fog1", fogMachineDevice)) }
            val fogMachine2 by value { fakeFixture(1, FakeModelEntity("fog2", fogMachineDevice)) }
            val vuzuvela1 by value { fakeFixture(1, FakeModelEntity("vuzuvela1", vuzuvelaDevice)) }
            val vuzuvela2 by value { fakeFixture(1, FakeModelEntity("vuzuvela2", vuzuvelaDevice)) }
            val fixtures by value { listOf(fogMachine1, fogMachine2, vuzuvela1, vuzuvela2) }
            override(modelEntities) { fixtures.map { it.modelEntity } }
            val initialFixtures by value { fixtures }

            beforeEachTest {
                fixtureManager.fixturesChanged(initialFixtures, emptyList())
            }

            context("#createFixtureFor") {
                val brainId by value { "brain1" }
                val msgSurfaceName by value<String?> { null }
                val surface by value { TestModelSurface("surface1", 2, vertices = listOf(
                    Vector3F(1f, 1f, 1f),
                    Vector3F(2f, 2f, 1f),
                    Vector3F(1f, 2f, 2f),
                    Vector3F(2f, 1f, 2f)
                )) }
                val pixelLocations by value<List<Vector3F?>?> { null }
                val mappingInfo by value { MappingResults.Info(surface, pixelLocations) }
                val controllerId by value { BrainId(brainId).asControllerId() }

                val subject by value { fixtureManager.createFixtureFor(controllerId, msgSurfaceName, NullTransport) }

                context("when the brain id is mapped to a model element") {
                    override(resultsByBrainId) { mapOf(BrainId(brainId) to mappingInfo) }

                    it("should create a fixture") {
                        expect(subject.modelEntity).toBe(surface)
                        expect(subject.pixelCount).toBe(2)

                        // LinearSurfacePixelStrategy(Random(1))
                        expect(subject.pixelLocations).toBe(LinearSurfacePixelStrategy(Random(1)).forKnownSurface(2, surface, TestModel))
                    }
                }

                context("when the surface name is specified") {
                    override(msgSurfaceName) { "surface1" }
                    override(resultsBySurfaceName) { mapOf(msgSurfaceName to mappingInfo) }

                    it("should create a fixture") {
                        expect(subject.modelEntity).toBe(surface)
                        expect(subject.pixelCount).toBe(2)
                        expect(subject.pixelLocations).toBe(LinearSurfacePixelStrategy(Random(1)).forKnownSurface(2, surface, TestModel))
                    }
                }
            }

            context("generating programs to cover every fixture") {
                val show by value {
                    MutableShow("Test Show") {
                        addPatch {
                            addShaderInstance(
                                Shader(
                                    "Pea Soup",
                                    """
                                        vec4 main() { return vec4(0.); }
                                    """.trimIndent()
                                )
                            )
                            addShaderInstance(
                                Shader(
                                    "Din", """
                                        vec4 main() { return vec4(0.); }
                                    """.trimIndent()
                                )
                            )
                        }
                    }.getShow()
                }

                val openShow by value {
                    object : ShowOpener(testToolchain, show, FakeShowPlayer(model)) {
                        override fun openShader(shader: Shader): OpenShader {
                            val contentType = when (shader.title) {
                                "Pea Soup" -> fogginess
                                "Din" -> deafeningness
                                else -> error("unknown shader")
                            }
                            return FakeOpenShader(emptyList(), OutputPort(contentType), shader.title)
                        }
                    }.openShow()
                }

                val activePatchSet by value { openShow.activePatchSet() }

                beforeEachTest {
                    fixtureManager.activePatchSetChanged(activePatchSet)
                    val updated = fixtureManager.maybeUpdateRenderPlans()
                    expect(updated).toBe(true)
                }

                val renderPlan by value { fixtureManager.currentRenderPlan!! }
                val fogMachinePrograms by value { renderPlan[fogMachineDevice]!!.programs }
                val vuzuvelaPrograms by value { renderPlan[vuzuvelaDevice]!!.programs }

                it("creates a RenderPlan to cover all device types") {
                    val fogMachineProgram = fogMachinePrograms.only("program")
                    val vuzuvelaProgram = vuzuvelaPrograms.only("program")

                    expect(fogMachineProgram.renderTargets.map { it.fixture })
                        .containsExactly(fogMachine1,fogMachine2)
                    expect(vuzuvelaProgram.renderTargets.map { it.fixture })
                        .containsExactly(vuzuvela1,vuzuvela2)

                    expect(fogMachineProgram.program!!.title).toBe("Pea Soup")
                    expect(vuzuvelaProgram.program!!.title).toBe("Din")
                }

                context("when more fixtures are added") {
                    override(initialFixtures) { listOf(fogMachine1) }

                    beforeEachTest {
                        expect(renderPlan.keys).toBe(setOf(fogMachineDevice))

                        fixtureManager.fixturesChanged(listOf(vuzuvela1), emptyList())
                        val updated = fixtureManager.maybeUpdateRenderPlans()
                        expect(updated).toBe(true)
                    }

                    it("updates the RenderPlan to include the new fixture") {
                        val newRenderPlan = fixtureManager.currentRenderPlan!!
                        expect(newRenderPlan.keys).toBe(setOf(fogMachineDevice, vuzuvelaDevice))
                    }
                }
            }
        }
    }
})
