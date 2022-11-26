package baaahs.app.ui.layout

import baaahs.app.ui.appContext
import baaahs.ui.gridlayout.LayoutGrid
import baaahs.ui.gridlayout.PositionParams
import baaahs.ui.unaryPlus
import baaahs.ui.withMouseEvent
import baaahs.ui.xComponent
import kotlinx.css.*
import kotlinx.html.org.w3c.dom.events.Event
import materialui.icon
import mui.icons.material.Add
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.onClick
import react.dom.onMouseDown
import react.useContext
import styled.inlineStyles

private val GridBackgroundView = xComponent<GridBackgroundProps>("GridBackground") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.showManager.editMode)
    val layoutStyles = appContext.allStyles.layout
    val layoutGrid = props.layoutGrid

    if (editMode.isAvailable) {
        div(+layoutStyles.gridBackground) {
            val positionParams = PositionParams(
                props.margin to props.margin,
                props.itemPadding to props.itemPadding,
                props.layoutWidth,
                layoutGrid.layout.cols,
                props.gridRowHeight,
                layoutGrid.layout.rows
            )

            layoutGrid.forEachCell { column, row ->
                val position = positionParams.calcGridItemPosition(column, row, 1, 1)

                div(+layoutStyles.emptyGridCell) {
                    inlineStyles {
                        top = position.top.px
                        left = position.left.px
                        width = position.width.px
                        height = position.height.px
                    }

                    attrs["data-cell-x"] = column
                    attrs["data-cell-y"] = row

                    props.onGridCellMouseDown?.let {
                        attrs.onMouseDown = it.withMouseEvent()
                    }
                    attrs.onClick = props.onGridCellClick.withMouseEvent()

                    icon(Add)
                }
            }
        }
    }
}

external interface GridBackgroundProps : Props {
    var layoutGrid: LayoutGrid
    var margin: Int
    var itemPadding: Int
    var layoutWidth: Int
    var gridRowHeight: Double
    var onGridCellMouseDown: ((Event) -> Unit)?
    var onGridCellClick: (Event) -> Unit
}

fun RBuilder.gridBackground(handler: RHandler<GridBackgroundProps>) =
    child(GridBackgroundView, handler = handler)