package baaahs.show.live

import baaahs.describe
import baaahs.gl.patch.AutoWirer
import baaahs.plugin.Plugins
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.shows.FakeShowPlayer
import org.spekframework.spek2.Spek
import kotlin.test.expect

object ControlDisplaySpec : Spek({
    describe<ControlDisplay> {
        val mutableShow by value {
            MutableShow("Show")
                .editLayouts { copyFrom(createLayouts("Panel 1", "Panel 2", "Panel 3")) }
        }
        val show by value { mutableShow.build(ShowBuilder()) }

        val showPlayer by value { FakeShowPlayer() }
        val autoWirer by value { AutoWirer(Plugins.safe()) }
        val showOpener by value { ShowOpener(autoWirer.glslAnalyzer, show, showPlayer) }
        val openShow by value { showOpener.openShow() }
        val editHandler by value { FakeEditHandler() }
        val dragNDrop by value { FakeDragNDrop() }
        val controlDisplay by value {
            ControlDisplay(openShow, editHandler, dragNDrop)
        }

        context("empty show") {
            it("renders all empty sections") {
                expect(
                    """
                    Panel 1:
                      |Show|
                    Panel 2:
                      |Show|
                    Panel 3:
                      |Show|
                """.trimIndent()
                ) { openShow.fakeRender(controlDisplay) }
                expect(emptyList()) { controlDisplay.unplacedControls }
            }
        }

        context("a show with button groups") {
            beforeEachTest { mutableShow.addFixtureControls() }

            val panel1 by value { openShow.controlLayout["Panel 1"] ?: emptyList() }
            val scenesButtonGroup by value { panel1.first() as OpenButtonGroupControl }
            val scene1Button by value { scenesButtonGroup.buttons.first() }
            val backdrops1ButtonGroup by value { scene1Button.controlLayout["Panel 2"]!!.first() as OpenButtonGroupControl }
            val backdrop11Button by value { backdrops1ButtonGroup.buttons.first() }

            it("renders controls correctly") {
                expect(
                    """
                        Panel 1:
                          |Show| scenesButtonGroup[*scene1Button*, scene2Button]
                          |Scene 1|
                          |Backdrop 1.1|
                        Panel 2:
                          |Show|
                          |Scene 1| backdropsButtonGroup[*backdrop11Button*, backdrop12Button]
                          |Backdrop 1.1|
                        Panel 3:
                          |Show|
                          |Scene 1| slider1SliderControl
                          |Backdrop 1.1|
                    """.trimIndent()
                ) { openShow.fakeRender(controlDisplay) }
            }

            it("lists controls that aren't visible on screen as unplaced") {
                expect(
                    setOf("slider2SliderControl", "backdrop21Button", "backdrop22Button", "backdropsButtonGroup2")
                ) { controlDisplay.unplacedControls.map { it.id }.toSet() }
            }

            it("has the first item in the button group selected by default") {
                expect(listOf(true, false)) { scenesButtonGroup.buttons.map { it.isPressed } }

                expect(
                    (openShow.patches + scene1Button.patches + backdrop11Button.patches).prettyPrint()
                ) {
                    openShow.activeSet().getActivePatches().prettyPrint()
                }
            }
        }
    }
})

fun List<OpenPatch>.prettyPrint(): List<String> {
    return flatMap { it.shaderInstances.map { it.shader.title } }
}