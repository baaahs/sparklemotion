package baaahs.app.ui.editor

import acex.*
import baaahs.app.ui.appContext
import baaahs.boundedBy
import baaahs.show.mutable.EditingShader
import baaahs.ui.Styles
import baaahs.ui.addObserver
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinext.js.jsObject
import kotlinx.css.left
import kotlinx.css.px
import kotlinx.css.top
import kotlinx.html.js.onClickFunction
import materialui.components.divider.divider
import materialui.components.listitemtext.listItemText
import materialui.components.menu.menu
import materialui.components.menuitem.menuItem
import materialui.components.popover.enums.PopoverOriginHorizontal
import materialui.components.popover.enums.PopoverOriginVertical
import materialui.components.popover.horizontal
import materialui.components.popover.vertical
import materialui.icon
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.inlineStyles

private val ShaderEditorView = xComponent<ShaderEditorProps>("ShaderEditor") { props ->
    val appContext = useContext(appContext)
    var aceEditor by state<AceEditor?> { null }
    val styles = appContext.allStyles.editor

    val glslDoc = memo(props.editingShader) {
        Document(props.editingShader.id, props.editingShader.mutableShader.src)
    }

    val lastSelection = ref<dynamic>()

    onChange("AceEditor", props.editingShader, aceEditor) {
        val editor = aceEditor?.editor ?: return@onChange

        val editingShader = props.editingShader

        // Restore selection if we have a new editor for the same shader.
        if (lastSelection.current != null) {
            editor.selection.fromJSON(lastSelection.current.unsafeCast<SavedSelection>())
        }

        fun setAnnotations() {
            val lineCount = editor.getSession().getLength().toInt()
            val annotations = editingShader.shaderBuilder.glslErrors.map { error ->
                jsObject<Annotation> {
                    row = (error.row).boundedBy(1 until lineCount) - 1
                    column = 0
                    text = error.message
                    type = "error"
                }
            }.toTypedArray()
            editor.getSession().setAnnotations(annotations)
        }
        setAnnotations()

        val compilationObserver = editingShader.addObserver {
            when (editingShader.state) {
                EditingShader.State.Building,
                EditingShader.State.Success,
                EditingShader.State.Errors -> setAnnotations()
            }
        }
        withCleanup { compilationObserver.remove() }
    }

    val shaderRefactor = memo(props.editingShader, aceEditor?.editor) {
        aceEditor?.editor?.let {
            ShaderRefactor(props.editingShader, it, appContext) { forceRender() }
        }
    }

    val handleSrcChange = memo(props.editingShader) {
        { incoming: String ->
            // Update [EditingShader].
            props.editingShader.updateSrc(incoming)
        }
    }

    val handleCursorChange = callback(shaderRefactor) { value: Any, _: Any ->
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val selection = value as Selection
        shaderRefactor?.onCursorChange(selection)
        lastSelection.current = selection.toJSON()
    }

    var refactorMenuAnchor by state<EventTarget?> { null }
    val showRefactorMenu = callback { event: Event -> refactorMenuAnchor = event.target }
    val hideRefactorMenu = callback { _: Event?, _: String? -> refactorMenuAnchor = null}

    val extractUniform = callback(shaderRefactor) { _: Event ->
        hideRefactorMenu(null, null)
        shaderRefactor?.onExtract()
        Unit
    }

    val handleAceEditor by handler { incoming: AceEditor ->
        later { aceEditor = incoming }
    }

    div(+Styles.shaderEditor) {
        textEditor {
            attrs.document = glslDoc
            attrs.mode = Modes.glsl
            attrs.onAceEditor = handleAceEditor
            attrs.debounceSeconds = 0.25f
            attrs.onChange = handleSrcChange
            attrs.onCursorChange = handleCursorChange
        }

        shaderRefactor?.selectionEndScreenPosition?.let { (x, y) ->
            div(+styles.editorActionMenuAffordance) {
                inlineStyles { top = y.px; left = x.px }
                attrs.onClickFunction = showRefactorMenu

                icon(materialui.icons.MoreHoriz)
            }
        }

        if (refactorMenuAnchor != null) {
            menu {
                attrs.anchorEl(refactorMenuAnchor)
                attrs.anchorOrigin {
                    horizontal(PopoverOriginHorizontal.left)
                    vertical(PopoverOriginVertical.top)
                }
                attrs.open = true
                attrs.onClose = hideRefactorMenu

                shaderRefactor?.let {
                    it.extractionCandidate?.let { extraction ->
                        menuItem {
                            attrs.onClickFunction = extractUniform

                            listItemText { +"Extract ${extraction.text}…" }
                        }
                    }
                }

                divider {}

                menuItem {
                    attrs.disabled = true

                    listItemText { +"Rename…" }
                }
            }
        }
    }
}


external interface ShaderEditorProps : Props {
    var editingShader: EditingShader
}

fun RBuilder.shaderEditor(handler: RHandler<ShaderEditorProps>) =
    child(ShaderEditorView, handler = handler)
