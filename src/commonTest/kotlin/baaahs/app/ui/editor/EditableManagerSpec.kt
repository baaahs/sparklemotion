package baaahs.app.ui.editor

import baaahs.app.ui.ShowEditIntent
import baaahs.show.SampleData
import baaahs.show.Show
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShow
import baaahs.ui.addObserver
import describe
import org.spekframework.spek2.Spek
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
            val editIntent by value { ShowEditIntent() }
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
                assertTrue { session.mutableShow === session.mutableEditable }
            }

            it("pushes it onto the undo stack") {
                assertTrue { baseShow === editableManager.undoStack.stack.first() }
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
                    ) { editableManager.undoStack.stack.map { it.title } }
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
                        ) { editableManager.undoStack.stack.map { it.title } }
                    }

                    it("is not modified") {
                        expect(false) { editableManager.isModified() }
                    }

                    it("has a different MutableShow") {
                        assertTrue { priorMutableShow !== editableManager.session!!.mutableShow }
                    }
                }
            }
        }
    }
})