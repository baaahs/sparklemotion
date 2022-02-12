package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenControl
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.DraggableProvided
import external.copyFrom
import kotlinx.html.js.onClickFunction
import materialui.components.card.card
import materialui.components.paper.enums.PaperStyle
import materialui.icon
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val ControlWrapper = xComponent<ControlWrapperProps>("Control") { props ->
    val appContext = useContext(appContext)

    val control = props.control

    val onEditButtonClick = callback(control) { event: Event ->
        control.getEditIntent()?.let { appContext.openEditor(it) }
        event.preventDefault()
    }

    card(Styles.controlBox on PaperStyle.root) {
        with (props.control.getView(props.controlProps)) {
            render()
        }

        props.draggableProvided?.let { draggableProvided ->
            ref = draggableProvided.innerRef
            copyFrom(draggableProvided.draggableProps)
        }

        problemBadge(control)

        if (!props.disableEdit) {
            div(+Styles.editButton) {
                attrs.onClickFunction = onEditButtonClick

                icon(materialui.icons.Edit)
            }
        }

        props.draggableProvided?.let { draggableProvided ->
            div(+Styles.dragHandle) {
                copyFrom(draggableProvided.dragHandleProps)
                icon(materialui.icons.DragIndicator)
            }
        }
    }
}

external interface ControlWrapperProps : Props {
    var control: OpenControl
    var controlProps: ControlProps
    var draggableProvided: DraggableProvided?
    var disableEdit: Boolean
}

fun RBuilder.controlWrapper(handler: RHandler<ControlWrapperProps>) =
    child(ControlWrapper, handler = handler)