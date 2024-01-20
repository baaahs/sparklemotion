package baaahs.app.ui.editor

import acex.*
import baaahs.app.ui.appContext
import baaahs.boundedBy
import baaahs.show.mutable.EditingShader
import baaahs.ui.addObserver
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import js.objects.jso
import kotlinx.css.left
import kotlinx.css.px
import kotlinx.css.top
import materialui.icon
import mui.material.Divider
import mui.material.ListItemText
import mui.material.Menu
import mui.material.MenuItem
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.onClick
import react.useContext
import styled.inlineStyles
import web.dom.Element
import web.events.Event
import kotlin.time.Duration.Companion.seconds

private val ShaderEditorView = xComponent<ShaderEditorProps>("ShaderEditor") { props ->
    val appContext = useContext(appContext)
    var aceEditor by state<AceEditor?> { null }
    val styles = appContext.allStyles.shaderEditor

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
                jso<Annotation> {
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

    var refactorMenuAnchor by state<Element?> { null }
    val showRefactorMenu by mouseEventHandler { event -> refactorMenuAnchor = event.target as Element? }
    val hideRefactorMenu = callback { _: Event?, _: String? -> refactorMenuAnchor = null}

    val handleExtractUniform by mouseEventHandler(shaderRefactor) {
        hideRefactorMenu(null, null)
        shaderRefactor?.onExtract()
        Unit
    }

    val handleAceEditor by handler { incoming: AceEditor ->
        later { aceEditor = incoming }
    }

    textEditor {
        attrs.document = glslDoc
        attrs.mode = Modes.glsl
        attrs.onAceEditor = handleAceEditor
        attrs.debouncePeriod = 0.25.seconds
        attrs.onChange = handleSrcChange
        attrs.onCursorChange = handleCursorChange
    }

    shaderRefactor?.selectionEndScreenPosition?.let { (x, y) ->
        div(+styles.editorActionMenuAffordance) {
            inlineStyles { top = y.px; left = x.px }
            attrs.onClick = showRefactorMenu

            icon(mui.icons.material.MoreHoriz)
        }
    }

    if (refactorMenuAnchor != null) {
        Menu {
            attrs.anchorEl = refactorMenuAnchor.asDynamic()
            attrs.anchorOrigin = jso {
                horizontal = "left"
                vertical = "top"
            }
            attrs.open = true
            attrs.onClose = hideRefactorMenu

            shaderRefactor?.let {
                it.extractionCandidate?.let { extraction ->
                    MenuItem {
                        attrs.onClick = handleExtractUniform

                        ListItemText { +"Extract ${extraction.text}…" }
                    }
                }
            }

            Divider {}

            MenuItem {
                attrs.disabled = true

                ListItemText { +"Rename…" }
            }
        }
    }
}


external interface ShaderEditorProps : Props {
    var editingShader: EditingShader
}

fun RBuilder.shaderEditor(handler: RHandler<ShaderEditorProps>) =
    child(ShaderEditorView, handler = handler)
