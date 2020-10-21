package baaahs.app.ui.editor

import acex.AceEditor
import acex.Point
import acex.Range
import acex.Selection
import baaahs.camelize
import baaahs.gl.glsl.GlslType
import baaahs.show.mutable.EditingShader
import baaahs.ui.Prompt
import baaahs.ui.Styles
import baaahs.ui.name
import kotlinext.js.jsObject

class ShaderRefactor(
    private val editingShader: EditingShader,
    private val aceEditor: AceEditor,
    private val prompt: (prompt: Prompt) -> Unit,
    private val onChange: () -> Unit
) {
    private val editor get() = aceEditor.editor
    private val session get() = editor.session

    var extractionCandidate: ExtractionCandidate? = null
    private var glslNumberMarker: Number? = null

    fun onCursorChange(selection: Selection) {
        val session = selection.session

        val cursor = selection.getCursor()
        val line = session.getDocument().getLine(cursor.row)
        var start = cursor.column as Int
        var end = cursor.column as Int
        while (glslNumberRegex.matches(line[start - 1].toString())) start--
        while (glslNumberRegex.matches(line[end].toString())) end++
        val badCharBefore = start > 0 && glslIllegalRegex.matches(line[start - 1].toString())
        val badCharAfter = end < line.length - 1 && glslIllegalRegex.matches(line[end].toString())
        val candidate = line.substring(start, end)
        val looksLikeFloatOrInt = glslFloatOrIntRegex.matches(candidate)

        val priorExtractionCandidate = extractionCandidate

        if (badCharBefore || badCharAfter || !looksLikeFloatOrInt) {
            extractionCandidate = null
            glslNumberMarker?.let { session.removeMarker(it) }
        } else {
            val prevRange = extractionCandidate?.range
            if (prevRange == null ||
                cursor.row != prevRange.start.row ||
                start != prevRange.start.column ||
                cursor.row != prevRange.end.row ||
                end != prevRange.end.column ||
                extractionCandidate?.text != candidate
            ) {
                val range = Range(cursor.row, start, cursor.row, end)
                glslNumberMarker = session.addMarker(range, glslNumberClassName, "text", false)

                extractionCandidate = ExtractionCandidate(range, candidate)
            }
        }

        if (priorExtractionCandidate != extractionCandidate) onChange()
    }

    fun onExtract() {
        if (extractionCandidate == null) return

        prompt(Prompt(
            "Extract Input Port",
            "Enter the name of the new port.",
            fieldLabel = "Input Port Name",
            cancelButtonLabel = "Cancel",
            submitButtonLabel = "Create",
            isValid = { name ->
                if (name.isBlank()) return@Prompt "No name given."

                val portId = name.camelize()
                if (editingShader.openShader?.inputPorts?.any { it.id == portId } == true) {
                    "A port named \"$portId\" already exists."
                } else null
            },
            onSubmit = { name -> onExtractPart2(name) }
        ))
    }

    private fun onExtractPart2(portName: String) {
        val uniformName = portName.camelize()

        extractionCandidate?.let { extr ->
            session.markUndoGroup()
            val lastUniform = editor.find("uniform", jsObject {
                needle = "uniform"
                backwards = true
                caseSensitive = true
                wholeWord = "true"
            })

            session.replace(extr.range, uniformName)
            val insertionRow = lastUniform?.let { it.start.row.toInt() + 1 } ?: 0
            session.insert(
                point(insertionRow, 0),
                "uniform ${extr.type.glslLiteral} $uniformName;" +
                        " // @@Slider default=${extr.text} min=${extr.min} max=${extr.max}\n"
            )
            session.markUndoGroup()
        }
    }

    private fun point(row: Number, column: Number): Point =
        jsObject { this.row = row; this.column = column }

    companion object {
        val glslNumberRegex = Regex("[0-9.]")
        val glslIllegalRegex = Regex("[A-Za-z_]")
        val glslFloatOrIntRegex = Regex("^([0-9]+\\.[0-9]*|[0-9]*\\.[0-9]+|[0-9]+)$")
        val glslNumberClassName = Styles.glslNumber.name
    }
}

data class ExtractionCandidate(
    val range: Range,
    val text: String
) {
    val type get() = if (text.indexOf('.') > -1) GlslType.Float else GlslType.Int
    val min get() = text.toFloat() / 2f
    val max get() = text.toFloat() * 2f
}
