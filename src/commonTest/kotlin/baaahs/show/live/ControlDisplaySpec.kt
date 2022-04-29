package baaahs.show.live

import baaahs.control.OpenButtonGroupControl
import baaahs.describe
import baaahs.gl.testToolchain
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.shows.FakeShowPlayer
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip

object ControlDisplaySpec : Spek({
    describe<ControlDisplay> {
        val mutableShow by value {
            MutableShow("Show").also {
                it.editLayouts {
                    copyFrom(createLayouts(it, "Panel 1", "Panel 2", "Panel 3"))
                }
            }
        }
        val show by value { mutableShow.build(ShowBuilder()) }

        val showPlayer by value { FakeShowPlayer() }
        val showOpener by value { ShowOpener(testToolchain, show, showPlayer) }
        val openShow by value { showOpener.openShow() }
        val editHandler by value { FakeEditHandler() }
        val dragNDrop by value { FakeDragNDrop<Int>() }
        val controlDisplay by value {
            ControlDisplay(openShow, editHandler, dragNDrop)
        }

        context("empty show") {
            it("renders all empty sections") {
                expect(openShow.fakeRender(controlDisplay)).toBe("""
                    Panel 1:
                      |Show|
                    Panel 2:
                      |Show|
                    Panel 3:
                      |Show|
                """.trimIndent())
                expect(controlDisplay.unplacedControls).isEmpty()
            }
        }

        context("a show with button groups") {
            beforeEachTest { mutableShow.addFixtureControls() }

            val panel1 by value { openShow.controlLayout[openShow.getPanel("panel1")] ?: emptyList() }
            val scenesButtonGroup by value { panel1.first() as OpenButtonGroupControl }
            val scene1Button by value { scenesButtonGroup.buttons.first() }
            val backdrops1ButtonGroup by value {
                scene1Button.controlLayout[openShow.getPanel("panel2")]!!.first() as OpenButtonGroupControl
            }
            val backdrop11Button by value { backdrops1ButtonGroup.buttons.first() }

            it("renders controls correctly") {
                expect(openShow.fakeRender(controlDisplay)).toBe("""
                        Panel 1:
                          |Show| scenes[*scene1*, scene2]
                          |Scene 1|
                          |Backdrop 1.1|
                        Panel 2:
                          |Show|
                          |Scene 1| backdrops[*backdrop11*, backdrop12]
                          |Backdrop 1.1|
                        Panel 3:
                          |Show|
                          |Scene 1| slider1
                          |Backdrop 1.1|
                    """.trimIndent())
            }

            // TODO: Which controls should we display here?
            it("lists controls that aren't visible on screen as unplaced",
                skip = Skip.Yes("Figure out how this should work and fix it")
            ) {
                expect(controlDisplay.unplacedControls.map { it.id }.toSet())
                    .toBe(setOf("slider2", "backdrop21", "backdrop22", "backdrops2"))
            }

            it("has the first item in the button group selected by default") {
                expect(scenesButtonGroup.buttons.map { it.isPressed })
                    .containsExactly(true,false)

                expect(openShow.buildActivePatchSet().activePatches.prettyPrint())
                    .toBe((openShow.patches + scene1Button.patches + backdrop11Button.patches).prettyPrint())
            }
        }
    }
})

fun List<OpenPatch>.prettyPrint(): List<String> =
    map { it.shader.title }