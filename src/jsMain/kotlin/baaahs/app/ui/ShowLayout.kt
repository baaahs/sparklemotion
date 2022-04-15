package baaahs.app.ui

import baaahs.app.ui.controls.controlWrapper
import baaahs.app.ui.editor.AddControlToPanelBucket
import baaahs.client.document.EditMode
import baaahs.getBang
import baaahs.show.Layout
import baaahs.show.live.ControlDisplay
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenShow
import baaahs.ui.*
import csstype.FlexDirection
import csstype.ident
import external.Direction
import external.draggable
import external.droppable
import kotlinx.css.*
import kotlinx.js.jso
import materialui.icon
import mui.material.*
import org.w3c.dom.Element
import react.*
import react.dom.div
import react.dom.events.MouseEvent
import react.dom.events.MouseEventHandler
import react.dom.header
import styled.inlineStyles

val ShowLayout = xComponent<ShowLayoutProps>("ShowLayout") { props ->
    val appContext = useContext(appContext)

    var currentTabIndex by state { 0 }
    val currentTab = props.layout.tabs.getBounded(currentTabIndex)

    val editMode = observe(props.editMode)
    val editModeStyle = if (editMode.isOn) Styles.editModeOn else Styles.editModeOff

    val handleAddButtonClick = memo { mutableMapOf<String, MouseEventHandler<*>>() }
    var showAddMenuFor by state<ControlDisplay.PanelBuckets.PanelBucket?> { null }
    var showAddMenuForAnchorEl by state<Element?> { null }

    sharedGlContext {
        div(+Styles.showLayout) {
            if (currentTab != null) {
                val colCount = currentTab.columns.size
                val rowCount = currentTab.rows.size
                if (currentTab.areas.size != colCount * rowCount) {
                    error("Invalid layout! " +
                            "Area count (${currentTab.areas.size} != cell count " +
                            "($colCount columns * $rowCount rows)")
                }

                val areas = mutableListOf<String>()
                currentTab.rows.indices.forEach { rowIndex ->
                    val cols = mutableListOf<String>()

                    currentTab.columns.indices.forEach { columnIndex ->
                        cols.add(currentTab.areas[rowIndex * colCount + columnIndex])
                    }
                    areas.add(cols.joinToString(" ") { it.replace(" ", "") })
                }

                inlineStyles {
                    gridTemplateAreas = GridTemplateAreas(areas.joinToString(" ") { "\"$it\"" })
                    gridTemplateColumns = GridTemplateColumns(currentTab.columns.joinToString(" "))
                    gridTemplateRows = GridTemplateRows(currentTab.rows.joinToString(" "))
                }
            }

            currentTab?.areas?.distinct()?.forEach { panelId ->
                val panel = props.show.layouts.panels.getBang(panelId, "panel")
                Paper {
                    attrs.classes = jso { root = -Styles.layoutPanelPaper }
                    attrs.sx = jso {
                        gridArea = ident(panelId)
                        // TODO: panel flow direction could change here.
                        flexDirection = FlexDirection.column
                    }

                    header { +panel.title }

                    Paper {
                        attrs.classes = jso { root = -Styles.layoutPanel and editModeStyle }

                        props.controlDisplay.render(panel) { panelBucket ->
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

                                    div(+Styles.layoutControls and style) {
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
                                            icon(mui.icons.material.AddCircleOutline)
                                        }
                                    }
                                }
                            }
                        }
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

private fun <E> List<E>.getBounded(index: Int): E? {
    if (size == 0) return null
    if (index > size) return get(size - 1)
    return get(index)
}

external interface ShowLayoutProps : Props {
    var show: OpenShow
    var onShowStateChange: () -> Unit
    var layout: Layout
    var controlDisplay: ControlDisplay
    var controlProps: ControlProps
    var editMode: EditMode
}

fun RBuilder.showLayout(handler: RHandler<ShowLayoutProps>) =
    child(ShowLayout, handler = handler)
