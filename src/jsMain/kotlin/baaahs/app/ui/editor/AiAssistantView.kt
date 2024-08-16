package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.ui.value
import baaahs.ui.withFormEvent
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import mui.material.*
import org.w3c.dom.events.Event
import react.*
import react.dom.events.ChangeEvent
import react.dom.onChange
import web.html.InputType

private val AiAssistantView = xComponent<AiAssistantProps>("AiAssistant") { props ->
    val appContext = useContext(appContext)
    val aiAssistant = appContext.webClient.aiAssistant

    var isThinking by state { false }
    var isValidRequest by state { false }
    val promptValue = ref("")

    val handleClose = callback(props.onClose) { _: Event, _: String -> props.onClose() }

    val handleChange by changeEventHandler { event: ChangeEvent<*> ->
        val newValue = event.target.value
        if (promptValue.current != newValue) {
            promptValue.current = newValue
            isValidRequest = newValue.isNotBlank()
        }
    }

    val handleCancelClick by mouseEventHandler(props.onClose) { event ->
        props.onClose()
        event.stopPropagation()
    }

    val handleSubmitClick by mouseEventHandler(props.glslSource, props.onChange, props.onClose) { event ->
        isThinking = true

        globalLaunch {
            val orig = props.glslSource
            val request = promptValue.current ?: error("Prompt must not be blank.")

            val response = aiAssistant.regenerateGlsl(null, orig, request)
            console.log(response)
            isThinking = false
            props.onChange(response.updatedSource)
            console.log(response.responseMessage)
            props.onClose()
        }

//        prompt.onSubmit(value.current!!)
        event.stopPropagation()
    }

    Dialog {
        attrs.open = true
        attrs.onClose = handleClose

        DialogTitle { +"AI Assistant" }
        DialogContent {
            TextField {
                attrs.autoFocus = true
                attrs.margin = FormControlMargin.dense
                attrs.label = buildElement {
                    if (isThinking) +"Thinking…" else +"Ask me anything"
                }
                attrs.type = InputType.text
                attrs.multiline = true
                attrs.fullWidth = true
                attrs.disabled = isThinking
                attrs.onChange = handleChange.withFormEvent()
            }
        }

        if (isThinking) {
            DialogContent {
                LinearProgress {
                    attrs.variant = LinearProgressVariant.indeterminate
                }
            }
        }

        DialogActions {
            Button {
                attrs.color = ButtonColor.primary
                attrs.onClick = handleCancelClick
                +"Cancel"
            }
            Button {
                attrs.color = ButtonColor.primary
                attrs.disabled = !isValidRequest
                attrs.onClick = handleSubmitClick
                +"Submit"
            }
        }
    }
}

external interface AiAssistantProps : Props {
    var glslSource: String
    var onChange: (incoming: String) -> Unit
    var onClose: () -> Unit
}

fun RBuilder.aiAssistant(handler: RHandler<AiAssistantProps>) =
    child(AiAssistantView, handler = handler)