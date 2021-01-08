package baaahs.app.ui.editor

import acex.*
import baaahs.app.ui.appContext
import baaahs.boundedBy
import baaahs.show.mutable.EditingShader
import baaahs.ui.*
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
import materialui.icons.Icons
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.*
import react.dom.div
import styled.inlineStyles

val ShaderEditor = xComponent<ShaderEditorProps>("ShaderEditor") { props ->
    val appContext = useContext(appContext)
    var aceEditor by state<AceEditor?> { null }
    val styles = appContext.allStyles.appUiEditor

    val glslDoc = memo(props.editingShader) {
        Document(props.editingShader.id, props.editingShader.mutableShader.src)
    }

    onChange("AceEditor", props.editingShader, aceEditor) {
        val editor = aceEditor?.editor ?: return@onChange

        val editingShader = props.editingShader

        val compilationObserver = editingShader.addObserver {
            fun setAnnotations(list: List<Annotation>) {
                editor.getSession().setAnnotations(list.toTypedArray())
            }
            when (editingShader.state) {
                EditingShader.State.Building,
                EditingShader.State.Success,
                EditingShader.State.Errors -> {
                    val lineCount = editor.getSession().getLength().toInt()
                    setAnnotations(editingShader.shaderBuilder.glslErrors.map { error ->
                        jsObject {
                            row = (error.row).boundedBy(1 until lineCount) - 1
                            column = 0
                            text = error.message
                            type = "error"
                        }
                    })
                }
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

    val handleCursorChange = useCallback(shaderRefactor) { value: Any, _: Any ->
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val selection = value as Selection
        shaderRefactor?.onCursorChange(selection)
        Unit
    }

    var refactorMenuAnchor by state<EventTarget?> { null }
    val showRefactorMenu = useCallback { event: Event -> refactorMenuAnchor = event.target }
    val hideRefactorMenu = useCallback { _: Event?, _: String? -> refactorMenuAnchor = null}

    val extractUniform = useCallback(shaderRefactor) { _: Event ->
        hideRefactorMenu(null, null)
        shaderRefactor?.onExtract()
        Unit
    }

    val x = this

    div(+Styles.shaderEditor) {
        textEditor {
            attrs.document = glslDoc
            attrs.mode = Modes.glsl
            attrs.onAceEditor = x.handler("onAceEditor") { incoming: AceEditor ->
                x.later { aceEditor = incoming }
            }
            attrs.debounceSeconds = 0.25f
            attrs.onChange = handleSrcChange
            attrs.onCursorChange = handleCursorChange
        }

        shaderRefactor?.selectionEndScreenPosition?.let { (x, y) ->
            div(+styles.editorActionMenuAffordance) {
                inlineStyles { top = y.px; left = x.px }
                attrs.onClickFunction = showRefactorMenu

                icon(Icons.MoreHoriz)
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


external interface ShaderEditorProps : RProps {
    var editingShader: EditingShader
}

fun RBuilder.shaderEditor(handler: RHandler<ShaderEditorProps>) =
    child(ShaderEditor, handler = handler)
