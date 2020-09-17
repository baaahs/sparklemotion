package baaahs.ui

import ReactAce.Ace.reactAce
import acex.AceEditor
import baaahs.show.Layout
import baaahs.show.Layouts
import baaahs.ui.Styles.previewBar
import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.json.*
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.dialog.dialog
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogcontenttext.dialogContentText
import materialui.components.dialogtitle.dialogTitle
import materialui.components.tab.tab
import materialui.components.tabs.tabs
import org.w3c.dom.events.Event
import react.*
import react.dom.a
import react.dom.code
import react.dom.p
import styled.css
import styled.styledDiv
import kotlin.browser.window

val LayoutEditorDialog = xComponent<LayoutEditorDialogProps>("LayoutEditorWindow") { props ->
    val aceEditor = ref<AceEditor>()
    val json = memo {
        Json {
            isLenient = true
            prettyPrint = true
        }
    }
    var panelNames by state {
        props.layouts.map["default"]?.rootNode?.let { getPanelNames(it) }
    }
    var errorMessage by state<String?> { null }
    val changed = ref { false }

    fun getLayoutJson(): JsonObject {
        val layoutJson = aceEditor.current.editor.session.getDocument()
        return json.parseToJsonElement(layoutJson.getValue()) as JsonObject
    }

    val checkLayout = useCallback() {
        if (changed.current) {
            errorMessage = try {
                panelNames = getPanelNames(getLayoutJson())
                null
            } catch (e: Exception) {
                e.message
            }
            changed.current = false
        }
    }

    onMount {
        val interval = window.setInterval(checkLayout, 100)
        withCleanup { window.clearInterval(interval) }
    }

    val handleApply = useCallback(props.onApply) { event: Event ->
        val layoutJson = getLayoutJson()
        val layouts = Layouts(
            getPanelNames(layoutJson),
            mapOf("default" to Layout(layoutJson))
        )
        props.onApply(layouts)
    }

    val handleClose =
        useCallback(props.onClose) { event: Event, reason: String -> props.onClose() }

    val handleCancel =
        useCallback(props.onClose) { event: Event -> props.onClose() }

    val jsonStr = props.layouts.map["default"]?.let {
        json.encodeToString(JsonElement.serializer(), it.rootNode)
    }


    dialog {
        attrs.open = props.open
        attrs.onClose = handleClose

        dialogTitle { +"Edit Layout…" }

        dialogContent {
            dialogContentText(null) {
                +"Eventually there'll be tabs that allow you to provide different layouts for phones/tablets/etc."
                p {
                    +"See "
                    a("https://github.com/nomcopter/react-mosaic") { code { +"nomcopter/react-mosaic" } }
                    +" for clues about the format."
                }
            }

            styledDiv {
                css { +previewBar }

                +(errorMessage ?: "Panels: ${panelNames}")
            }

            tabs {
                tab { attrs.label { +"Default" } }
            }

            reactAce {
                ref = aceEditor
                attrs {
                    mode = "glsl"
                    theme = "tomorrow_night_bright"
                    width = "100%"
                    height = "30vh"
                    showGutter = true
                    this.onChange = { _, _ -> changed.current = true }
                    defaultValue = jsonStr
                    name = "LayoutEditorWindow"
                    setOptions = jsObject {
                        autoScrollEditorIntoView = true
                    }
                    editorProps = jsObject {
                        `$blockScrolling` = true
                    }
                }
            }
        }

        dialogActions {
            button {
                +"Cancel"
                attrs.color = ButtonColor.secondary
                attrs.onClickFunction = handleCancel
            }
            button {
                +"Apply"
                attrs.disabled = errorMessage != null
                attrs.color = ButtonColor.primary
                attrs.onClickFunction = handleApply
            }
        }
    }
}

private fun MutableSet<String>.visitNode(json: JsonElement?) {
    when (json) {
        is JsonPrimitive -> {
            if (!this.add(json.contentOrNull ?: error("null panel name"))) {
                error("panel ${json.contentOrNull} appears twice")
            }
        }
        is JsonObject -> {
            visitNode(json["first"])
            visitNode(json["second"])
        }
        else -> error("unexpected json: $json")
    }
}

private fun getPanelNames(layoutJson: JsonElement): List<String> {
    val newPanelNames = mutableSetOf<String>()
    newPanelNames.visitNode(layoutJson)
    return newPanelNames.toList().sorted()
}

external interface LayoutEditorDialogProps : RProps {
    var open: Boolean
    var layouts: Layouts
    var onApply: (Layouts) -> Unit
    var onClose: () -> Unit
}

fun RBuilder.layoutEditorDialog(handler: RHandler<LayoutEditorDialogProps>): ReactElement =
    child(LayoutEditorDialog, handler = handler)
