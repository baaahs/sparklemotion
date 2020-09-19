package baaahs.show.live

import baaahs.getBang
import baaahs.gl.patch.AutoWirer
import baaahs.plugin.Plugins
import baaahs.show.ButtonControl
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeKgl
import baaahs.shows.FakeShowPlayer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object ControlDragNDropSpec : Spek({
    describe("Control Drag & Drop") {
        val mutableShow by value {
            MutableShow("Show")
                .editLayouts { copyFrom(createLayouts("Panel 1", "Panel 2", "Panel 3")) }
        }
        val show by value { mutableShow.build(ShowBuilder()) }

        val showPlayer by value { FakeShowPlayer(FakeGlContext(FakeKgl())) }
        val autoWirer by value { AutoWirer(Plugins.safe()) }
        val showOpener by value { ShowOpener(autoWirer.glslAnalyzer, show, showPlayer) }
        val openShow by value { showOpener.openShow() }
        val editMode by value { true }
        val editHandler by value { FakeEditHandler() }
        val dragNDrop by value { FakeDragNDrop() }
        val controlDisplay by value {
            ControlDisplay(openShow, editMode, editHandler, dragNDrop)
        }

        beforeEachTest {
            mutableShow.addFixtureControls()
            mutableShow.addButton("Panel 1", "Button A") {}
            mutableShow.addButton("Panel 1", "Button B") {}
        }

        val panel1Buckets by value {
            val buckets = arrayListOf<ControlDisplay.PanelBuckets.PanelBucket>()
            controlDisplay.render("Panel 1") { panelBucket -> buckets.add(panelBucket) }
            buckets
        }
        val panel1ShowBucket by value { panel1Buckets.first() }
        val panel1SceneBucket by value { panel1Buckets[1] }

        it("has the expected initial state") {
            expect(
                listOf("scenesButtonGroup", "buttonAButton", "buttonBButton")
            ) { panel1ShowBucket.controls.map { it.control.id } }
        }

        context("when a control is dragged") {
            val buttonADraggable by value { panel1ShowBucket.getDraggable(1) }

            it("can be dropped back in the same bucket") {
                expect(true) { buttonADraggable.willMoveTo(panel1ShowBucket) }
            }

            it("can be dropped back into another bucket") {
                expect(true) { buttonADraggable.willMoveTo(panel1SceneBucket) }
            }

            context("and dropped within the same DropTarget") {
                beforeEachTest {
                    dragNDrop.doMove(panel1ShowBucket, 1, panel1ShowBucket, 2)
                }

                it("reorders controls in the parent") {
                    expect(
                        listOf("scenesButtonGroup", "buttonBButton", "buttonAButton")
                    ) { editHandler.updatedShow.controlLayout["Panel 1"] }
                }
            }

            context("and dropped to another DropTarget") {
                beforeEachTest {
                    dragNDrop.doMove(panel1ShowBucket, 1, panel1SceneBucket, 0)
                }

                it("removes the control from prior parent") {
                    expect(
                        listOf("scenesButtonGroup", "buttonBButton")
                    ) { editHandler.updatedShow.controlLayout["Panel 1"] }
                }

                it("adds the control from its new parent") {
                    val scene1ButtonAfterEdit =
                        editHandler.updatedShow.controls.getBang("scene1Button", "Control") as ButtonControl

                    expect(listOf("buttonAButton")) { scene1ButtonAfterEdit.controlLayout["Panel 1"] }
                }
            }
        }
    }

})