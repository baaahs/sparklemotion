package baaahs.util

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object UndoStackSpec : Spek({
    describe("UndoStack") {
        val size by value { 3 }
        val undoStack by value { UndoStack<String>(size) }
        val editor by value { FakeEditor("base", undoStack) }

        it("starts empty") {
            expect(editor.undo()).toBe(false)
            expect(editor.redo()).toBe(false)
        }

        context("when a change is pushed") {
            beforeEachTest { editor.change("first change") }

            it("can be undone") {
                expect(editor.undo()).toBe(true)
                expect(editor.contents).toBe("base")
                expect(undoStack.canUndo()).toBe(false)
            }

            it("redo isn't available") {
                expect(editor.redo()).toBe(false)
            }

            it("can be undone and redone") {
                expect(editor.undo()).toBe(true)
                expect(editor.redo()).toBe(true)
                expect(editor.contents).toBe("first change")
                expect(editor.redo()).toBe(false)
            }

            context("multiple changes") {
                beforeEachTest {
                    editor.change("second change")
                    editor.change("third change")
                }

                it("can be undone and redone") {
                    expect(editor.contents).toBe("third change")
                    expect(editor.undo()).toBe(true)
                    expect(editor.undo()).toBe(true)
                    expect(editor.undo()).toBe(true)
                    expect(editor.contents).toBe("base")
                    expect(editor.undo()).toBe(false)
                    expect(editor.redo()).toBe(true)
                    expect(editor.redo()).toBe(true)
                    expect(editor.redo()).toBe(true)
                    expect(editor.contents).toBe("third change")
                    expect(editor.redo()).toBe(false)
                }

                it("changes discard redo") {
                    expect(editor.undo()).toBe(true)
                    expect(editor.undo()).toBe(true)
                    expect(editor.contents).toBe("first change")
                    editor.change("another change")
                    expect(editor.redo()).toBe(false)
                }

                context("when capacity is exceeded") {
                    beforeEachTest { editor.change("fourth change") }

                    it("drops eldest history") {
                        expect(editor.undo()).toBe(true)
                        expect(editor.undo()).toBe(true)
                        expect(editor.undo()).toBe(true)
                        expect(editor.undo()).toBe(false)
                        expect(editor.contents).toBe("first change")
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
