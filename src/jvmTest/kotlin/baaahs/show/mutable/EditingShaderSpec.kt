package baaahs.show.mutable

import baaahs.*
import baaahs.app.ui.editor.LinkOption
import baaahs.app.ui.editor.PortLinkOption
import baaahs.gl.*
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.preview.ShaderBuilder
import baaahs.gl.render.PreviewRenderEngine
import baaahs.glsl.Shaders
import baaahs.plugin.CorePlugin
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.show.Panel
import baaahs.show.Shader
import baaahs.show.mutable.EditingShader.State
import baaahs.shows.FakeGlContext
import baaahs.ui.Observer
import baaahs.ui.addObserver
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import com.danielgergely.kgl.GL_FRAGMENT_SHADER
import ext.TestCoroutineContext
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import org.spekframework.spek2.Spek
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

// Currently in jvmTest so we can use mockk.
// TODO: move back to commonTest when mockk supports multiplatform.
@Suppress("unused")
@InternalCoroutinesApi
object EditingShaderSpec : Spek({
    describe<EditingShader> {
        val plugins by value { testPlugins() + BeatLinkPlugin.Builder(StubBeatSource()) }
        val toolchain by value { RootToolchain(plugins) }
        val beatLinkDataSource by value {
            (plugins.find(BeatLinkPlugin.id) as BeatLinkPlugin).beatLinkDataSource
        }
        val scaleUniform by value { "uniform float theScale;" }
        val paintShader by value {
            Shader(
                "Paint", """
                uniform float time;
                void main(void) {
                    gl_FragColor = vec4(gl_FragCoord.x, gl_FragCoord.y, mod(time, 1.), 1.);
                }
            """.trimIndent()
            )
        }
        val filterShader by value {
            Shader(
                "Filter", """
                    $scaleUniform
                    uniform float time;
                    // @return color
                    // @param inColor color
                    vec4 main(vec4 inColor) {
                        return inColor * theScale + time * 0.;
                    }
                """.trimIndent()
            )
        }
        val shaderInEdit by value { filterShader }
        val otherShaderInPatch by value<Shader?> { paintShader }
        val otherShaderInShow by value { Shaders.red }

        val mutableShaderInstance by value { MutableShaderInstance(MutableShader(shaderInEdit)) }
        val mutablePatch by value { MutablePatch { addShaderInstance(mutableShaderInstance) } }
        val mutableShow by value { MutableShow("show") { addPatch(mutablePatch) } }
        val observerSlot by value { slot<Observer>() }
        val mockShaderBuilder by value { mockk<ShaderBuilder>() }
        val getShaderBuilder by value<(Shader) -> ShaderBuilder> { { mockShaderBuilder } }
        val button by value { mutableShow.addButton(MutablePanel(Panel("panel")), "button") {} }
        val otherPatchInShow by value { MutablePatch { addShaderInstance(otherShaderInShow) } }

        beforeEachTest {
            otherShaderInPatch?.let { mutablePatch.addShaderInstance(it) }
            button.addPatch(otherPatchInShow)
            every { mockShaderBuilder.addObserver(capture(observerSlot)) } answers { observerSlot.captured }
            every { mockShaderBuilder.startBuilding() } just runs
            every { mockShaderBuilder.gadgets } returns emptyList()
            every { mockShaderBuilder.openShader } returns toolchain.openShader(shaderInEdit)
        }
        val notifiedStates by value { arrayListOf<State>() }

        val beforeBuildingShader by value { { } }
        val editingShader by value {
            beforeBuildingShader()
            EditingShader(mutableShow, mutablePatch, mutableShaderInstance, toolchain, getShaderBuilder)
                .also { it.addObserver { notifiedStates.add(it.state) } }
        }
        beforeEachTest {
            editingShader.let {} // Make sure it's warmed up.
        }

        context("at initialization") {
            it("is in Building state") {
                expect(editingShader.state).toBe(State.Building)
            }

            it("starts building the shader") {
                verify { mockShaderBuilder.startBuilding() }
            }

            it("has no gadgets") {
                expect(editingShader.gadgets).isEmpty()
            }

            it("should not have notified observers yet") {
                expect(notifiedStates).isEmpty()
            }

            it("has no incoming links") {
                expectEmptyMap { mutableShaderInstance.incomingLinks }
            }

            context("if shader builder notifies us") {
                val builderState by value { ShaderBuilder.State.Success }

                beforeEachTest {
                    every { mockShaderBuilder.state } returns builderState
                    observerSlot.captured.notifyChanged()
                }

                context("of a successful build") {
                    it("should notify our observers") {
                        expect(notifiedStates).containsExactly(State.Success)
                    }
                }

                context("that it's still building") {
                    override(builderState) { ShaderBuilder.State.Compiling }
                    it("should not notify our observers") {
                        expect(notifiedStates).isEmpty()
                    }
                }

                context("of a failure to build") {
                    override(builderState) { ShaderBuilder.State.Errors }
                    it("should not notify our observers again") {
                        expect(notifiedStates).containsExactly(State.Errors)
                    }

                    it("should still return ShaderInstanceOptions") {
                        expect(editingShader.getShaderInstanceOptions()).notToBeNull()
                    }
                }
            }
        }

        context("when a shader has successfully compiled") {
            beforeEachTest {
                every { mockShaderBuilder.state } returns ShaderBuilder.State.Success
                observerSlot.captured.notifyChanged()
            }

            context("when the shader instance had no previous incoming links") {
                it("should set some reasonable defaults") {
                    expect(mutableShaderInstance.incomingLinks.mapValues { (_, port) -> port.title })
                        .toBe(mapOf(
                            "inColor" to "Main Channel",
                            "theScale" to "The Scale Slider",
                            "time" to "Time"
                        ))
                }

                it("should create an appropriate data source") {
                    expect(mutableShaderInstance.incomingLinks["theScale"])
                        .toBe(MutableDataSourcePort(CorePlugin.SliderDataSource("The Scale", 1f, 0f, 1f)))
                }

                context("when hints are provided") {
                    override(scaleUniform) { "uniform float theScale; // @@Slider min=.25 max=4 default=1" }

                    it("should create an appropriate data source") {
                        expect(mutableShaderInstance.incomingLinks["theScale"])
                            .toBe(MutableDataSourcePort(CorePlugin.SliderDataSource("The Scale", 1f, .25f, 4f)))
                    }
                }

                context("when a plugin reference is provided") {
                    override(scaleUniform) { "uniform float theScale; // @@baaahs.BeatLink:BeatLink" }

                    it("should create an appropriate data source") {
                        expect(mutableShaderInstance.incomingLinks["theScale"])
                            .toBe(MutableDataSourcePort(beatLinkDataSource))
                    }
                }

                context("when a content type is provided") {
                    override(scaleUniform) { "uniform float theScale; // @type beat-link" }

                    it("should create an appropriate data source") {
                        expect(mutableShaderInstance.incomingLinks["theScale"])
                            .toBe(MutableDataSourcePort(beatLinkDataSource))
                    }
                }
            }

            context("when the shader instance had a previous incoming link") {
                override(beforeBuildingShader) {
                    {
                        mutableShaderInstance.incomingLinks["theScale"] =
                            MutableDataSourcePort(CorePlugin.SliderDataSource("Custom slider", 1f, 0f, 1f))
                    }
                }

                it("shouldn't change it") {
                    expect(mutableShaderInstance.incomingLinks["theScale"]!!.title)
                        .toBe("Custom slider Slider")
                }

                it("should be listed in link options") {
                    expect(editingShader.linkOptionsFor("theScale").stringify()).toBe("""
                            Channel:
                            - Main Channel
                            Data Source:
                            - BeatLink
                            - Custom slider Slider
                            * The Scale Slider
                            - Time
                        """.trimIndent())
                }
            }

            context("when a link has been selected by a human") {
                beforeEachTest {
                    mutableShaderInstance.incomingLinks["theScale"] =
                        MutableDataSourcePort(CorePlugin.SliderDataSource("custom slider", 1f, 0f, 1f))

                    editingShader.changeInputPortLink(
                        "theScale",
                        PortLinkOption(
                            MutableDataSourcePort(CorePlugin.SliderDataSource("custom slider", 1f, 0f, 1f))
                        )
                    )

                    // Rebuild.
                    observerSlot.captured.notifyChanged()
                }

                it("shouldn't modify it") {
                    expect(mutableShaderInstance.incomingLinks["theScale"]!!.title)
                        .toBe("custom slider Slider")
                }

                // TODO: ... unless its type doesn't make any sense now.
            }

            context("incoming link suggestions") {
                it("suggests link options for each input port") {
                    expect(editingShader.openShader!!.inputPorts.map { it.id }.toSet())
                        .toBe(setOf("theScale", "time", "inColor"))
                }

                it("suggests reasonable link options for scale") {
                    expect(editingShader.linkOptionsFor("theScale").stringify())
                        .toBe("""
                            Channel:
                            - Main Channel
                            Data Source:
                            - BeatLink
                            * The Scale Slider
                            - Time
                        """.trimIndent())
                }

                it("suggests reasonable link options for time") {
                    expect(editingShader.linkOptionsFor("time").stringify())
                        .toBe("""
                            Channel:
                            - Main Channel
                            Data Source:
                            - BeatLink
                            * Time
                            - Time Slider
                        """.trimIndent())
                }

                it("suggests reasonable link options for input color") {
                    // Should never include ourself.
                    expect(editingShader.linkOptionsFor("inColor").stringify())
                        .toBe("""
                            Channel:
                            * Main Channel
                            Data Source:
                            - In Color ColorPicker
                        """.trimIndent())
                }

                context("when another patch has shader on a different shader channel") {
                    override(beforeBuildingShader) {
                        { otherPatchInShow.mutableShaderInstances.only().shaderChannel = MutableShaderChannel("other") }
                    }

                    context("and its result type matches this input's type") {
                        it("should include that shader channel as an option") {
                            // Should never include ourself.
                            expect(editingShader.linkOptionsFor("inColor").stringify())
                                .toBe("""
                                    Channel:
                                    * Main Channel
                                    - Other Channel
                                    Data Source:
                                    - In Color ColorPicker
                                """.trimIndent())
                        }
                    }

                    context("and its result type doesn't match this input's type") {
                        override(otherShaderInShow) { Shaders.flipY } // distortion

                        it("shouldn't include that shader channel as an option") {
                            expect(editingShader.linkOptionsFor("inColor").stringify())
                                .toBe("""
                                    Channel:
                                    * Main Channel
                                    Data Source:
                                    - In Color ColorPicker
                                """.trimIndent())
                        }
                    }
                }
            }
        }

        context("preview") {
            val context by value { TestCoroutineContext("test") }
            override(shaderInEdit) { paintShader }
            override(otherShaderInPatch) { null }
            override(getShaderBuilder) {
                { shader: Shader -> PreviewShaderBuilder(shader, toolchain, TestModel, CoroutineScope(context)) }
            }
            val gl by value { FakeGlContext() }
            val renderEngine by value { PreviewRenderEngine(gl, 1, 1) }

            beforeEachTest {
                // Run through the shader building steps.
                expect(editingShader.shaderBuilder.state).toBe(ShaderBuilder.State.Analyzing)
                expect(editingShader.state).toBe(State.Building)
                context.runAll()

                expect(editingShader.shaderBuilder.state).toBe(ShaderBuilder.State.Linked)
                expect(editingShader.state).toBe(State.Building)
                editingShader.shaderBuilder.startCompile(renderEngine)

                expect(editingShader.state).toBe(State.Building)
                context.runAll()
                expect(editingShader.state).toBe(State.Success)
            }

            it("generates valid GLSL") {
                val fakeProgram = gl.programs[0]
                val fragShader = fakeProgram.shaders[GL_FRAGMENT_SHADER]?.src
                kexpect(fragShader).toBe("""
                    #version 1234

                    #ifdef GL_ES
                    precision mediump float;
                    #endif

                    // SparkleMotion-generated GLSL

                    layout(location = 0) out vec4 sm_result;

                    // Data source: PreviewResolution
                    uniform vec2 in_previewResolution;

                    // Data source: Time
                    uniform float in_time;

                    // Shader: Screen Coords; namespace: p0
                    // Screen Coords

                    vec2 p0_screenCoordsi_result = vec2(0.);

                    #line 4
                    vec2 p0_screenCoords_main(
                        vec4 fragCoords 
                    ) {
                      return fragCoords.xy / in_previewResolution;
                    }

                    // Shader: Paint; namespace: p1
                    // Paint

                    vec4 p1_paint_gl_FragColor = vec4(0., 0., 0., 1.);

                    #line 2
                    void p1_paint_main(void) {
                        p1_paint_gl_FragColor = vec4(p0_screenCoordsi_result.x, p0_screenCoordsi_result.y, mod(in_time, 1.), 1.);
                    }


                    #line 10001
                    void main() {
                        // Invoke Screen Coords
                        p0_screenCoordsi_result = p0_screenCoords_main(gl_FragCoord);

                        // Invoke Paint
                        p1_paint_main();

                        sm_result = p1_paint_gl_FragColor;
                    }


                """.trimIndent())
            }
        }
    }
})

fun List<LinkOption>?.stringify(): String {
    if (this == null) return "no options!"

    val lines = arrayListOf<String>()
    var groupName: String? = null
    sortedWith(
        compareBy<LinkOption> { it.groupName }.thenBy { it.title }
    ).forEach { linkOption ->
        if (linkOption.groupName != groupName) {
            groupName = linkOption.groupName
            groupName?.let { lines.add(it) }
        }
        val selected = if (linkOption == first()) "*" else "-"
        lines.add("$selected ${linkOption.title}")
    }
    return lines.joinToString("\n")
}