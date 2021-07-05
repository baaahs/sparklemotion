package baaahs.app.ui

import baaahs.app.ui.controls.controlWrapper
import baaahs.getBang
import baaahs.gl.SharedGlContext
import baaahs.show.Layout
import baaahs.show.live.ControlDisplay
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenShow
import baaahs.ui.*
import external.Direction
import external.draggable
import external.droppable
import external.mosaic.Mosaic
import external.mosaic.MosaicControlledProps
import external.mosaic.MosaicWindow
import external.mosaic.MosaicWindowProps
import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import materialui.components.iconbutton.enums.IconButtonStyle
import materialui.components.iconbutton.iconButton
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.menu.menu
import materialui.components.menuitem.menuItem
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import materialui.icon
import materialui.icons.Icons
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.*
import react.dom.div
import react.dom.header
import styled.inlineStyles
import kotlin.reflect.KClass

val ShowLayout = xComponent<ShowLayoutProps>("ShowLayout") { props ->
    val appContext = useContext(appContext)

    var currentTabIndex by state { 0 }
    val currentTab = props.layout.tabs.getBounded(currentTabIndex)

    val editModeStyle =
        if (props.editMode) Styles.editModeOn else Styles.editModeOff

    val handleAddButtonClick = memo { mutableMapOf<String, (Event) -> Unit>() }
    var showAddMenuFor by state<ControlDisplay.PanelBuckets.PanelBucket?> { null }
    var showAddMenuForAnchorEl by state<EventTarget?> { null }

    val canvasParentRef = ref<HTMLElement?> { null }
    val appGlContext = memo {
        jsObject<AppGlContext> {
            this.sharedGlContext = SharedGlContext()
        }
    }
    val sharedGlContext = appGlContext.sharedGlContext!!

    onMount {
        val canvas = sharedGlContext.canvas
        canvas.style.apply {
            position = "absolute"
            top = "0"
            left = "0"
            width = "100%"
            height = "100%"
            setProperty("pointer-events", "none")
        }
        canvasParentRef.current!!.let { parent ->
            parent.insertBefore(canvas, parent.firstChild)
        }

        withCleanup {
            canvasParentRef.current!!.removeChild(canvas)
        }
    }

    div(+Styles.showLayout) {
        ref = canvasParentRef

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
            baaahs.app.ui.appGlContext.Provider {
                attrs.value = appGlContext

                val panel = props.show.layouts.panels.getBang(panelId, "panel")
                paper(Styles.layoutPanelPaper on PaperStyle.root) {
                    inlineStyles {
                        put("gridArea", panelId)
                        // TODO: panel flow direction could change here.
                        flexDirection = FlexDirection.column
                    }

                    header { +panel.title }

                    paper(Styles.layoutPanel and editModeStyle on PaperStyle.root) {
                        props.controlDisplay.render(panel) { panelBucket ->
                            droppable({
                                this.droppableId = panelBucket.dropTargetId
                                this.type = panelBucket.type
                                this.direction = Direction.horizontal.name
                                this.isDropDisabled = !props.editMode
                            }) { droppableProvided, _ ->
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
                                            this.isDragDisabled = !props.editMode
                                            this.index = index
                                        }) { draggableProvided, _ ->
                                            controlWrapper {
                                                attrs.control = control
                                                attrs.controlProps = props.controlProps
                                                attrs.draggableProvided = draggableProvided
                                            }
                                        }
                                    }

                                    insertPlaceholder(droppableProvided)

                                    iconButton(+Styles.addToSectionButton on IconButtonStyle.root) {
                                        attrs.onClickFunction = handleAddButtonClick.getOrPut(panelBucket.suggestId()) {
                                            { event: Event ->
                                                showAddMenuFor = panelBucket
                                                showAddMenuForAnchorEl = event.target
                                            }
                                        }.withEvent()
                                        icon(Icons.AddCircleOutline)
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
        menu {
            attrs.getContentAnchorEl = null
            attrs.anchorEl(showAddMenuForAnchorEl)
            attrs.open = true
            attrs.onClose = { _, _ -> showAddMenuFor = null }

            appContext.plugins.addControlMenuItems.forEach { addControlMenuItem ->
                menuItem {
                    attrs.onClickFunction = {
                        val editIntent = AddControlToPanelBucket(panelBucket, addControlMenuItem.createControlFn)
                        appContext.openEditor(editIntent)
                        showAddMenuFor = null
                    }

                    listItemIcon { icon(addControlMenuItem.icon) }
                    listItemText { +addControlMenuItem.label }
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

external interface ShowLayoutProps : RProps {
    var show: OpenShow
    var onShowStateChange: () -> Unit
    var layout: Layout
    var controlDisplay: ControlDisplay
    var controlProps: ControlProps
    var editMode: Boolean
}

fun RBuilder.showLayout(handler: RHandler<ShowLayoutProps>): ReactElement =
    child(ShowLayout, handler = handler)

@Suppress("UNCHECKED_CAST")
fun <T> RBuilder.mosaic(handler: MosaicControlledProps<T>.() -> Unit): ReactElement =
    child(Mosaic::class as KClass<out Component<MosaicControlledProps<T>, *>>) { attrs { handler() } }

@Suppress("UNCHECKED_CAST")
fun <T> RBuilder.mosaicWindow(handler: MosaicWindowProps<T>.() -> Unit): ReactElement =
    child(MosaicWindow::class as KClass<out Component<MosaicWindowProps<T>, *>>) { attrs { handler() } }
