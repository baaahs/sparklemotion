package baaahs.app.ui.editor

import baaahs.app.ui.AddButtonToButtonGroupEditIntent
import baaahs.app.ui.ControlEditIntent
import baaahs.app.ui.EditIntent
import baaahs.app.ui.ShowEditIntent
import baaahs.gl.override
import baaahs.show.SampleData
import baaahs.show.Show
import baaahs.show.mutable.MutableButtonControl
import baaahs.show.mutable.MutableButtonGroupControl
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShow
import baaahs.ui.addObserver
import describe
import org.spekframework.spek2.Spek
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.expect

object EditableManagerSpec : Spek({
    describe<EditableManager> {
        val showUpdates by value { arrayListOf<Show>() }
        val editableManager by value {
            EditableManager { showUpdates.add(it) }
        }
        val facadeUpdates by value { arrayListOf<String>() }

        beforeEachTest {
            editableManager.addObserver { facadeUpdates.add("updated") }
        }

        context("when there's no active session") {
            it("isEditing() is false") {
                expect(false) { editableManager.isEditing() }
            }

            it("editorPanels is empty") {
                expect(emptyList()) { editableManager.editorPanels }
            }
        }

        context("when there's an active session") {
            val baseShow by value { SampleData.sampleShow }
            val editIntent by value<EditIntent> { ShowEditIntent() }
            val session: EditableManager.Session by value { editableManager.session!! }

            beforeEachTest {
                editableManager.openEditor(baseShow, editIntent)
            }

            it("isEditing() is true") {
                expect(true) { editableManager.isEditing() }
            }

            it("editorPanels comes from the show") {
                expect(arrayListOf("Properties", "Patches")) {
                    editableManager.editorPanels.map { it.title }
                }
            }

            it("selectedPanel defaults to the first") {
                expect("Properties") { editableManager.selectedPanel?.title }
            }

            it("creates a MutableShow") {
                expect(baseShow) { editableManager.session!!.mutableShow.getShow() }
            }

            it("finds the relevant MutableEditable") {
                assertSame(session.mutableShow, session.mutableEditable)
            }

            it("pushes it onto the undo stack") {
                assertSame(baseShow, editableManager.undoStack.stack.first().show)
            }

            it("is not modified") {
                expect(false) { editableManager.isModified() }
            }

            context("when a change has been made to the editable") {
                beforeEachTest {
                    (session.mutableEditable as MutablePatchHolder).title = "different title"
                    editableManager.onChange()
                }

                it("pushes the changed show onto the undo stack") {
                    expect(
                        listOf("Sample Show", "different title")
                    ) { editableManager.undoStack.stack.map { it.show.title } }
                }

                it("is modified") {
                    expect(true) { editableManager.isModified() }
                }

                context("when changes are applied") {
                    var priorMutableShow: MutableShow? = null

                    beforeEachTest {
                        priorMutableShow = session.mutableShow
                        editableManager.applyChanges()
                    }

                    it("calls onApply callback with new show") {
                        expect(listOf("different title")) { showUpdates.map { it.title } }
                    }

                    it("still has the same undo stack") {
                        expect(
                            listOf("Sample Show", "different title")
                        ) { editableManager.undoStack.stack.map { it.show.title } }
                    }

                    it("is not modified") {
                        expect(false) { editableManager.isModified() }
                    }

                    it("has a different MutableShow") {
                        assertTrue { priorMutableShow !== editableManager.session!!.mutableShow }
                    }

                    context("and user clicks Undo") {
                        beforeEachTest {
                            editableManager.undo()
                        }

                        it("is modified") {
                            expect(true) { editableManager.isModified() }
                        }

                        context("and user clicks Redo") {
                            beforeEachTest {
                                editableManager.redo()
                            }

                            it("is not modified") {
                                expect(false) { editableManager.isModified() }
                            }
                        }
                    }
                }
            }

            context("when the EditIntent creates a new object") {
                val baseButtonGroupId by value { baseShow.findControlIdByTitle("Scenes") }
                override(editIntent) { AddButtonToButtonGroupEditIntent(baseButtonGroupId) }
                val mutableButtonGroup by value {
                    session.mutableShow.findControl(baseButtonGroupId) as MutableButtonGroupControl
                }
                val mutableButton by value { session.mutableEditable as MutableButtonControl }

                it("creates a new empty button") {
                    expect("New Button") { mutableButton.title }
                }

                it("adds the new button to the MutableButtonGroup") {
                    expect(listOf("Pleistocene", "Holocene", "New Button")) {
                        mutableButtonGroup.buttons.map { it.title }
                    }
                }

                it("returns the new button as the MutableEditable") {
                    expect(mutableButtonGroup.buttons.last()) { session.mutableEditable }
                }

                it("is modified because a button has been added to the button group") {
                    expect(true) { editableManager.isModified() }
                }

                context("when a change has been made to the editable") {
                    beforeEachTest {
                        mutableButton.title = "My new button"
                        editableManager.onChange()
                    }

                    it("pushes the changed show with the same edit intent onto the undo stack") {
                        expect(listOf(editIntent, editIntent)) { editableManager.undoStack.stack.map { it.editIntent } }
                    }

                    context("when changes are applied") {
                        beforeEachTest {
                            editableManager.applyChanges()
                        }
                        val newShow by value { showUpdates.last() }
                        val savedButtonId by value { newShow.findControlIdByTitle("My new button") }

                        it("still has the same undo stack") {
                            expect(
                                listOf(editIntent, editIntent)
                            ) { editableManager.undoStack.stack.map { it.editIntent } }
                        }

                        it("is not modified") {
                            expect(false) { editableManager.isModified() }
                        }

                        it("has a new EditIntent which modifies the now-existing button") {
                            val newEditIntent = editableManager.session!!.editIntent
                            expect(ControlEditIntent(savedButtonId)) { newEditIntent }
                        }
                    }
                }
            }
        }
    }
})

private fun Show.findControlIdByTitle(title: String) =
    controls.entries.find { (_, v) -> v.title == title }!!.key