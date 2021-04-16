package baaahs.app.ui.editor

import baaahs.app.ui.AddButtonToButtonGroupEditIntent
import baaahs.app.ui.ControlEditIntent
import baaahs.app.ui.EditIntent
import baaahs.app.ui.ShowEditIntent
import baaahs.control.MutableButtonControl
import baaahs.control.MutableButtonGroupControl
import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testToolchain
import baaahs.show.SampleData
import baaahs.show.Show
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShow
import baaahs.ui.addObserver
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import kotlin.test.assertSame
import kotlin.test.assertTrue

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
                expect(editableManager.isEditing()).toBe(false)
            }

            it("editorPanels is empty") {
                expect(editableManager.editorPanels).isEmpty()
            }
        }

        context("when there's an active session") {
            val baseShow by value { SampleData.sampleShow }
            val editIntent by value<EditIntent> { ShowEditIntent() }
            val session: EditableManager.Session by value { editableManager.session!! }

            beforeEachTest {
                editableManager.openEditor(baseShow, editIntent, testToolchain)
            }

            it("isEditing() is true") {
                expect(editableManager.isEditing()).toBe(true)
            }

            it("editorPanels comes from the show") {
                expect(editableManager.editorPanels.map { it.title })
                    .toBe(arrayListOf("Properties", "Patches"))
            }

            it("selectedPanel defaults to the first") {
                expect(editableManager.selectedPanel?.title).toBe("Properties")
            }

            it("creates a MutableShow") {
                expect(editableManager.session!!.mutableShow.getShow()).toBe(baseShow)
            }

            it("finds the relevant MutableEditable") {
                assertSame(session.mutableShow, session.mutableEditable)
            }

            it("pushes it onto the undo stack") {
                assertSame(baseShow, editableManager.undoStack.stack.first().show)
            }

            it("is not modified") {
                expect(editableManager.isModified()).toBe(false)
            }

            context("when a change has been made to the editable") {
                beforeEachTest {
                    (session.mutableEditable as MutablePatchHolder).title = "different title"
                    editableManager.onChange()
                }

                it("pushes the changed show onto the undo stack") {
                    expect(editableManager.undoStack.stack.map { it.show.title })
                        .containsExactly("Sample Show", "different title")
                }

                it("is modified") {
                    expect(editableManager.isModified()).toBe(true)
                }

                context("when changes are applied") {
                    var priorMutableShow: MutableShow? = null

                    beforeEachTest {
                        priorMutableShow = session.mutableShow
                        editableManager.applyChanges()
                    }

                    it("calls onApply callback with new show") {
                        expect(showUpdates.map { it.title }).containsExactly("different title")
                    }

                    it("still has the same undo stack") {
                        expect(editableManager.undoStack.stack.map { it.show.title })
                            .containsExactly("Sample Show","different title")
                    }

                    it("is not modified") {
                        expect(editableManager.isModified()).toBe(false)
                    }

                    it("has a different MutableShow") {
                        assertTrue { priorMutableShow !== editableManager.session!!.mutableShow }
                    }

                    context("and user clicks Undo") {
                        beforeEachTest {
                            editableManager.undo()
                        }

                        it("is modified") {
                            expect(editableManager.isModified()).toBe(true)
                        }

                        context("and user clicks Redo") {
                            beforeEachTest {
                                editableManager.redo()
                            }

                            it("is not modified") {
                                expect(editableManager.isModified()).toBe(false)
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
                    expect(mutableButton.title).toBe("New Button")
                }

                it("adds the new button to the MutableButtonGroup") {
                    expect(mutableButtonGroup.buttons.map { it.title })
                        .containsExactly("Pleistocene","Holocene","New Button")
                }

                it("returns the new button as the MutableEditable") {
                    expect(session.mutableEditable).toBe(mutableButtonGroup.buttons.last())
                }

                it("is modified because a button has been added to the button group") {
                    expect(editableManager.isModified()).toBe(true)
                }

                context("when a change has been made to the editable") {
                    beforeEachTest {
                        mutableButton.title = "My new button"
                        editableManager.onChange()
                    }

                    it("pushes the changed show with the same edit intent onto the undo stack") {
                        expect(editableManager.undoStack.stack.map { it.editIntent })
                            .containsExactly(editIntent,editIntent)
                    }

                    context("when changes are applied") {
                        beforeEachTest {
                            editableManager.applyChanges()
                        }
                        val newShow by value { showUpdates.last() }
                        val savedButtonId by value { newShow.findControlIdByTitle("My new button") }

                        it("still has the same undo stack") {
                            expect(editableManager.undoStack.stack.map { it.editIntent })
                                .containsExactly(editIntent,editIntent)
                        }

                        it("is not modified") {
                            expect(editableManager.isModified()).toBe(false)
                        }

                        it("has a new EditIntent which modifies the now-existing button") {
                            val newEditIntent = editableManager.session!!.editIntent
                            expect(newEditIntent).toBe(ControlEditIntent(savedButtonId))
                        }
                    }
                }
            }
        }
    }
})

private fun Show.findControlIdByTitle(title: String) =
    controls.entries.find { (_, v) -> v.title == title }!!.key