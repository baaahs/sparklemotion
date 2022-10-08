package baaahs.app.ui.editor.actions

import acex.Editor
import acex.Range
import acex.Selection
import baaahs.app.ui.AppContext
import baaahs.geom.Vector2I
import baaahs.show.mutable.EditingShader
import baaahs.ui.unaryPlus

interface EditorPlugin {
    fun findActions(selection: Selection): List<Action>

    interface Action {
        val contextMenuTitle: String
        val range: Range

        fun perform()
    }
}

class EditorPlugins(
    editingShader: EditingShader,
    private val editor: Editor,
    private val appContext: AppContext,
    private val onChange: () -> Unit
) {
    val plugins = listOf(
        ExtractUniformPlugin(editingShader, editor, appContext),
        RenameSymbolPlugin(editingShader, editor, appContext)
    )

    var currentActions = listOf<EditorPlugin.Action>()
        private set
    private var refactorMenuMarker: Number? = null
    var selectionEndScreenPosition: Vector2I? = null
        private set

    fun onCursorChange(selection: Selection) {
        val actions = plugins.flatMap { it.findActions(selection) }

        if (currentActions == actions) return

        refactorMenuMarker?.let { selection.session.removeMarker(it) }

        if (actions.isEmpty()) {
            refactorMenuMarker = null
            selectionEndScreenPosition = null
        } else {
            val span = Range(
                actions.minOf { it.range.start.row.toDouble() },
                actions.minOf { it.range.start.column.toDouble() },
                actions.minOf { it.range.end.row.toDouble() },
                actions.minOf { it.range.end.column.toDouble() }
            )

            refactorMenuMarker = editor.session.addMarker(
                span, +appContext.allStyles.shaderEditor.refactorMarker, "text", false)

            selectionEndScreenPosition =
                editor.renderer.textToScreenCoordinates(span.end.row, span.end.column).let {
                    Vector2I(it.pageX.toInt(), it.pageY.toInt())
                }
        }
        currentActions = actions
        onChange()
    }
}