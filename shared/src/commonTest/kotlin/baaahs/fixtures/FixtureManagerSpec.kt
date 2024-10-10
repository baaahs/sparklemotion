package baaahs.fixtures

import baaahs.describe
import baaahs.fakeModel
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.FixtureTypeForTest
import baaahs.gl.render.RenderManager
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.gl.testPlugins
import baaahs.gl.testToolchain
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.model.FakeModelEntity
import baaahs.model.Model
import baaahs.only
import baaahs.scene.OpenScene
import baaahs.scene.SceneMonitor
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
        val renderManager by value { RenderManager(FakeGlContext()) }
        val renderTargets by value { linkedMapOf<Fixture, FixtureRenderTarget>() }
        val surfacePixelStrategy by value { LinearSurfacePixelStrategy(Random(1)) }

        // Maintain stable fixture order for test:
        val fixtureManager by value { FixtureManagerImpl(renderManager, testPlugins(), initialRenderTargets = renderTargets) }

        context("when fixtures of multiple types have been added") {
            val fogginess by value { ContentType("fogginess", "Fogginess", GlslType.Float) }
            val fogMachineDevice by value { FixtureTypeForTest(id = "fogMachine", resultContentType = fogginess) }

            val deafeningness by value { ContentType("deafeningness", "Deafeningness", GlslType.Float) }
            val vuzuvelaDevice by value { FixtureTypeForTest(id = "vuzuvela", resultContentType = deafeningness) }

            val fogMachineEntity1 by value { FakeModelEntity("fog1", fogMachineDevice) }
            val fogMachineEntity2 by value { FakeModelEntity("fog2", fogMachineDevice) }
            val vuzuvelaEntity1 by value { FakeModelEntity("vuzuvela1", vuzuvelaDevice) }
            val vuzuvelaEntity2 by value { FakeModelEntity("vuzuvela2", vuzuvelaDevice) }
            override(modelEntities) { listOf(fogMachineEntity1, fogMachineEntity2, vuzuvelaEntity1, vuzuvelaEntity2) }

            val fogMachine1 by value { fakeFixture(1, fogMachineEntity1, model = model) }
            val fogMachine2 by value { fakeFixture(1, fogMachineEntity2, model = model) }
            val vuzuvela1 by value { fakeFixture(1, vuzuvelaEntity1, model = model) }
            val vuzuvela2 by value { fakeFixture(1, vuzuvelaEntity2, model = model) }
            val fixtures by value { listOf(fogMachine1, fogMachine2, vuzuvela1, vuzuvela2) }
            val initialFixtures by value { fixtures }

            beforeEachTest {
                fixtureManager.fixturesChanged(initialFixtures, emptyList())
            }

            context("generating programs to cover every fixture") {
                val show by value {
                    MutableShow("Test Show") {
                        addPatch(
                            Shader("Pea Soup", "vec4 main() { return vec4(0.); }")
                        )
                        addPatch(
                            Shader("Din", "vec4 main() { return vec4(0.); }")
                        )
                    }.getShow()
                }

                val openShow by value {
                    object : ShowOpener(testToolchain, show, FakeShowPlayer(SceneMonitor(OpenScene(model)))) {
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

                val activePatchSet by value { openShow.buildActivePatchSet() }

                beforeEachTest {
                    fixtureManager.activePatchSetChanged(activePatchSet)
                    val updated = fixtureManager.maybeUpdateRenderPlans()
                    expect(updated).toBe(true)
                }

                val renderPlan by value { fixtureManager.currentRenderPlan!! }
                val fogMachinePrograms by value { renderPlan[fogMachineDevice]!!.programs }
                val vuzuvelaPrograms by value { renderPlan[vuzuvelaDevice]!!.programs }

                it("creates a RenderPlan to cover all fixture types") {
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