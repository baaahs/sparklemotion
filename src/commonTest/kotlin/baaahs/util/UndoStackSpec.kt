package baaahs.util

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object UndoStackSpec : Spek({
    describe("UndoStack") {
        val size by value { 3 }
        val undoStack by value { UndoStack<String>(size) }
        val editor by value { FakeEditor("base", undoStack) }

        it("starts empty") {
            expect(false) { editor.undo() }
            expect(false) { editor.redo() }
        }

        context("when a change is pushed") {
            beforeEachTest { editor.change("first change") }

            it("can be undone") {
                expect(true) { editor.undo() }
                expect("base") { editor.contents }
                expect(false) { undoStack.canUndo() }
            }

            it("redo isn't available") {
                expect(false) { editor.redo() }
            }

            it("can be undone and redone") {
                expect(true) { editor.undo() }
                expect(true) { editor.redo() }
                expect("first change") { editor.contents }
                expect(false) { editor.redo() }
            }

            context("multiple changes") {
                beforeEachTest {
                    editor.change("second change")
                    editor.change("third change")
                }

                it("can be undone and redone") {
                    expect("third change") { editor.contents }
                    expect(true) { editor.undo() }
                    expect(true) { editor.undo() }
                    expect(true) { editor.undo() }
                    expect("base") { editor.contents }
                    expect(false) { editor.undo() }
                    expect(true) { editor.redo() }
                    expect(true) { editor.redo() }
                    expect(true) { editor.redo() }
                    expect("third change") { editor.contents }
                    expect(false) { editor.redo() }
                }

                it("changes discard redo") {
                    expect(true) { editor.undo() }
                    expect(true) { editor.undo() }
                    expect("first change") { editor.contents }
                    editor.change("another change")
                    expect(false) { editor.redo() }
                }

                context("when capacity is exceeded") {
                    beforeEachTest { editor.change("fourth change") }

                    it("drops eldest history") {
                        expect(true) { editor.undo() }
                        expect(true) { editor.undo() }
                        expect(true) { editor.undo() }
                        expect(false) { editor.undo() }
                        expect("first change") { editor.contents }
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
