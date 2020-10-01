package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.show.live.OpenControl
import baaahs.show.live.getViewFor
import baaahs.ui.copyInto
import baaahs.ui.unaryPlus
import baaahs.ui.useCallback
import baaahs.ui.xComponent
import external.DraggableProvided
import external.copyFrom
import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import materialui.icon
import materialui.icons.Icons
import org.w3c.dom.events.Event
import react.*
import react.dom.div

val ControlWrapper = xComponent<ControlWrapperProps>("Control") { props ->
    val appContext = useContext(appContext)

    val control = props.control
    val controlView = memo(control) { getViewFor(control) }

    val onEditButtonClick = useCallback(control, props.genericControlProps) { event: Event ->
        controlView.onEdit(appContext)
        event.preventDefault()
    }

    div(+Styles.controlBox) {
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

        child(controlView.getReactElement(), jsObject {
            props.genericControlProps.copyInto(this)
            this.control = props.control
        })
    }
}

external interface ControlWrapperProps : RProps {
    var control: OpenControl
    var genericControlProps: GenericControlProps
    var draggableProvided: DraggableProvided
}

fun RBuilder.controlWrapper(handler: RHandler<ControlWrapperProps>): ReactElement =
    child(ControlWrapper, handler = handler)