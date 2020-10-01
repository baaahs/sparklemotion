package baaahs.show.live

import baaahs.getBang
import baaahs.gl.override
import baaahs.show.live.ControlDisplay.PanelBuckets.PanelBucket
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeKgl
import baaahs.shows.FakeShowPlayer
import baaahs.toBeSpecified
import baaahs.ui.Draggable
import baaahs.ui.DropTarget
import baaahs.unknown
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
        val openShow by value { showPlayer.openShow(show) }
        val editHandler by value { FakeEditHandler() }
        val dragNDrop by value { FakeDragNDrop() }
        val controlDisplay by value { ControlDisplay(openShow, editHandler, dragNDrop) }

        fun renderEditedShow(): String {
            val editedOpenShow = showPlayer.openShow(editHandler.updatedShow, openShow.getShowState())
            val newControlDisplay = ControlDisplay(editedOpenShow, editHandler, dragNDrop)
            return editedOpenShow.fakeRender(newControlDisplay)
        }

        beforeEachTest {
            mutableShow.addFixtureControls()
            mutableShow.addButton("Panel 1", "Button A") {}
            mutableShow.addButton("Panel 1", "Button B") {}
        }

        val panelBuckets by value {
            openShow.layouts.panelNames.associateWith { panelName ->
                controlDisplay.renderBuckets(panelName)
            }
        }

        fun findBucket(panelName: String, sectionTitle: String): PanelBucket {
            val panelBucket = panelBuckets.getBang(panelName, "Panel")
            return panelBucket.find { it.section.title == sectionTitle }
                ?: error(unknown("section", sectionTitle, panelBucket.map { it.section.title }))
        }

        val fromDropTarget by value { toBeSpecified<DropTarget>() }
        val toDropTarget by value { toBeSpecified<DropTarget>() }
        val draggedControl by value { toBeSpecified<Draggable>() }

        it("has the expected initial state") {
            expect(
                """
                    Panel 1:
                      |Show| scenesButtonGroup[*scene1Button*, scene2Button], buttonAButton, buttonBButton
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

        context("when a button is dragged") {
            override(fromDropTarget) { findBucket("Panel 1", "Show") }
            override(draggedControl) { fromDropTarget.getDraggable(1) }

            context("and dropped within the same DropTarget") {
                override(toDropTarget) { fromDropTarget }

                it("can be dropped back in the same bucket") {
                    expect(true) { draggedControl.willMoveTo(toDropTarget) }
                    expect(true) { toDropTarget.willAccept(draggedControl) }
                }

                it("reorders controls in the parent") {
                    dragNDrop.doMove(fromDropTarget, 1, fromDropTarget, 2)

                    expect(
                        listOf("scenesButtonGroup", "buttonBButton", "buttonAButton")
                    ) { editHandler.updatedShow.controlLayout["Panel 1"] }
                }
            }

            context("and dropped to another PanelBucket") {
                override(toDropTarget) { findBucket("Panel 1", "Scene 1") }

                it("can be dropped back into another bucket") {
                    expect(true) { draggedControl.willMoveTo(toDropTarget) }
                    expect(true) { toDropTarget.willAccept(draggedControl) }
                }

                it("removes the control from prior parent and adds it to the new parent") {
                    dragNDrop.doMove(fromDropTarget, 1, toDropTarget, 0)

                    expect(
                        """
                            Panel 1:
                              |Show| scenesButtonGroup[*scene1Button*, scene2Button], buttonBButton
                              |Scene 1| buttonAButton
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
                    ) { renderEditedShow() }
                }
            }

            context("and dropped to a ButtonGroup") {
                override(toDropTarget) {
                    (openShow.allControls.find { it.id == "scenesButtonGroup" } as OpenButtonGroupControl)
                        .createDropTarget(controlDisplay)
                }

                it("can be dropped into a button group") {
                    expect(true) { draggedControl.willMoveTo(toDropTarget) }
                    expect(true) { toDropTarget.willAccept(draggedControl) }
                }

                it("removes the control from prior parent and adds it to the new parent") {
                    dragNDrop.doMove(fromDropTarget, 1, toDropTarget, 0)

                    expect(
                        """
                            Panel 1:
                              |Show| scenesButtonGroup[buttonAButton, *scene1Button*, scene2Button], buttonBButton
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
                    ) { renderEditedShow() }
                }
            }

            context("from a ButtonGroup to a panel") {
                override(fromDropTarget) {
                    (openShow.allControls.find { it.id == "scenesButtonGroup" } as OpenButtonGroupControl)
                        .createDropTarget(controlDisplay)
                }
                override(toDropTarget) { findBucket("Panel 1", "Scene 1") }

                it("can be dropped") {
                    expect(true) { draggedControl.willMoveTo(toDropTarget) }
                    expect(true) { toDropTarget.willAccept(draggedControl) }
                }

                it("removes the control from prior parent and adds it to the new parent") {
                    dragNDrop.doMove(fromDropTarget, 1, toDropTarget, 0)

                    expect(
                        """
                            Panel 1:
                              |Show| scenesButtonGroup[*scene1Button*], buttonAButton, buttonBButton
                              |Scene 1| scene2Button
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
                    ) { renderEditedShow() }
                }
            }
        }
    }
})