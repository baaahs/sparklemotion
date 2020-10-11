package baaahs.app.ui.controls

import baaahs.app.ui.AppContext
import baaahs.app.ui.ControlEditIntent
import baaahs.show.live.ControlProps
import baaahs.show.live.ControlView
import baaahs.show.live.OpenButtonControl
import baaahs.show.live.OpenControl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import materialui.toggleButton
import react.FunctionalComponent
import react.RBuilder
import react.RHandler
import react.child
import react.dom.div

class ButtonControlView(val openButtonControl: OpenButtonControl) : ControlView {
    override fun <P : ControlProps<in OpenControl>> getReactElement(): FunctionalComponent<P> {
        return Button.unsafeCast<FunctionalComponent<P>>()
    }

    override fun onEdit(appContext: AppContext) {
        appContext.openEditor(ControlEditIntent(openButtonControl.id))
    }
}

val Button = xComponent<ButtonProps>("Button") { props ->
    val buttonControl = props.control

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

            attrs["value"] = "n/a"
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

external interface ButtonProps : ControlProps<OpenButtonControl>

fun RBuilder.button(handler: RHandler<ButtonProps>) =
    child(Button, handler = handler)