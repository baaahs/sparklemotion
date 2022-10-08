package baaahs.app.ui.editor.actions

import acex.Editor
import acex.Point
import acex.Range
import acex.Selection
import baaahs.app.ui.AppContext
import baaahs.camelize
import baaahs.gl.glsl.GlslType
import baaahs.show.mutable.EditingShader
import baaahs.ui.Prompt
import kotlinx.js.jso

class ExtractUniformPlugin(
    private val editingShader: EditingShader,
    private val editor: Editor,
    private val appContext: AppContext
) : EditorPlugin {
    val session get() = editor.session

    override fun findActions(selection: Selection): List<EditorPlugin.Action> {
        val session = selection.session

        val cursor = selection.getCursor()
        val line = session.getDocument().getLine(cursor.row)
        var start = cursor.column as Int
        var end = cursor.column as Int
        var longestMatch : MatchResult? = null
        while (
            start > 0 && glslFloatOrIntRegex.find(line, start - 1)?.let {
                longestMatch = it
                console.log("matchResult:", it)
                true
            } == true
        ) start--
        longestMatch?.let {
            end = start + it.range.last - it.range.first
        }
        val badCharBefore = start > 0 && glslIllegalRegex.matches(line[start - 1].toString())
        val badCharAfter = end < line.length - 1 && glslIllegalRegex.matches(line[end].toString())
        val candidate = line.substring(start, end)
        val looksLikeFloatOrInt = glslFloatOrIntRegex.matches(candidate)

        return if (badCharBefore || badCharAfter || !looksLikeFloatOrInt) {
            emptyList()
        } else {
            val range = Range(cursor.row, start, cursor.row, end)
            listOf(ExtractUniformAction(range, candidate))
        }
    }

    private fun point(row: Number, column: Number): Point =
        jso { this.row = row; this.column = column }

    inner class ExtractUniformAction(
        override val range: Range,
        val text: String
    ) : EditorPlugin.Action {
        override val contextMenuTitle: String
            get() = "Extract ${text}…"

        val type get() = if (text.indexOf('.') > -1) GlslType.Float else GlslType.Int
        val min get() = text.toFloat() / 2f
        val max get() = text.toFloat() * 2f

        override fun perform() {
            appContext.prompt(Prompt(
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
                onSubmit = { name -> doExtract(name) }
            ))
        }

        private fun doExtract(portName: String) {
            val uniformName = portName.camelize()

            session.markUndoGroup()
            val lastUniform = editor.find("uniform", jso {
                needle = "uniform"
                backwards = true
                caseSensitive = true
                wholeWord = "true"
            })

            session.replace(range, uniformName)
            val insertionRow = lastUniform?.let { it.start.row.toInt() + 1 } ?: 0
            session.insert(
                point(insertionRow, 0),
                "uniform ${type.glslLiteral} $uniformName;" +
                        " // @@Slider default=$text min=$min max=$max\n"
            )
            session.markUndoGroup()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ExtractUniformAction) return false

            if (range != other.range) return false
            if (text != other.text) return false

            return true
        }

        override fun hashCode(): Int {
            var result = range.hashCode()
            result = 31 * result + text.hashCode()
            return result
        }
    }

    companion object {
        val glslNumberRegex = Regex("[0-9.]")
        val glslIllegalRegex = Regex("[A-Za-z_]")
        val glslFloatOrIntRegex = Regex("([0-9]+\\.[0-9]*|[0-9]*\\.[0-9]+|[0-9]+)")
    }
}