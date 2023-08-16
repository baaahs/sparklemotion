package baaahs.app.ui.layout

import baaahs.app.ui.Styles
import baaahs.app.ui.appContext
import baaahs.app.ui.controls.controlWrapper
import baaahs.app.ui.editor.AddControlToPanelBucket
import baaahs.show.Panel
import baaahs.show.live.ControlDisplay
import baaahs.show.live.ControlProps
import baaahs.show.live.LegacyControlDisplay
import baaahs.ui.*
import web.dom.Element
import external.Direction
import external.draggable
import external.droppable
import js.core.jso
import materialui.icon
import mui.icons.material.AddCircleOutline
import mui.material.*
import react.*
import react.dom.div
import react.dom.events.MouseEvent
import react.dom.events.MouseEventHandler
import baaahs.app.ui.controls.Styles as ControlStyles

private val LegacyPanelLayoutView = xComponent<PanelLayoutProps>("PanelLayout") { props ->
    val appContext = useContext(appContext)

    val panel = props.panel
    val editMode = observe(appContext.showManager.editMode)

    val handleAddButtonClick = memo { mutableMapOf<String, MouseEventHandler<*>>() }
    var showAddMenuFor by state<LegacyControlDisplay.PanelBuckets.PanelBucket?> { null }
    var showAddMenuForAnchorEl by state<Element?> { null }


    (props.controlDisplay as LegacyControlDisplay).render(panel) { panelBucket ->
        droppable({
            this.droppableId = panelBucket.dropTargetId
            this.type = panelBucket.type
            this.direction = Direction.horizontal.name
            this.isDropDisabled = !editMode.isOn
        }) { droppableProvided, _ ->
            buildElement {
                val style = if (Styles.controlSections.size > panelBucket.section.depth)
                    Styles.controlSections[panelBucket.section.depth]
                else
                    Styles.controlSections.last()

                div(+Styles.layoutControls and style and ControlStyles.notExplicitlySized) {
                    install(droppableProvided)

                    div(+Styles.controlPanelHelpText) { +panelBucket.section.title }
                    panelBucket.controls.forEachIndexed { index, placedControl ->
                        val control = placedControl.control
                        val draggableId = control.id

                        draggable({
                            this.key = draggableId
                            this.draggableId = draggableId
                            this.isDragDisabled = !editMode.isOn
                            this.index = index
                        }) { draggableProvided, _ ->
                            buildElement {
                                controlWrapper {
                                    attrs.control = control
                                    attrs.controlProps = props.controlProps
                                    attrs.draggableProvided = draggableProvided
                                }
                            }
                        }
                    }

                    child(droppableProvided.placeholder)

                    IconButton {
                        attrs.classes = jso { root = -Styles.addToSectionButton }
                        attrs.onClick = handleAddButtonClick.getOrPut(panelBucket.suggestId()) {
                            { event: MouseEvent<*, *> ->
                                showAddMenuFor = panelBucket
                                showAddMenuForAnchorEl = event.currentTarget
                            }
                        }
                        icon(AddCircleOutline)
                    }
                }
            }
        }
    }

    showAddMenuFor?.let { panelBucket ->
        Menu {
            attrs.anchorEl = showAddMenuForAnchorEl.asDynamic()
            attrs.open = true
            attrs.onClose = { showAddMenuFor = null }

            appContext.plugins.addControlMenuItems.forEach { addControlMenuItem ->
                MenuItem {
                    attrs.onClick = {
                        val editIntent = AddControlToPanelBucket(panelBucket, addControlMenuItem.createControlFn)
                        appContext.openEditor(editIntent)
                        showAddMenuFor = null
                    }

                    ListItemIcon { icon(addControlMenuItem.icon) }
                    ListItemText { +addControlMenuItem.label }
                }
            }
        }
    }
}

external interface PanelLayoutProps : Props {
    var panel: Panel
    var controlDisplay: ControlDisplay
    var controlProps: ControlProps
}

fun RBuilder.legacyPanelLayout(handler: RHandler<PanelLayoutProps>) =
    child(LegacyPanelLayoutView, handler = handler)