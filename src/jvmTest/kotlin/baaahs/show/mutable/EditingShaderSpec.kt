package baaahs.show.mutable

import baaahs.*
import baaahs.app.ui.editor.LinkOption
import baaahs.app.ui.editor.PortLinkOption
import baaahs.gl.override
import baaahs.gl.patch.AutoWirer
import baaahs.gl.preview.ShaderBuilder
import baaahs.glsl.Shaders
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugins
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.show.Shader
import baaahs.show.ShaderType
import baaahs.show.mutable.EditingShader.State
import baaahs.ui.Observer
import baaahs.ui.addObserver
import io.mockk.*
import org.spekframework.spek2.Spek
import kotlin.test.expect

// Currently in jvmTest so we can use mockk.
// TODO: move back to commonTest when mockk supports multiplatform.
object EditingShaderSpec : Spek({
    describe<EditingShader> {
        val plugins by value { Plugins.safe() + BeatLinkPlugin(StubBeatSource(), FakeClock()) }
        val autoWirer by value { AutoWirer(plugins) }
        val scaleUniform by value { "uniform float theScale;" }
        val paintShader by value {
            Shader(
                "Paint", ShaderType.Paint, """
                uniform float time;
                void main(void) {
                    gl_FragColor = vec4(gl_FragCoord.x, gl_FragCoord.y, mod(time, 1.), 1.);
                }
            """.trimIndent()
            )
        }
        val filterShader by value {
            Shader(
                "Filter", ShaderType.Filter, """
                $scaleUniform
                uniform float time;
                vec4 mainFilter(vec4 inColor) {
                    return inColor * theScale + time * 0.;
                }
            """.trimIndent()
            )
        }
        val shaderInEdit by value { filterShader }
        val otherShaderInPatch by value { paintShader }
        val otherShaderInShow by value { Shaders.red }

        val mutableShaderInstance by value { MutableShaderInstance(MutableShader(shaderInEdit)) }
        val mutablePatch by value { MutablePatch { addShaderInstance(mutableShaderInstance) } }
        val mutableShow by value { MutableShow("show") { addPatch(mutablePatch) } }
        val observerSlot by value { slot<Observer>() }
        val mockShaderBuilder by value { mockk<ShaderBuilder>() }
        val button by value { mutableShow.addButton("panel", "button") {} }
        val otherPatchInShow by value { MutablePatch { addShaderInstance(otherShaderInShow) } }

        beforeEachTest {
            mutablePatch.addShaderInstance(otherShaderInPatch)
            button.addPatch(otherPatchInShow)
            every { mockShaderBuilder.addObserver(capture(observerSlot)) } answers { observerSlot.captured }
            every { mockShaderBuilder.startBuilding() } just runs
            every { mockShaderBuilder.gadgets } returns emptyList()
            every { mockShaderBuilder.openShader } returns autoWirer.glslAnalyzer.openShader(shaderInEdit)
        }
        val notifiedStates by value { arrayListOf<State>() }

        val beforeBuildingShader by value { { } }
        val editingShader by value {
            beforeBuildingShader()
            EditingShader(mutableShow, mutablePatch, mutableShaderInstance, autoWirer) {
                mockShaderBuilder
            }.also { it.addObserver { notifiedStates.add(it.state) } }
        }
        beforeEachTest {
            editingShader.let {} // Make sure it's warmed up.
        }

        context("at initialization") {
            it("is in Building state") {
                expect(State.Building) { editingShader.state }
            }

            it("starts building the shader") {
                verify { mockShaderBuilder.startBuilding() }
            }

            it("has no gadgets") {
                expect(emptyList()) { editingShader.gadgets }
            }

            it("should not have notified observers yet") {
                expect(emptyList<State>()) { notifiedStates }
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
                        expect(listOf(State.Success)) { notifiedStates }
                    }
                }

                context("that it's still building") {
                    override(builderState) { ShaderBuilder.State.Compiling }
                    it("should not notify our observers") {
                        expect(emptyList<State>()) { notifiedStates }
                    }
                }

                context("of a failure to build") {
                    override(builderState) { ShaderBuilder.State.Errors }
                    it("should not notify our observers again") {
                        expect(listOf(State.Errors)) { notifiedStates }
                    }

                    it("should return null for ShaderInstanceOptions") {
                        expect(null) { editingShader.getShaderInstanceOptions() }
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
                    expect(
                        mapOf(
                            "gl_FragColor" to "Main Channel",
                            "theScale" to "The Scale Slider",
                            "time" to "Time"
                        )
                    ) { mutableShaderInstance.incomingLinks.mapValues { (_, port) -> port.title } }
                }

                it("should create an appropriate data source") {
                    expect(
                        MutableDataSourcePort(CorePlugin.SliderDataSource("The Scale", 1f, 0f, 1f))
                    ) { mutableShaderInstance.incomingLinks["theScale"] }
                }

                context("when hints are provided") {
                    override(scaleUniform) { "uniform float theScale; // @@Slider min=.25 max=4 default=1" }

                    it("should create an appropriate data source") {
                        expect(
                            MutableDataSourcePort(CorePlugin.SliderDataSource("The Scale", 1f, .25f, 4f))
                        ) { mutableShaderInstance.incomingLinks["theScale"] }
                    }
                }

                context("when a plugin reference is provided") {
                    override(scaleUniform) { "uniform float theScale; // @@baaahs.BeatLink:BeatLink" }

                    it("should create an appropriate data source") {
                        expect(
                            MutableDataSourcePort(BeatLinkPlugin.BeatLinkDataSource())
                        ) { mutableShaderInstance.incomingLinks["theScale"] }
                    }
                }

                context("when a content type is provided") {
                    override(scaleUniform) { "uniform float theScale; // @type beat-link" }

                    it("should create an appropriate data source") {
                        expect(
                            MutableDataSourcePort(BeatLinkPlugin.BeatLinkDataSource())
                        ) { mutableShaderInstance.incomingLinks["theScale"] }
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
                    expect("Custom slider Slider") { mutableShaderInstance.incomingLinks["theScale"]!!.title }
                }

                it("should be listed in link options") {
                    expect(
                        """
                            Data Source:
                            * BeatLink
                            * Custom slider Slider
                            * The Scale Slider
                            * Time
                        """.trimIndent()
                    ) {
                        editingShader.linkOptionsFor("theScale").stringify()
                    }
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
                    expect("custom slider Slider") { mutableShaderInstance.incomingLinks["theScale"]!!.title }
                }

                // TODO: ... unless its type doesn't make any sense now.
            }

            context("incoming link suggestions") {
                it("suggests link options for each input port") {
                    expect(setOf("theScale", "time", "gl_FragColor")) {
                        editingShader.openShader!!.inputPorts.map { it.id }.toSet()
                    }
                }

                it("suggests reasonable link options for scale") {
                    expect(
                        """
                            Data Source:
                            * BeatLink
                            * The Scale Slider
                            * Time
                        """.trimIndent()
                    ) {
                        editingShader.linkOptionsFor("theScale").stringify()
                    }
                }

                it("suggests reasonable link options for time") {
                    expect(
                        """
                            Data Source:
                            * BeatLink
                            * Time
                            * Time Slider
                        """.trimIndent()
                    ) {
                        editingShader.linkOptionsFor("time").stringify()
                    }
                }

                it("suggests reasonable link options for input color") {
                    // Should never include ourself.
                    expect(
                        """
                            Channel:
                            * Main Channel
                            Data Source:
                            * Input Color ColorPicker
                            Shader Output:
                            * Shader "Paint" output
                        """.trimIndent()
                    ) {
                        editingShader.linkOptionsFor("gl_FragColor").stringify()
                    }
                }

                context("when another patch has shader on a different shader channel") {
                    override(beforeBuildingShader) {
                        { otherPatchInShow.mutableShaderInstances.only().shaderChannel = MutableShaderChannel("other") }
                    }

                    context("and its result type matches this input's type") {
                        it("should include that shader channel as an option") {
                            // Should never include ourself.
                            expect(
                                """
                                    Channel:
                                    * Main Channel
                                    * Other Channel
                                    Data Source:
                                    * Input Color ColorPicker
                                    Shader Output:
                                    * Shader "Paint" output
                                """.trimIndent()
                            ) {
                                editingShader.linkOptionsFor("gl_FragColor").stringify()
                            }
                        }
                    }

                    context("and its result type doesn't match this input's type") {
                        override(otherShaderInShow) { Shaders.flipY } // distortion

                        it("shouldn't include that shader channel as an option") {
                            expect(
                                """
                                    Channel:
                                    * Main Channel
                                    Data Source:
                                    * Input Color ColorPicker
                                    Shader Output:
                                    * Shader "Paint" output
                                """.trimIndent()
                            ) {
                                editingShader.linkOptionsFor("gl_FragColor").stringify()
                            }
                        }

                    }
                }
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
        lines.add("* ${linkOption.title}")
    }
    return lines.joinToString("\n")
}