package baaahs.app.ui.controls

import baaahs.app.ui.AddButtonToButtonGroupEditIntent
import baaahs.app.ui.appContext
import baaahs.show.ButtonGroupControl
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenButtonGroupControl
import baaahs.ui.*
import external.Direction
import external.copyFrom
import external.draggable
import external.droppable
import kotlinx.html.js.onClickFunction
import materialui.ToggleButtonGroupStyle
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttongroup.enums.ButtonGroupOrientation
import materialui.components.card.card
import materialui.components.iconbutton.iconButton
import materialui.components.paper.enums.PaperStyle
import materialui.icon
import materialui.icons.Icons
import materialui.toggleButton
import materialui.toggleButtonGroup
import org.w3c.dom.events.Event
import react.*
import react.dom.div

private val ButtonGroup = xComponent<ButtonGroupProps>("SceneList") { props ->
    val appContext = useContext(appContext)

    val buttonGroupControl = props.buttonGroupControl
    val dropTarget = props.controlProps.controlDisplay?.dropTargetFor(buttonGroupControl)

    val editMode = props.controlProps.editMode
    val onShowStateChange = props.controlProps.onShowStateChange

//    val sceneDropTargets = props.show.scenes.mapIndexed { index, _ ->
//        val sceneDropTarget = SceneDropTarget(props.show, index)
//        val sceneDropTargetId = appContext.dragNDrop.addDropTarget(sceneDropTarget)
//        sceneDropTargetId to sceneDropTarget as DropTarget
//    }
//    onChange("unregister drop target") {
//        withCleanup {
//            sceneDropTargets.forEach { (_, sceneDropTarget) ->
//                appContext.dragNDrop.removeDropTarget(sceneDropTarget)
//            }
//        }
//    }

    val propsRef = ref { props }
    propsRef.current = props
    val handleEditButtonClick = useCallback(buttonGroupControl) { event: Event, index: Int ->
        val button = buttonGroupControl.buttons[index]
        button.getEditIntent()?.let { appContext.openEditor(it) }
        event.preventDefault()
    }

    card(Styles.buttonGroupCard on PaperStyle.root) {
        droppable({
            if (dropTarget != null) {
                droppableId = dropTarget.dropTargetId
                type = dropTarget.type
            } else {
                isDropDisabled = true
            }
            direction = buttonGroupControl.direction
                .decode(Direction.horizontal, Direction.vertical).name
            isDropDisabled = !editMode
        }) { sceneDropProvided, _ ->
            toggleButtonGroup(
                ToggleButtonGroupStyle.root to buttonGroupControl.direction
                    .decode(Styles.horizontalButtonList, Styles.verticalButtonList).name
            ) {
                install(sceneDropProvided)

                attrs.variant = ButtonVariant.outlined
                attrs.orientation = buttonGroupControl.direction
                    .decode(ButtonGroupOrientation.horizontal, ButtonGroupOrientation.vertical)
                attrs["exclusive"] = true
//                    attrs["value"] = props.selected // ... but this is busted.
//                    attrs.onChangeFunction = eventHandler { value: Int -> props.onSelect(value) }

                buttonGroupControl.buttons.forEachIndexed { index, buttonControl ->
                    draggable({
                        this.key = buttonControl.id
                        this.draggableId = buttonControl.id
                        this.isDragDisabled = !editMode
                        this.index = index
                    }) { sceneDragProvided, _ ->
//                            div {
//                                +"Handle"

                        div(+Styles.controlButton) {
                            ref = sceneDragProvided.innerRef
                            copyFrom(sceneDragProvided.draggableProps)

                            div(+Styles.editButton) {
                                if (editMode) {
                                    attrs.onClickFunction = { event -> handleEditButtonClick(event, index) }
                                }

                                icon(Icons.Edit)
                            }
                            div(+Styles.dragHandle) {
                                copyFrom(sceneDragProvided.dragHandleProps)
                                icon(Icons.DragIndicator)
                            }

//                            droppable({
//                                droppableId = sceneDropTargets[index].first
//                                type = "Patch"
//                                direction = Direction.vertical.name
//                                isDropDisabled = !props.editMode
//                            }) { patchDroppableProvided, _ ->
                                toggleButton {
//                                    ref = patchDroppableProvided.innerRef
//                                    copyFrom(patchDroppableProvided.droppableProps)

                                    attrs["value"] = index
                                    attrs["selected"] = buttonControl.isPressed
                                    attrs.onClickFunction = {
                                        buttonGroupControl.clickOn(index)
                                        onShowStateChange()
                                    }

                                    +buttonControl.title
                                }
//                            }
                        }
                    }

//                            }
                }

                insertPlaceholder(sceneDropProvided)

                if (editMode) {
                    iconButton {
                        icon(Icons.AddCircleOutline)
                        attrs.onClickFunction = { _: Event ->
                            appContext.openEditor(AddButtonToButtonGroupEditIntent(buttonGroupControl.id))
                        }
                    }
                }
            }
        }
    }
}

private fun <T> ButtonGroupControl.Direction.decode(horizontal: T, vertical: T): T {
    return when (this) {
        ButtonGroupControl.Direction.Horizontal -> horizontal
        ButtonGroupControl.Direction.Vertical -> vertical
    }
}

external interface ButtonGroupProps : RProps {
    var controlProps: ControlProps
    var buttonGroupControl: OpenButtonGroupControl
}

fun RBuilder.buttonGroup(handler: RHandler<ButtonGroupProps>) =
    child(ButtonGroup, handler = handler)