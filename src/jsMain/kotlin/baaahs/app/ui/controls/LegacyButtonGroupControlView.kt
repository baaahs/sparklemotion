package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.AddButtonToButtonGroupEditIntent
import baaahs.app.ui.shaderPreview
import baaahs.control.ButtonGroupControl
import baaahs.control.OpenButtonGroupControl
import baaahs.show.live.ControlProps
import baaahs.show.live.LegacyControlDisplay
import baaahs.show.live.OpenControl
import baaahs.ui.install
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import dom.html.HTMLElement
import external.Direction
import external.copyFrom
import external.draggable
import external.droppable
import kotlinx.js.jso
import materialui.icon
import mui.material.*
import react.*
import react.dom.div
import react.dom.events.MouseEvent
import react.dom.onClick

private val LegacyButtonGroupControlView = xComponent<LegacyButtonGroupProps>("LegacyButtonControlGroup") { props ->
    val appContext = useContext(appContext)
    val controlStyles = appContext.allStyles.controls
    val editMode = observe(appContext.showManager.editMode)

    val buttonGroupControl = props.buttonGroupControl
    val controlDisplay = props.controlProps.controlDisplay as LegacyControlDisplay?
    val dropTarget = controlDisplay?.dropTargetFor(buttonGroupControl)

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

    val handleEditButtonClick = callback(buttonGroupControl) { event: MouseEvent<*, *>, index: Int ->
        val button = buttonGroupControl.buttons[index]
        button.getEditIntent()?.let { appContext.openEditor(it) }
        event.preventDefault()
    }

    Card {
        attrs.classes = jso { root = -controlStyles.buttonGroupCard }

        droppable({
            if (dropTarget != null) {
                droppableId = dropTarget.dropTargetId
                type = dropTarget.type
            } else {
                isDropDisabled = true
            }
            direction = buttonGroupControl.direction
                .decode(Direction.horizontal, Direction.vertical).name
            isDropDisabled = !editMode.isOn
        }) { sceneDropProvided, _ ->
            buildElement {
                ToggleButtonGroup {
                    attrs.classes = jso {
                        root = -buttonGroupControl.direction
                            .decode(Styles.horizontalButtonList, Styles.verticalButtonList)
                    }
                    attrs.color = ToggleButtonGroupColor.primary
                    attrs.value = "n/a"

                    install(sceneDropProvided)

                    attrs.orientation = buttonGroupControl.direction
                        .decode(Orientation.horizontal, Orientation.vertical)
                    attrs.exclusive = true
//                    attrs.value = props.selected // ... but this is busted.
//                    attrs.onChangeFunction = eventHandler { value: Int -> props.onSelect(value) }

                    buttonGroupControl.buttons.forEachIndexed { index, buttonControl ->
                        val shaderForPreview = if (showPreview) buttonControl.shaderForPreview() else null

                        draggable({
                            this.key = buttonControl.id
                            this.draggableId = buttonControl.id
                            this.isDragDisabled = !editMode.isOn
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
                                        if (editMode.isOn) {
                                            attrs.onClick = { event -> handleEditButtonClick(event, index) }
                                        }

                                        icon(mui.icons.material.Edit)
                                    }
                                    div(+Styles.dragHandle) {
                                        copyFrom(sceneDragProvided.dragHandleProps)
                                        icon(mui.icons.material.DragIndicator)
                                    }

                                    if (shaderForPreview != null) {
                                        div(+Styles.buttonShaderPreviewContainer) {
                                            shaderPreview {
                                                attrs.shader = shaderForPreview.shader
                                            }
                                        }
                                    }

                                    ToggleButton {
                                        if (showPreview) {
                                            attrs.classes = jso {
                                                root = -Styles.buttonLabelWhenPreview
                                                selected = -Styles.buttonSelectedWhenPreview
                                            }
                                        }

                                        attrs.value = index.toString()
                                        attrs.selected = buttonControl.isPressed
                                        attrs.onClick = { _: MouseEvent<HTMLElement, *>, _: dynamic ->
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

                    if (editMode.isOn) {
                        IconButton {
                            icon(mui.icons.material.AddCircleOutline)
                            attrs.onClick = {
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

external interface LegacyButtonGroupProps : Props {
    var controlProps: ControlProps
    var buttonGroupControl: OpenButtonGroupControl
}

fun RBuilder.legacyButtonGroupControl(handler: RHandler<LegacyButtonGroupProps>) =
    child(LegacyButtonGroupControlView, handler = handler)