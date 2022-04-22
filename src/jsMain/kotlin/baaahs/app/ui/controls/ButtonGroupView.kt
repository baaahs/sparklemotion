package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.AddButtonToButtonGroupEditIntent
import baaahs.app.ui.shaderPreview
import baaahs.control.ButtonGroupControl
import baaahs.control.OpenButtonGroupControl
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenControl
import baaahs.ui.install
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.Direction
import external.copyFrom
import external.draggable
import external.droppable
import kotlinx.css.*
import kotlinx.html.Draggable
import kotlinx.html.draggable
import kotlinx.html.js.onClickFunction
import kotlinx.js.jso
import materialui.icon
import mui.material.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import react.dom.events.MouseEvent
import styled.inlineStyles

private val ButtonGroupView = xComponent<ButtonGroupProps>("SceneList") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.showManager.editMode)

    val buttonGroupControl = props.buttonGroupControl
    val dropTarget = props.controlProps.controlDisplay?.dropTargetFor(buttonGroupControl)

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

    Card {
        attrs.classes = jso { root = -Styles.buttonGroupCard }

        div {
            inlineStyles {
                width = 60.px
                height = 60.px
                backgroundColor = Color.white
            }

            attrs.onDragEnter = { e -> console.log("dragenter", e) }
            attrs.onDragOver = { e ->
                console.log("dragover", e);
                e.preventDefault()
            }
            attrs.onDrop = { e -> console.log("drop", e) }
            attrs.onDragLeave = { e -> console.log("dragleave", e) }
        }

        div {
            inlineStyles {
                width = 60.px
                height = 60.px
                backgroundColor = Color.orange
            }

            attrs.draggable = Draggable.htmlTrue
            attrs.onDragStart = { e ->
                console.log("dragstart", e)
                e.dataTransfer.setData("Text", e.target.toString())
            }
            attrs.onDrag = { e ->
//                console.log("drag", e)
            }
            attrs.onDragEnd = { e -> console.log("dragend", e) }
        }

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
                                            attrs.onClickFunction = { event -> handleEditButtonClick(event, index) }
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

external interface ButtonGroupProps : Props {
    var controlProps: ControlProps
    var buttonGroupControl: OpenButtonGroupControl
}

fun RBuilder.buttonGroup(handler: RHandler<ButtonGroupProps>) =
    child(ButtonGroupView, handler = handler)