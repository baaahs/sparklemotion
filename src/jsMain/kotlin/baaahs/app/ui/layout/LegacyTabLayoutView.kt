package baaahs.app.ui.layout

import baaahs.app.ui.Styles
import baaahs.app.ui.appContext
import baaahs.app.ui.controlsPalette
import baaahs.getBang
import baaahs.show.LegacyTab
import baaahs.show.live.ControlProps
import baaahs.show.live.LegacyControlDisplay
import baaahs.show.live.OpenShow
import baaahs.ui.and
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.dragDropContext
import external.mui.Portal
import js.core.jso
import kotlinx.css.*
import mui.material.Paper
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.header
import react.useContext
import styled.inlineStyles
import web.cssom.FlexDirection
import web.cssom.ident

private val LegacyTabLayoutView = xComponent<LegacyTabLayoutProps>("LegacyTabLayout") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.showManager.editMode)
    val editModeStyle = if (editMode.isOn) Styles.editModeOn else Styles.editModeOff
    val tab = props.tab

    val colCount = tab.columns.size
    val rowCount = tab.rows.size
    if (tab.areas.size != colCount * rowCount) {
        error("Invalid layout! " +
                "Area count (${tab.areas.size} != cell count " +
                "($colCount columns * $rowCount rows)")
    }

    val areas = mutableListOf<String>()
    tab.rows.indices.forEach { rowIndex ->
        val cols = mutableListOf<String>()

        tab.columns.indices.forEach { columnIndex ->
            cols.add(tab.areas[rowIndex * colCount + columnIndex])
        }
        areas.add(cols.joinToString(" ") { it.replace(" ", "") })
    }

    logger.info { "switch state is ${props.show.getEnabledSwitchState()}" }

    val controlDisplay = memo(props.show, props.show.getEnabledSwitchState()) {
        LegacyControlDisplay(props.show, appContext.showManager, appContext.dragNDrop)
            .also { withCleanup { it.release() } }
    }

    val genericControlProps = memo(props.onShowStateChange, controlDisplay) {
        ControlProps(props.onShowStateChange, controlDisplay)
    }

    dragDropContext({
        onDragEnd = appContext.dragNDrop::onDragEnd
    }) {
        div(+Styles.showLayout) {
            inlineStyles {
                gridTemplateAreas = GridTemplateAreas(areas.joinToString(" ") { "\"$it\"" })
                gridTemplateColumns = GridTemplateColumns(tab.columns.joinToString(" "))
                gridTemplateRows = GridTemplateRows(tab.rows.joinToString(" "))
            }

            tab.areas.distinct().forEach { panelId ->
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

                        legacyPanelLayout {
                            attrs.panel = panel
                            attrs.controlDisplay = controlDisplay
                            attrs.controlProps = genericControlProps
                        }
                    }
                }
            }
        }

        Portal {
            controlsPalette {
                attrs.controlDisplay = controlDisplay
                attrs.controlProps = genericControlProps
                attrs.show = props.show
            }
        }
    }
}

external interface LegacyTabLayoutProps : Props {
    var show: OpenShow
    var tab: LegacyTab
    var onShowStateChange: () -> Unit
}

fun RBuilder.legacyTabLayout(handler: RHandler<LegacyTabLayoutProps>) =
    child(LegacyTabLayoutView, handler = handler)