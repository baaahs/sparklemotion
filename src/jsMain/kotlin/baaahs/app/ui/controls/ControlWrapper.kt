package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenControl
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.useCallback
import baaahs.ui.xComponent
import external.DraggableProvided
import external.copyFrom
import kotlinx.html.js.onClickFunction
import materialui.components.card.card
import materialui.components.paper.enums.PaperStyle
import materialui.icon
import materialui.icons.Icons
import org.w3c.dom.events.Event
import react.*
import react.dom.div

val ControlWrapper = xComponent<ControlWrapperProps>("Control") { props ->
    val appContext = useContext(appContext)

    val control = props.control

    val onEditButtonClick = useCallback(control, props.controlProps) { event: Event ->
        control.getEditIntent()?.let { appContext.openEditor(it) }
        event.preventDefault()
    }

    card(Styles.controlBox on PaperStyle.root) {
        ref = props.draggableProvided.innerRef
        copyFrom(props.draggableProvided.draggableProps)

        div(+Styles.editButton) {
            attrs.onClickFunction = onEditButtonClick

            icon(Icons.Edit)
        }
        div(+Styles.dragHandle) {
            copyFrom(props.draggableProvided.dragHandleProps)
            icon(Icons.DragIndicator)
        }

        with (props.control.getRenderer(props.controlProps)) {
            render()
        }
    }
}

external interface ControlWrapperProps : RProps {
    var control: OpenControl
    var controlProps: ControlProps
    var draggableProvided: DraggableProvided
}

fun RBuilder.controlWrapper(handler: RHandler<ControlWrapperProps>): ReactElement =
    child(ControlWrapper, handler = handler)