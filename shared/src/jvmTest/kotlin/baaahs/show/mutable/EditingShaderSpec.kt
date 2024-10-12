package baaahs.show.mutable

import baaahs.TestModel
import baaahs.app.ui.editor.LinkOption
import baaahs.app.ui.editor.PortLinkOption
import baaahs.describe
import baaahs.expectEmptyMap
import baaahs.gl.RootToolchain
import baaahs.gl.kexpect
import baaahs.gl.openShader
import baaahs.gl.override
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.preview.ShaderBuilder
import baaahs.gl.render.PreviewRenderEngine
import baaahs.glsl.Shaders
import baaahs.plugin.Plugins
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.beatlink.FakeBeatSource
import baaahs.plugin.core.feed.SliderFeed
import baaahs.scene.OpenScene
import baaahs.scene.SceneMonitor
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
import ext.kotlinx_coroutines_test.TestCoroutineDispatcher
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
        val plugins by value {
            Plugins.buildForClient(Plugins.Companion.dummyContext, listOf(BeatLinkPlugin.forTest(FakeBeatSource())))
        }
        val toolchain by value { RootToolchain(plugins) }
        val beatLinkFeed by value {
            (plugins.find(BeatLinkPlugin.id) as BeatLinkPlugin).beatLinkFeed
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
        val otherShaderInShow by value<Shader?> { paintShader }
        val shaderForButton by value { Shaders.red }

        val mutablePatch by value { MutablePatch(MutableShader(shaderInEdit)) }
        val mutableShow by value {
            MutableShow("show") {
                addPatch(mutablePatch)
                otherShaderInShow?.let { addPatch(it) }
            }
        }
        val observerSlot by value { slot<Observer>() }
        val mockShaderBuilder by value { mockk<ShaderBuilder>() }
        val getShaderBuilder by value<(Shader) -> ShaderBuilder> { { mockShaderBuilder } }
        val patchOnButton by value { MutablePatch(shaderForButton) }
        val button by value { mutableShow.addButton(MutablePanel(Panel("panel")), "button") {} }

        beforeEachTest {
            button.addPatch(patchOnButton)
            every { mockShaderBuilder.addObserver(capture(observerSlot)) } answers { observerSlot.captured }
            every { mockShaderBuilder.startBuilding() } just runs
            every { mockShaderBuilder.gadgets } returns emptyList()
            every { mockShaderBuilder.openShader } returns toolchain.openShader(shaderInEdit)
        }
        val notifiedStates by value { arrayListOf<State>() }

        val beforeBuildingShader by value { { } }
        val editingShader by value {
            beforeBuildingShader()
            EditingShader(mutableShow, mutablePatch, toolchain, getShaderBuilder)
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
                expectEmptyMap { mutablePatch.incomingLinks }
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

                    it("should still return PatchOptions") {
                        expect(editingShader.getPatchOptions()).notToBeNull()
                    }
                }
            }
        }

        context("when a shader has successfully compiled") {
            beforeEachTest {
                every { mockShaderBuilder.state } returns ShaderBuilder.State.Success
                observerSlot.captured.notifyChanged()
            }

            context("when the patch had no previous incoming links") {
                it("should set some reasonable defaults") {
                    expect(mutablePatch.incomingLinks.mapValues { (_, port) -> port.title })
                        .toBe(mapOf(
                            "inColor" to "Main Stream",
                            "theScale" to "The Scale Slider",
                            "time" to "Time"
                        ))
                }

                it("should create an appropriate feed") {
                    expect(mutablePatch.incomingLinks["theScale"])
                        .toBe(MutableFeedPort(SliderFeed("The Scale", 1f, 0f, 1f)))
                }

                context("when hints are provided") {
                    override(scaleUniform) { "uniform float theScale; // @@Slider min=.25 max=4 default=1" }

                    it("should create an appropriate feed") {
                        expect(mutablePatch.incomingLinks["theScale"])
                            .toBe(MutableFeedPort(SliderFeed("The Scale", 1f, .25f, 4f)))
                    }
                }

                context("when a plugin reference is provided") {
                    override(scaleUniform) { "uniform float theScale; // @@baaahs.BeatLink:BeatLink" }

                    it("should create an appropriate feed") {
                        expect(mutablePatch.incomingLinks["theScale"])
                            .toBe(MutableFeedPort(beatLinkFeed))
                    }
                }

                context("when a content type is provided") {
                    override(scaleUniform) { "uniform float theScale; // @type beat-link" }

                    it("should create an appropriate feed") {
                        expect(mutablePatch.incomingLinks["theScale"])
                            .toBe(MutableFeedPort(beatLinkFeed))
                    }
                }
            }

            context("when the patch had a previous incoming link") {
                override(beforeBuildingShader) {
                    {
                        mutablePatch.incomingLinks["theScale"] =
                            MutableFeedPort(SliderFeed("Custom slider", 1f, 0f, 1f))
                    }
                }

                it("shouldn't change it") {
                    expect(mutablePatch.incomingLinks["theScale"]!!.title)
                        .toBe("Custom slider Slider")
                }

                it("should be listed in link options") {
                    expect(editingShader.linkOptionsFor("theScale").stringify()).toBe("""
                            Feed:
                            - BeatLink
                            - Custom slider Slider (advanced)
                            - Pixel Distance from Edge
                            * The Scale Slider
                            - Time
                            Stream:
                            - Main Stream
                        """.trimIndent())
                }
            }

            context("when a link has been selected by a human") {
                beforeEachTest {
                    mutablePatch.incomingLinks["theScale"] =
                        MutableFeedPort(SliderFeed("custom slider", 1f, 0f, 1f))

                    editingShader.changeInputPortLink(
                        "theScale",
                        PortLinkOption(
                            MutableFeedPort(SliderFeed("custom slider", 1f, 0f, 1f))
                        )
                    )

                    // Rebuild.
                    observerSlot.captured.notifyChanged()
                }

                it("shouldn't modify it") {
                    expect(mutablePatch.incomingLinks["theScale"]!!.title)
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
                            Feed:
                            - BeatLink
                            - Pixel Distance from Edge
                            * The Scale Slider
                            - Time
                            Stream:
                            - Main Stream
                        """.trimIndent())
                }

                it("suggests reasonable link options for time") {
                    expect(editingShader.linkOptionsFor("time").stringify())
                        .toBe("""
                            Feed:
                            - BeatLink (advanced)
                            - Pixel Distance from Edge (advanced)
                            * Time
                            - Time Slider (advanced)
                            Stream:
                            - Main Stream
                        """.trimIndent())
                }

                it("suggests reasonable link options for input color") {
                    // Should never include ourself.
                    expect(editingShader.linkOptionsFor("inColor").stringify())
                        .toBe("""
                            Feed:
                            - Date (advanced)
                            - In Color Color Picker
                            - In Color Image
                            Stream:
                            * Main Stream
                        """.trimIndent())
                }

                context("when another patch has shader on a different stream") {
                    override(beforeBuildingShader) {
                        { patchOnButton.stream = MutableStream("other") }
                    }

                    context("and its result type matches this input's type") {
                        it("should include that stream as an option") {
                            // Should never include ourself.
                            expect(editingShader.linkOptionsFor("inColor").stringify())
                                .toBe("""
                                    Feed:
                                    - Date (advanced)
                                    - In Color Color Picker
                                    - In Color Image
                                    Stream:
                                    * Main Stream
                                    - Other Stream
                                """.trimIndent())
                        }
                    }

                    context("and its result type doesn't match this input's type") {
                        override(shaderForButton) { Shaders.flipY } // distortion

                        it("shouldn't include that stream as an option") {
                            expect(editingShader.linkOptionsFor("inColor").stringify())
                                .toBe("""
                                    Feed:
                                    - Date (advanced)
                                    - In Color Color Picker
                                    - In Color Image
                                    Stream:
                                    * Main Stream
                                """.trimIndent())
                        }
                    }
                }
            }
        }

        context("preview") {
            val dispatcher by value { TestCoroutineDispatcher() }
            override(shaderInEdit) { paintShader }
            override(otherShaderInShow) { null }
            override(getShaderBuilder) {
                { shader: Shader ->
                    PreviewShaderBuilder(
                        shader, toolchain, SceneMonitor(OpenScene(TestModel)),
                        coroutineScope = CoroutineScope(dispatcher)
                    )
                }
            }
            val gl by value { FakeGlContext() }
            val renderEngine by value { PreviewRenderEngine(gl, 1, 1) }

            beforeEachTest {
                // Run through the shader building steps.
                expect(editingShader.shaderBuilder.state).toBe(ShaderBuilder.State.Analyzing)
                expect(editingShader.state).toBe(State.Building)
                dispatcher.runCurrent()

                expect(editingShader.shaderBuilder.state).toBe(ShaderBuilder.State.Linked)
                expect(editingShader.state).toBe(State.Building)
                editingShader.shaderBuilder.startCompile(renderEngine)

                expect(editingShader.state).toBe(State.Building)
                dispatcher.runCurrent()
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

                    // Feed: PreviewResolution
                    uniform vec2 in_previewResolution;

                    // Feed: Raster Coordinate
                    uniform vec2 ds_rasterCoordinate_offset;
                    vec4 in_rasterCoordinate;

                    // Feed: Time
                    uniform float in_time;

                    // Shader: Screen Coords; namespace: p0
                    // Screen Coords

                    vec2 p0_screenCoordsi_result = vec2(0.);

                    #line 4 0
                    vec2 p0_screenCoords_main(
                        vec4 fragCoords 
                    ) {
                      return fragCoords.xy / in_previewResolution;
                    }

                    // Shader: Paint; namespace: p1
                    // Paint

                    vec4 p1_paint_gl_FragColor = vec4(0., 0., 0., 1.);

                    #line 2 1
                    void p1_paint_main(void) {
                        p1_paint_gl_FragColor = vec4(p0_screenCoordsi_result.x, p0_screenCoordsi_result.y, mod(in_time, 1.), 1.);
                    }


                    #line 10001
                    void main() {
                        // Invoke Raster Coordinate
                        in_rasterCoordinate = gl_FragCoord - vec4(ds_rasterCoordinate_offset, 0., 0.);
                    
                        // Invoke Screen Coords
                        p0_screenCoordsi_result = p0_screenCoords_main(in_rasterCoordinate);

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
        val advanced = if (linkOption.isAdvanced) " (advanced)" else ""
        lines.add("$selected ${linkOption.title}$advanced")
    }
    return lines.joinToString("\n")
}