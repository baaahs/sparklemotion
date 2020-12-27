package baaahs.app.ui.editor

import acex.*
import baaahs.app.ui.appContext
import baaahs.boundedBy
import baaahs.show.mutable.EditingShader
import baaahs.ui.*
import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import org.w3c.dom.events.Event
import react.*
import react.dom.div

val ShaderEditor = xComponent<ShaderEditorProps>("ShaderEditor") { props ->
    val appContext = useContext(appContext)
    var aceEditor by state<AceEditor?> { null }

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

    val shaderRefactor = ref<ShaderRefactor?> { null }
    onChange("ShaderRefactor", props.editingShader, aceEditor) {
        shaderRefactor.current = aceEditor?.let {
            ShaderRefactor(props.editingShader, it, appContext.prompt) { forceRender() }
        }
    }

    val handleSrcChange = memo(props.editingShader) {
        { incoming: String ->
            // Update [EditingShader].
            props.editingShader.updateSrc(incoming)
        }
    }

    val handleCursorChange = useCallback { value: Any, _: Any ->
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val selection = value as Selection
        shaderRefactor.current?.onCursorChange(selection)
        Unit
    }

    val extractUniform = useCallback { _: Event ->
        shaderRefactor.current?.onExtract()
        Unit
    }

    val x = this

    div(+Styles.shaderEditor) {
        textEditor {
            attrs.document = glslDoc
            attrs.mode = Modes.glsl
            attrs.onAceEditor = x.handler("onAceEditor") { incoming: AceEditor -> aceEditor = incoming }
            attrs.debounceSeconds = 0.25f
            attrs.onChange = handleSrcChange
            attrs.onCursorChange = handleCursorChange
        }

        div(+Styles.shaderEditorActions) {
            shaderRefactor.current?.extractionCandidate?.let { extraction ->
                button {
                    attrs.variant = ButtonVariant.outlined
                    attrs.onClickFunction = extractUniform

                    +"Extract ${extraction.text}?"
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
