package baaahs.util

import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

object UndoStackSpec : DescribeSpec({
    describe("UndoStack") {
        val size by value { 3 }
        val undoStack by value { UndoStack<String>(size) }
        val editor by value { FakeEditor("base", undoStack) }

        it("starts empty") {
            editor.undo().shouldBeFalse()
            editor.redo().shouldBeFalse()
        }

        context("when a change is pushed") {
            beforeEach { editor.change("first change") }

            it("can be undone") {
                editor.undo().shouldBeTrue()
                editor.contents.shouldBe("base")
                undoStack.canUndo().shouldBeFalse()
            }

            it("redo isn't available") {
                editor.redo().shouldBeFalse()
            }

            it("can be undone and redone") {
                editor.undo().shouldBeTrue()
                editor.redo().shouldBeTrue()
                editor.contents.shouldBe("first change")
                editor.redo().shouldBeFalse()
            }

            context("multiple changes") {
                beforeEach {
                    editor.change("second change")
                    editor.change("third change")
                }

                it("can be undone and redone") {
                    editor.contents.shouldBe("third change")
                    editor.undo().shouldBeTrue()
                    editor.undo().shouldBeTrue()
                    editor.undo().shouldBeTrue()
                    editor.contents.shouldBe("base")
                    editor.undo().shouldBeFalse()
                    editor.redo().shouldBeTrue()
                    editor.redo().shouldBeTrue()
                    editor.redo().shouldBeTrue()
                    editor.contents.shouldBe("third change")
                    editor.redo().shouldBeFalse()
                }

                it("changes discard redo") {
                    editor.undo().shouldBeTrue()
                    editor.undo().shouldBeTrue()
                    editor.contents.shouldBe("first change")
                    editor.change("another change")
                    editor.redo().shouldBeFalse()
                }

                context("when capacity is exceeded") {
                    beforeEach { editor.change("fourth change") }

                    it("drops eldest history") {
                        editor.undo().shouldBeTrue()
                        editor.undo().shouldBeTrue()
                        editor.undo().shouldBeTrue()
                        editor.undo().shouldBeFalse()
                        editor.contents.shouldBe("first change")
                    }
                }
            }
        }
    }
})

private class FakeEditor(initialContents: String, private val undoStack: UndoStack<String>) {
    var contents: String = initialContents
    init { undoStack.reset(initialContents) }

    fun change(newContents: String) {
        contents = newContents
        undoStack.changed(newContents)
    }

    fun undo(): Boolean {
        return if (undoStack.canUndo()) {
            contents = undoStack.undo()
            true
        } else false
    }

    fun redo(): Boolean {
        return if (undoStack.canRedo()) {
            contents = undoStack.redo()
            true
        } else false
    }
}
