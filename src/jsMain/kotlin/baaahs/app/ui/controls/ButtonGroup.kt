package baaahs.app.ui.controls

import baaahs.app.ui.AddButtonToButtonGroupEditIntent
import baaahs.app.ui.AppContext
import baaahs.app.ui.ControlEditIntent
import baaahs.app.ui.appContext
import baaahs.show.ButtonGroupControl
import baaahs.show.live.ControlProps
import baaahs.show.live.ControlView
import baaahs.show.live.OpenButtonGroupControl
import baaahs.show.live.OpenControl
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
import react.FunctionalComponent
import react.dom.div
import react.key
import react.useContext

class ButtonGroupControlView(val openControl: OpenButtonGroupControl) : ControlView {
    override fun <P : ControlProps<in OpenControl>> getReactElement(): FunctionalComponent<P> {
        return ButtonGroup.unsafeCast<FunctionalComponent<P>>()
    }

    override fun onEdit(appContext: AppContext) {
        appContext.openEditor(ControlEditIntent(openControl.id))
    }
}

val ButtonGroup = xComponent<ButtonGroupProps>("SceneList") { props ->
    val appContext = useContext(appContext)

    val buttonGroupControl = props.control
    val dropTarget = props.controlDisplay.dropTargetFor(buttonGroupControl) as OpenButtonGroupControl.ButtonGroupDropTarget

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
    val handleEditButtonClick = useCallback { event: Event, index: Int ->
        val button = propsRef.current.control.buttons[index]
        ButtonControlView(button).onEdit(appContext)
        event.preventDefault()
    }

    card(Styles.buttonGroupCard on PaperStyle.root) {
        droppable({
            droppableId = dropTarget.dropTargetId
            type = dropTarget.type
            direction = buttonGroupControl.direction
                .decode(Direction.horizontal, Direction.vertical).name
            isDropDisabled = !props.editMode
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
                        this.isDragDisabled = !props.editMode
                        this.index = index
                    }) { sceneDragProvided, _ ->
//                            div {
//                                +"Handle"

                        div(+Styles.controlButton) {
                            ref = sceneDragProvided.innerRef
                            copyFrom(sceneDragProvided.draggableProps)

                            div(+Styles.editButton) {
                                if (props.editMode) {
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
                                        props.onShowStateChange()
                                    }

                                    +buttonControl.title
                                }
//                            }
                        }
                    }

//                            }
                }

                insertPlaceholder(sceneDropProvided)

                if (props.editMode) {
                    iconButton {
                        icon(Icons.AddCircleOutline)
                        attrs.onClickFunction = { _: Event ->
                            appContext.openEditor(AddButtonToButtonGroupEditIntent(props.control.id))
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

external interface ButtonGroupProps : ControlProps<OpenButtonGroupControl>
