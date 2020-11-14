package baaahs.fixtures

import baaahs.*
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.render.DeviceTypeForTest
import baaahs.gl.render.RenderManager
import baaahs.gl.render.RenderTarget
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.gl.testPlugins
import baaahs.model.Model
import baaahs.shaders.fakeFixture
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.show.live.ShowOpener
import baaahs.show.mutable.MutableShow
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import org.spekframework.spek2.Spek
import kotlin.test.expect

object FixtureManagerSpec : Spek({
    describe<FixtureManager> {
        val modelEntities by value { emptyList<Model.Entity>() }
        val model by value { fakeModel(modelEntities) }
        val renderManager by value { RenderManager(model) { FakeGlContext() } }
        val renderTargets by value { linkedMapOf<Fixture, RenderTarget>() }
        val fixtureManager by value { FixtureManager(renderManager, renderTargets) } // Maintain stable fixture order for test.

        context("when fixtures of multiple types have been added") {
            val fogginess by value { ContentType("Fogginess", GlslType.Float) }
            val fogMachineDevice by value { DeviceTypeForTest(id = "fogMachine", resultContentType = fogginess) }

            val deafeningness by value { ContentType("Deafeningness", GlslType.Float) }
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

            context("generating programs to cover every fixture") {
                val show by value {
                    MutableShow("Test Show") {
                        addPatch {
                            addShaderInstance(
                                Shader(
                                    "Pea Soup", ShaderType.Paint, """
                                    vec4 main() { return vec4(0.); }
                                """.trimIndent()
                                )
                            )
                            addShaderInstance(
                                Shader(
                                    "Din", ShaderType.Paint, """
                                    vec4 main() { return vec4(0.); }
                                """.trimIndent()
                                )
                            )
                        }
                    }.getShow()
                }

                val openShow by value {
                    val glslAnalyzer = GlslAnalyzer(testPlugins())
                    object : ShowOpener(glslAnalyzer, show, FakeShowPlayer(model)) {
                        override fun openShader(shader: Shader): OpenShader {
                            val contentType = when (shader.title) {
                                "Pea Soup" -> fogginess
                                "Din" -> deafeningness
                                else -> error("unknown shader")
                            }
                            return FakeOpenShader(OutputPort(contentType), shader.title)
                        }
                    }.openShow()
                }

                val activePatchSet by value { openShow.activePatchSet() }

                beforeEachTest {
                    fixtureManager.activePatchSetChanged(activePatchSet)
                    val updated = fixtureManager.maybeUpdateRenderPlans { _, _ -> null }
                    expect(true) { updated }
                }

                val renderPlan by value { fixtureManager.currentRenderPlan!! }
                val fogMachinePrograms by value { renderPlan[fogMachineDevice]!!.programs }
                val vuzuvelaPrograms by value { renderPlan[vuzuvelaDevice]!!.programs }

                it("creates a RenderPlan to cover all device types") {
                    val fogMachineProgram = fogMachinePrograms.only("program")
                    val vuzuvelaProgram = vuzuvelaPrograms.only("program")

                    expect(listOf(fogMachine1, fogMachine2)) { fogMachineProgram.renderTargets.map { it.fixture } }
                    expect(listOf(vuzuvela1, vuzuvela2)) { vuzuvelaProgram.renderTargets.map { it.fixture } }

                    expect("Pea Soup") { fogMachineProgram.program!!.title }
                    expect("Din") { vuzuvelaProgram.program!!.title }
                }

                context("when more fixtures are added") {
                    override(initialFixtures) { listOf(fogMachine1) }

                    beforeEachTest {
                        expect(setOf(fogMachineDevice)) { renderPlan.keys }

                        fixtureManager.fixturesChanged(listOf(vuzuvela1), emptyList())
                        val updated = fixtureManager.maybeUpdateRenderPlans { _, _ -> null }
                        expect(true) { updated }
                    }

                    it("updates the RenderPlan to include the new fixture") {
                        val newRenderPlan = fixtureManager.currentRenderPlan!!
                        expect(setOf(fogMachineDevice, vuzuvelaDevice)) { newRenderPlan.keys }
                    }
                }
            }
        }
    }
})

class FakeOpenShader(
    override val outputPort: OutputPort,
    override val title: String
) : OpenShader, RefCounted by RefCounter() {
    override val shader: Shader
        get() = Shader(title, ShaderType.Paint /* not necessarily true */, "fake src for $title")
    override val glslCode: GlslCode
        get() = GlslCode(shader.src, emptyList())
    override val entryPointName: String
        get() = "entryPoint"
    override val inputPorts: List<InputPort>
        get() = emptyList()
    override val defaultPriority: Int
        get() = TODO("not implemented")
    override val defaultUpstreams: Map<ContentType, ShaderChannel>
        get() = TODO("not implemented")

    override fun findInputPort(portId: String): InputPort {
        TODO("not implemented")
    }

    override fun toGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String {
        return "glslFor($title);\n"
    }

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String {
        return "invocationGlslFor($title);\n"
    }
}