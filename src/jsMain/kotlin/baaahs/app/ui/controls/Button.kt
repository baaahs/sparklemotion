package baaahs.app.ui.controls

import baaahs.show.live.OpenButtonControl
import baaahs.show.live.View
import baaahs.show.mutable.MutableButtonControl
import baaahs.show.mutable.PatchHolderEditContext
import baaahs.ui.unaryPlus
import baaahs.ui.useCallback
import baaahs.ui.xComponent
import external.copyFrom
import external.draggable
import kotlinx.html.js.onClickFunction
import materialui.DragIndicator
import materialui.Edit
import materialui.icon
import materialui.toggleButton
import org.w3c.dom.events.Event
import react.RBuilder
import react.RHandler
import react.child
import react.dom.div
import react.key

class ButtonView(val openButtonControl: OpenButtonControl) : View

val Button = xComponent<ButtonProps>("Button") { props ->
    val buttonControl = props.buttonControl

    val handleEditButtonClick = useCallback(props.show) { event: Event ->
        props.show.edit {
            val mutableButtonControl = findControl(props.buttonControl.id) as MutableButtonControl
            props.editPatchHolder(PatchHolderEditContext(this@edit, mutableButtonControl))
            event.preventDefault()
        }
    }

    div(+Styles.controlButton) {
//        ref = sceneDragProvided.innerRef
//        copyFrom(sceneDragProvided.draggableProps)

//        div(+Styles.editButton) {
//            if (props.editMode) {
//                attrs.onClickFunction = { event -> handleEditButtonClick(event) }
//            }
//
//            icon(Edit)
//        }
//        div(+Styles.dragHandle) {
////            copyFrom(sceneDragProvided.dragHandleProps)
//            icon(DragIndicator)
//        }

//                            droppable({
//                                droppableId = sceneDropTargets[index].first
//                                type = "Patch"
//                                direction = Direction.vertical.name
//                                isDropDisabled = !props.editMode
//                            }) { patchDroppableProvided, _ ->
        toggleButton {
//                                    ref = patchDroppableProvided.innerRef
//                                    copyFrom(patchDroppableProvided.droppableProps)

//                attrs["value"] = index
            attrs["selected"] = buttonControl.isPressed
            attrs.onClickFunction = {
                buttonControl.click()
                props.onShowStateChange()
            }

            +buttonControl.title
        }
//                            }
    }
}

external interface ButtonProps : SpecialControlProps {
    var buttonControl: OpenButtonControl
}

fun RBuilder.button(handler: RHandler<ButtonProps>) =
    child(Button, handler = handler)