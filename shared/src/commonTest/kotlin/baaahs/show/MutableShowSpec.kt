package baaahs.show

import baaahs.gl.Toolchain
import baaahs.gl.autoWire
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.gl.testToolchain
import baaahs.kotest.value
import baaahs.only
import baaahs.show.live.FakeOpenShader
import baaahs.show.mutable.*
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec
import kotlin.collections.set

object MutableShowSpec : DescribeSpec({
    describe("MutableShow") {
        val shader0 by value { testToolchain.testPatch("shader 0") }
        val shader1a by value { testToolchain.testPatch("shader 1a") }
        val shader2a by value { testToolchain.testPatch("shader 2a") }

        val baseMutableShow by value {
            MutableShow("test show").apply {
                addPatch(shader0)

                val mainPanel = MutablePanel(Panel("Main"))
                editLayouts {
                    panels["main"] = mainPanel
                    editLayout("default") {
                        tabs.add(MutableLegacyTab("Tab"))
                    }
                }

                addButtonGroup(mainPanel, "scene 1") {
                    addButton("patchset 1a") { addPatch(shader1a) }
                }
                addButtonGroup(mainPanel, "scene 2") {
                    addButton("patchset 2a") { addPatch(shader2a) }
                    addButton("patchset 2b") { addPatch(testToolchain.testPatch("shader 2b")) }
                    addButton("patchset 2c") { addPatch(testToolchain.testPatch("shader 2c")) }
                }
//                addControl("Scenes", MutableButtonGroupControl("Scenes", ButtonGroupControl.Direction.Horizontal, this))
            }
        }
        val baseShow by value { baseMutableShow.build(ShowBuilder()) }
//        fun Show.showState() = ShowState.from(this).selectScene(1).selectPatchSet(1)
//        val baseShowState by value { baseShow.showState() }
        val mutableShow by value { MutableShow(baseShow) }
        val show by value { mutableShow.build(ShowBuilder()) }
//        val showState by value { mutableShow.getShowState() }

        context("base show") {
//            it("has the expected initial scenes and patchsets") {
//                expect(
//                    listOf("scene 1 (patchset 1a)", "scene 2 (patchset 2a, patchset 2b, patchset 2c)")
//                ) { show.desc() }
//            }

            it("has the expected initial shaders") {
                expect(show.shaders.values.map { it.title }.toSet())
                    .toBe(setOf("shader 0", "shader 1a", "shader 2a", "shader 2b", "shader 2c"))
            }

            it("has the expected initial feeds") {
                expect(show.feeds.values.map { it.title }.toSet())
                    .toBe(setOf("Time", "Resolution", "Blueness Slider"))
            }
        }

        it("leaves everything as-is if no changes are made") {
            expect(baseShow).toBe(show)
        }

        context("editing a patch") {
            val editor by value { mutableShow.patches.only() }

            context("when weird port mappings are added") {
                beforeEach {
                    editor.incomingLinks["nonsense"] = MutableConstPort("invalid", GlslType.from("?unknown?"))
                }

                it("should retain them, I guess?") {
                    val id = show.patchIds.only()
                    val patch = show.patches[id]!!
                    expect(patch.incomingLinks.keys)
                        .toBe(setOf("nonsense", "time", "blueness", "resolution", "gl_FragCoord"))
                }
            }

            context(".isFilter") {
                val inputPorts by value { listOf<InputPort>() }
                val outputPort by value { OutputPort(ContentType.Color) }
                val openShader by value { FakeOpenShader(inputPorts, outputPort) }
                val mutableShader by value { MutableShader("Test shader", "Src for shader") }
                val incomingLinks by value { mutableMapOf<String, MutablePort>() }
                val stream by value { Stream.Main.toMutable() }
                val patch by value { MutablePatch(mutableShader, incomingLinks, stream) }

                context("with no input port links") {
                    it("isn't a filter") { expect(patch.isFilter(openShader)).toBe(false) }
                }

                context("when an input port's content type matches the return content type") {
                    override(inputPorts) { listOf(InputPort("color", ContentType.Color)) }
                    override(incomingLinks) { mapOf("color" to MutableConstPort("foo", GlslType.Vec4)) }

                    it("isn't a filter") { expect(patch.isFilter(openShader)).toBe(false) }

                    context("linked to a stream") {
                        override(incomingLinks) { mapOf("color" to Stream.Main.toMutable()) }

                        context("on the same channel") {
                            it("is a filter") { expect(patch.isFilter(openShader)).toBe(true) }
                        }

                        context("on a different channel") {
                            override(stream) { Stream("other").toMutable() }
                            it("isn't a filter") { expect(patch.isFilter(openShader)).toBe(false) }
                        }
                    }
                }

                context("when the return content type doesn't match any of the input ports") {
                    override(outputPort) { OutputPort(ContentType.XyCoordinate) }
                    it("is a filter") {
                        expect(patch.isFilter(openShader)).toBe(false)
                    }
                }
            }
        }

//        context("adding a patchset") {
//            beforeEach {
//                mutableShow.apply {
//                    editScene(1) {
//                        addButton("patchset 2b") { addPatch(toolchain.testPatch("shader 2b")) }
//                    }
//                }
//            }
//
//            it("collects shaders") {
//                expect(
//                    setOf("Cylindrical Projection", "shader 1a", "shader 2a", "shader 2b", "shader 2c")
//                ) { show.shaders.values.map { it.title }.toSet() }
//            }
//        }

//        context("editing a patchset") {
//            beforeEach {
//                mutableShow.editScene(1) {
//                    editPatchSet(1) { title = "modified $title" }
//                }
//            }
//
//            it("applies changes") {
//                expect(
//                    listOf("scene 1 (patchset 1a)", "scene 2 (patchset 2a, modified patchset 2b, patchset 2c)")
//                ) { show.desc() }
//            }
//        }

//        context("reordering scenes") {
//            val fromIndex = 1
//            val toIndex = 0
//
//            beforeEach {
//                baseMutableShow.apply {
//                    addButtonGroup("scene 3") {
//                        addButton("patchset 3a") { addPatch(toolchain.testPatch("shader 3a")) }
//                    }
//                }
//
//                mutableShow.moveScene(fromIndex, toIndex)
//            }
//
//            it("reorders scenes") {
//                expect(
//                    listOf(
//                        "scene 2 (patchset 2a, patchset 2b, patchset 2c)",
//                        "scene 1 (patchset 1a)",
//                        "scene 3 (patchset 3a)"
//                    )
//                ) { show.desc() }
//            }
//
//            it("reorders patchset selections in show state") {
//                expect(listOf(1, 0, 0)) { showState.patchSetSelections }
//            }
//
//            context("if the 'from' scene was selected for that scene") {
//                override(baseShowState) { baseShow.showState().selectScene(fromIndex) }
//                it("keeps the same scene selected") {
//                    expect(ShowState(toIndex, listOf(1, 0, 0))) { showState }
//                }
//            }
//
//            context("if the 'to' scene was selected for that scene") {
//                override(baseShowState) { baseShow.showState().selectScene(toIndex) }
//                it("keeps the same scene selected") {
//                    expect(ShowState(fromIndex, listOf(1, 0, 0))) { showState }
//                }
//            }
//
//            context("if some other scene was selected") {
//                override(baseShowState) { baseShow.showState().selectScene(2) }
//                it("doesn't update show state") {
//                    expect(ShowState(2, listOf(1, 0, 0))) { showState }
//                }
//            }
//
//        }

//        context("reordering patchsets") {
//            val fromIndex = 1
//            val toIndex = 2
//
//            override(baseShowState) { ShowState(1, listOf(2, 0)) }
//
//            beforeEach {
//                mutableShow.apply {
//                    editScene(1) { movePatchSet(fromIndex, toIndex) }
//                }
//            }
//
//            it("reorders patchsets") {
//                expect(
//                    listOf("scene 1 (patchset 1a)", "scene 2 (patchset 2a, patchset 2c, patchset 2b)")
//                ) { show.desc() }
//            }
//
//            context("if the 'from' patchset was selected for that scene") {
//                override(baseShowState) { baseShow.showState().selectPatchSet(fromIndex) }
//                it("keeps the same patchset selected") {
//                    expect(ShowState(1, listOf(0, toIndex))) { showState }
//                }
//            }
//
//            context("if the 'to' patchset was selected for that scene") {
//                override(baseShowState) { baseShow.showState().selectPatchSet(toIndex) }
//                it("keeps the same patchset selected") {
//                    expect(ShowState(1, listOf(0, fromIndex))) { showState }
//                }
//            }
//
//            context("if some other patchset was selected") {
//                override(baseShowState) { baseShow.showState().selectPatchSet(0) }
//                it("doesn't update show state") {
//                    expect(ShowState(1, listOf(0, 0))) { showState }
//                }
//            }
//        }
    }
})

private fun Toolchain.testPatch(title: String): MutablePatch {
    val shader = Shader(
        title,
        """
            // $title
            uniform float time;
            uniform vec2  resolution;
            uniform float blueness;
            int someGlobalVar;
            const int someConstVar = 123;

            int anotherFunc(int i) { return i; }

            void main( void ) {
                vec2 uv = gl_FragCoord.xy / resolution.xy;
                someGlobalVar = anotherFunc(someConstVar);
                gl_FragColor = vec4(uv.xy, blueness, 1.);
            }
        """.trimIndent()
    )

    return autoWire(shader).acceptSuggestedLinkOptions().confirm()
}

//private fun Show.desc(): List<String> =
//    scenes.map { "${it.title} (${it.patchSets.joinToString(", ") { it.title }})" }