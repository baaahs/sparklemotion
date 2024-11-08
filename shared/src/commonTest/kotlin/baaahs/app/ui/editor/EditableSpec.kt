package baaahs.app.ui.editor

import baaahs.control.MutableButtonControl
import baaahs.describe
import baaahs.gl.testToolchain
import baaahs.kotest.value
import baaahs.show.Panel
import baaahs.show.mutable.MutablePanel
import baaahs.show.mutable.MutableShow
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

object EditableSpec : DescribeSpec({
    describe<ControlEditIntent> {
        val baseShow by value {
            MutableShow("test show") {
                val mainPanel = MutablePanel(Panel("Main"))
                editLayouts { panels["main"] = mainPanel }
                addButton(mainPanel, "main button") { }
            }.getShow()

                .also { println("it = ${it}") }
        }
        val editIntent by value { ControlEditIntent("mainButton") }
        val editableManager by value {
            ShowEditableManager { }
                .apply { openEditor(baseShow, editIntent, testToolchain) }
        }
        val mutableButton by value {
            editableManager.session!!.mutableEditable as MutableButtonControl
        }

        context("when the id of its control changes") {
            beforeEach {
                mutableButton.title = "new title for button"
                editableManager.onChange()
                editableManager.undo()
                editableManager.redo()
            }

            it("provides an appropriate refreshed edit intent for the undo stack") {
                val newEditIntent = (editableManager.session!!.editIntent as ControlEditIntent)
                expect(newEditIntent.controlId).toBe("newTitleForButton")
            }
        }
    }
})