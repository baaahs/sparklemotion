package baaahs.show.mutable

import baaahs.StubBeatSource
import baaahs.app.ui.editor.LinkOption
import baaahs.app.ui.editor.PortLinkOption
import baaahs.describe
import baaahs.expectEmptyMap
import baaahs.gl.override
import baaahs.gl.patch.AutoWirer
import baaahs.gl.preview.ShaderBuilder
import baaahs.gl.testPlugins
import baaahs.glsl.Shaders
import baaahs.only
import baaahs.plugin.CorePlugin
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.show.Shader
import baaahs.show.ShaderType
import baaahs.show.mutable.EditingShader.State
import baaahs.ui.Observer
import baaahs.ui.addObserver
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.*
import org.spekframework.spek2.Spek
import kotlin.collections.List
import kotlin.collections.arrayListOf
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.emptyList
import kotlin.collections.forEach
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.collections.mapOf
import kotlin.collections.mapValues
import kotlin.collections.set
import kotlin.collections.setOf
import kotlin.collections.sortedWith
import kotlin.collections.toSet

// Currently in jvmTest so we can use mockk.
// TODO: move back to commonTest when mockk supports multiplatform.
object EditingShaderSpec : Spek({
    describe<EditingShader> {
        val plugins by value { testPlugins() + BeatLinkPlugin.Builder(StubBeatSource()) }
        val beatLinkDataSource by value {
            (plugins.find(BeatLinkPlugin.id) as BeatLinkPlugin).beatLinkDataSource
        }
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

                    it("should return null for ShaderInstanceOptions") {
                        expect(editingShader.getShaderInstanceOptions()).toBe(null)
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
                            "gl_FragColor" to "Main Channel",
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
                            Data Source:
                            * BeatLink
                            * Custom slider Slider
                            * The Scale Slider
                            * Time
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
                        .toBe(setOf("theScale", "time", "gl_FragColor"))
                }

                it("suggests reasonable link options for scale") {
                    expect(editingShader.linkOptionsFor("theScale").stringify())
                        .toBe("""
                            Data Source:
                            * BeatLink
                            * The Scale Slider
                            * Time
                        """.trimIndent())
                }

                it("suggests reasonable link options for time") {
                    expect(editingShader.linkOptionsFor("time").stringify())
                        .toBe("""
                            Data Source:
                            * BeatLink
                            * Time
                            * Time Slider
                        """.trimIndent())
                }

                it("suggests reasonable link options for input color") {
                    // Should never include ourself.
                    expect(editingShader.linkOptionsFor("gl_FragColor").stringify())
                        .toBe("""
                            Channel:
                            * Main Channel
                            Data Source:
                            * Input Color ColorPicker
                            Shader Output:
                            * Shader "Paint" output
                        """.trimIndent())
                }

                context("when another patch has shader on a different shader channel") {
                    override(beforeBuildingShader) {
                        { otherPatchInShow.mutableShaderInstances.only().shaderChannel = MutableShaderChannel("other") }
                    }

                    context("and its result type matches this input's type") {
                        it("should include that shader channel as an option") {
                            // Should never include ourself.
                            expect(editingShader.linkOptionsFor("gl_FragColor").stringify())
                                .toBe("""
                                    Channel:
                                    * Main Channel
                                    * Other Channel
                                    Data Source:
                                    * Input Color ColorPicker
                                    Shader Output:
                                    * Shader "Paint" output
                                """.trimIndent())
                        }
                    }

                    context("and its result type doesn't match this input's type") {
                        override(otherShaderInShow) { Shaders.flipY } // distortion

                        it("shouldn't include that shader channel as an option") {
                            expect(editingShader.linkOptionsFor("gl_FragColor").stringify())
                                .toBe("""
                                    Channel:
                                    * Main Channel
                                    Data Source:
                                    * Input Color ColorPicker
                                    Shader Output:
                                    * Shader "Paint" output
                                """.trimIndent())
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