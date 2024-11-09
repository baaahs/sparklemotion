package baaahs.app.ui.editor

import baaahs.control.ButtonControl
import baaahs.control.MutableButtonControl
import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testToolchain
import baaahs.kotest.value
import baaahs.show.SampleData
import baaahs.show.Show
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShow
import baaahs.ui.addObserver
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.booleans.*
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.assertSame
import kotlin.test.assertTrue

object EditableManagerSpec : DescribeSpec({
    describe<ShowEditableManager> {
        val showUpdates by value { arrayListOf<Show>() }
        val editableManager by value {
            ShowEditableManager { showUpdates.add(it) }
        }
        val facadeUpdates by value { arrayListOf<String>() }

        beforeEach {
            editableManager.addObserver { facadeUpdates.add("updated") }
        }

        context("when there's no active session") {
            it("isEditing() is false") {
                editableManager.isEditing().shouldBeFalse()
            }

            it("editorPanels is empty") {
                editableManager.dialogPanels.shouldBeEmpty()
            }
        }

        context("when there's an active session") {
            val baseShow by value { SampleData.sampleShow }
            val editIntent by value<EditIntent> { ShowEditIntent() }
            val session: EditableManager<Show>.Session by value { editableManager.session!! }

            beforeEach {
                editableManager.openEditor(baseShow, editIntent, testToolchain)
            }

            it("isEditing() is true") {
                editableManager.isEditing().shouldBeTrue()
            }

            it("editorPanels comes from the show") {
                editableManager.dialogPanels.map { it.title }
                    .shouldBe(arrayListOf("Properties", "Patches"))
            }

            it("selectedPanel defaults to the first") {
                editableManager.selectedPanel?.title.shouldBe("Properties")
            }

            it("creates a MutableShow") {
                editableManager.session!!.mutableDocument.build().shouldBe(baseShow)
            }

            it("finds the relevant MutableEditable") {
                assertSame(session.mutableDocument, session.mutableEditable)
            }

            it("pushes it onto the undo stack") {
                assertSame(baseShow, editableManager.undoStack.stack.first().document)
            }

            it("is not modified") {
                editableManager.isModified().shouldBeFalse()
            }

            context("when a change has been made to the editable") {
                beforeEach {
                    (session.mutableEditable as MutablePatchHolder).title = "different title"
                    editableManager.onChange()
                }

                it("pushes the changed show onto the undo stack") {
                    editableManager.undoStack.stack.map { it.document.title }
                        .shouldContainExactly("Sample Show", "different title")
                }

                it("is modified") {
                    editableManager.isModified().shouldBeTrue()
                }

                context("when changes are applied") {
                    var priorMutableShow: MutableShow? = null

                    beforeEach {
                        priorMutableShow = session.mutableDocument as MutableShow
                        editableManager.applyChanges()
                    }

                    it("calls onApply callback with new show") {
                        showUpdates.map { it.title }.shouldContainExactly("different title")
                    }

                    it("still has the same undo stack") {
                        editableManager.undoStack.stack.map { it.document.title }
                            .shouldContainExactly("Sample Show", "different title")
                    }

                    it("is not modified") {
                        editableManager.isModified().shouldBeFalse()
                    }

                    it("has a different MutableShow") {
                        assertTrue { priorMutableShow !== editableManager.session!!.mutableDocument }
                    }

                    context("and user clicks Undo") {
                        beforeEach {
                            editableManager.undo()
                        }

                        it("is modified") {
                            editableManager.isModified().shouldBeTrue()
                        }

                        context("and user clicks Redo") {
                            beforeEach {
                                editableManager.redo()
                            }

                            it("is not modified") {
                                editableManager.isModified().shouldBeFalse()
                            }
                        }
                    }
                }
            }

            context("when the EditIntent creates a new object") {
                val buttonGroupTitle by value { "Backdrops" }

                override(editIntent) {
                    val editor = object : Editor<MutableIGridLayout> {
                        override val title: String
                            get() = TODO("not implemented")
                        override fun edit(mutableShow: MutableShow, block: MutableIGridLayout.() -> Unit) {
                            mutableShow.findGridItem("default", "Main", buttonGroupTitle)
                                .layout!!.block()
                        }
                        override fun delete(mutableShow: MutableShow) {}
                    }
                    AddControlToGrid(editor, 1, 1, 1, 1) {
                        ButtonControl("New Button").createMutable(it)
                    }
                }
                val mutableButtonGroupGridItem by value {
                    (session.mutableDocument as MutableShow).findGridItem("default", "Main", buttonGroupTitle)
                }
                val mutableButton by value { session.mutableEditable as MutableButtonControl }

                it("creates a new empty button") {
                    mutableButton.title.shouldBe("New Button")
                }

                it("adds the new button to the MutableButtonGroup") {
                    mutableButtonGroupGridItem.layout!!.items.map { it.control.title }
                        .shouldContainExactly("Red Yellow Green", "Fire", "Blue Aqua Green", "Checkerboard", "New Button")
                }

                it("returns the new button as the MutableEditable") {
                    session.mutableEditable.shouldBe(mutableButtonGroupGridItem.layout!!.find("New Button").control)
                }

                it("is modified because a button has been added to the button group") {
                    editableManager.isModified().shouldBeTrue()
                }

                context("when a change has been made to the editable") {
                    beforeEach {
                        mutableButton.title = "My new button"
                        editableManager.onChange()
                    }

                    it("pushes the changed show with the same edit intent onto the undo stack") {
                        editableManager.undoStack.stack.map { it.editIntent }
                            .shouldContainExactly(editIntent, editIntent)
                    }

                    context("when changes are applied") {
                        beforeEach {
                            editableManager.applyChanges()
                        }
                        val newShow by value { showUpdates.last() }
                        val savedButtonId by value { newShow.findControlIdByTitle("My new button") }

                        it("still has the same undo stack") {
                            editableManager.undoStack.stack.map { it.editIntent }
                                .shouldContainExactly(editIntent, editIntent)
                        }

                        it("is not modified") {
                            editableManager.isModified().shouldBeFalse()
                        }

                        it("has a new EditIntent which modifies the now-existing button") {
                            val newEditIntent = editableManager.session!!.editIntent
                            newEditIntent.shouldBe(ControlEditIntent(savedButtonId))
                        }
                    }
                }
            }
        }
    }
})

private fun Show.findControlIdByTitle(title: String) =
    controls.entries.find { (_, v) -> v.title == title }?.key
        ?: error("No control with title $title among [${controls.entries.joinToString(", ") { it.value.title }}]")