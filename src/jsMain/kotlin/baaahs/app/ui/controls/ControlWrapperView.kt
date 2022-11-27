package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenControl
import baaahs.ui.className
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.DraggableProvided
import external.copyFrom
import kotlinx.js.jso
import materialui.icon
import mui.material.Card
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.onClick
import react.useContext

private val ControlWrapper = xComponent<ControlWrapperProps>("Control") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.showManager.editMode)

    val control = props.control

    val handleEditButtonClick by mouseEventHandler(control) { e ->
        control.getEditIntent()?.let { appContext.openEditor(it) }
        e.preventDefault()
    }

    Card {
        attrs.classes = jso { root = props.className?.className ?: -Styles.controlBox }
        with (props.control.getView(props.controlProps)) {
            render()
        }

        props.draggableProvided?.let { draggableProvided ->
            ref = draggableProvided.innerRef
            copyFrom(draggableProvided.draggableProps)
        }

        problemBadge(control)

        if (editMode.isAvailable && !props.disableEdit) {
            div(+Styles.editButton) {
                attrs.onClick = handleEditButtonClick

                icon(mui.icons.material.Edit)
            }
        }

        props.draggableProvided?.let { draggableProvided ->
            div(+Styles.dragHandle) {
                copyFrom(draggableProvided.dragHandleProps)
                icon(mui.icons.material.DragIndicator)
            }
        }
    }
}

external interface ControlWrapperProps : Props {
    var control: OpenControl
    var controlProps: ControlProps
    var draggableProvided: DraggableProvided?
    var disableEdit: Boolean
    var className: String?
}

fun RBuilder.controlWrapper(handler: RHandler<ControlWrapperProps>) =
    child(ControlWrapper, handler = handler)