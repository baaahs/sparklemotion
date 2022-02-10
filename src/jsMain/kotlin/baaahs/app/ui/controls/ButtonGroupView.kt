package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.AddButtonToButtonGroupEditIntent
import baaahs.app.ui.shaderPreview
import baaahs.control.ButtonGroupControl
import baaahs.control.OpenButtonGroupControl
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenControl
import baaahs.ui.*
import external.Direction
import external.copyFrom
import external.draggable
import external.droppable
import kotlinx.html.js.onClickFunction
import materialui.components.card.card
import materialui.components.iconbutton.iconButton
import materialui.components.paper.enums.PaperStyle
import materialui.icon
import materialui.lab.components.togglebutton.enums.ToggleButtonStyle
import materialui.lab.components.togglebutton.toggleButton
import materialui.lab.components.togglebuttongroup.enums.ToggleButtonGroupOrientation
import materialui.lab.components.togglebuttongroup.enums.ToggleButtonGroupStyle
import materialui.lab.components.togglebuttongroup.toggleButtonGroup
import org.w3c.dom.events.Event
import react.*
import react.dom.div

private val ButtonGroupView = xComponent<ButtonGroupProps>("SceneList") { props ->
    val appContext = useContext(appContext)

    val buttonGroupControl = props.buttonGroupControl
    val dropTarget = props.controlProps.controlDisplay?.dropTargetFor(buttonGroupControl)

    val editMode = props.controlProps.editMode
    val onShowStateChange = props.controlProps.onShowStateChange

    val showPreview = appContext.uiSettings.renderButtonPreviews

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

    val handleEditButtonClick = callback(buttonGroupControl) { event: Event, index: Int ->
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
            buildElement {
                toggleButtonGroup(
                    ToggleButtonGroupStyle.root to buttonGroupControl.direction
                        .decode(Styles.horizontalButtonList, Styles.verticalButtonList).name
                ) {
                    install(sceneDropProvided)

                    attrs.orientation = buttonGroupControl.direction
                        .decode(ToggleButtonGroupOrientation.horizontal, ToggleButtonGroupOrientation.vertical)
                    attrs.exclusive = true
//                    attrs.value = props.selected // ... but this is busted.
//                    attrs.onChangeFunction = eventHandler { value: Int -> props.onSelect(value) }

                    buttonGroupControl.buttons.forEachIndexed { index, buttonControl ->
                        val shaderForPreview = if (showPreview) buttonControl.shaderForPreview() else null

                        draggable({
                            this.key = buttonControl.id
                            this.draggableId = buttonControl.id
                            this.isDragDisabled = !editMode
                            this.index = index
                        }) { sceneDragProvided, _ ->
//                            div {
//                                +"Handle"
                            buildElement {
                                div(+Styles.controlButton) {
                                    ref = sceneDragProvided.innerRef
                                    copyFrom(sceneDragProvided.draggableProps)

                                    problemBadge(buttonControl as OpenControl)

                                    div(+Styles.editButton) {
                                        if (editMode) {
                                            attrs.onClickFunction = { event -> handleEditButtonClick(event, index) }
                                        }

                                        icon(materialui.icons.Edit)
                                    }
                                    div(+Styles.dragHandle) {
                                        copyFrom(sceneDragProvided.dragHandleProps)
                                        icon(materialui.icons.DragIndicator)
                                    }

                                    if (shaderForPreview != null) {
                                        div(+Styles.buttonShaderPreviewContainer) {
                                            shaderPreview {
                                                attrs.shader = shaderForPreview.shader
                                            }
                                        }
                                    }

                                    toggleButton {
                                        if (showPreview) {
                                            attrs.classes(
                                                Styles.buttonLabelWhenPreview on ToggleButtonStyle.label,
                                                Styles.buttonSelectedWhenPreview on SelectedStyle.selected
                                            )
                                        }

                                        attrs.value = index.toString()
                                        attrs.selected = buttonControl.isPressed
                                        attrs.onClickFunction = {
                                            buttonGroupControl.clickOn(index)
                                            onShowStateChange()
                                        }

                                        +buttonControl.title
                                    }
//                            }
                                }
                            }
                        }

//                            }
                    }

                    child(sceneDropProvided.placeholder)

                    if (editMode) {
                        iconButton {
                            icon(materialui.icons.AddCircleOutline)
                            attrs.onClickFunction = { _: Event ->
                                appContext.openEditor(AddButtonToButtonGroupEditIntent(buttonGroupControl.id))
                            }
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

external interface ButtonGroupProps : Props {
    var controlProps: ControlProps
    var buttonGroupControl: OpenButtonGroupControl
}

fun RBuilder.buttonGroup(handler: RHandler<ButtonGroupProps>) =
    child(ButtonGroupView, handler = handler)