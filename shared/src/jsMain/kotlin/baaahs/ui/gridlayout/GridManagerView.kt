package baaahs.ui.gridlayout

import baaahs.app.ui.appContext
import baaahs.geom.Vector2I
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import react.*
import react.dom.div
import web.html.HTMLElement

private val GridManagerView = xComponent<GridManagerProps>("GridManager") { props ->
    console.log("GridManagerView render ", renderCounter)
    val appContext = useContext(appContext)
    val layoutStyles = appContext.allStyles.layout
    val grid2Styles = appContext.allStyles.grid2

    val rootSize = ref<Vector2I>(null)
    val gridManager = memo(props.gridModel, props.render, props.onChange) {
        val debugFn = { s: String ->
//            val el = document.getElementsByClassName("app-ui-gridlayout-debugBox")[0] as HTMLElement
//            if (s.startsWith('\n')) {
//                el.innerText = s.substring(1)
//            } else {
//                el.innerText += "\n$s"
//            }
        }
        console.log("New ReactGridManager")
        ReactGridManager(props.gridModel, grid2Styles, debugFn, props.render, props.renderEmptyCell) {
            console.log("Grid changed!")
            console.log(it.rootNode.stringify())
            props.onChange(it)
        }.also {
            rootSize.current?.let { size -> it.onResize(size.x, size.y) }
        }
    }

    gridManager.editable(props.isEditable == true)

    val rootRef = ref<HTMLElement>()
    useResizeListener(rootRef) { width, height ->
        rootSize.current = Vector2I(width, height)
        gridManager.onResize(width, height)
    }

    div(+grid2Styles.debugBox) {}

    div(+layoutStyles.gridOuterContainer) {
        ref = rootRef

        gridManager.reactNodeWrappers.forEach { (_, nodeWrapper) ->
            child(nodeWrapper.reactNode)
            nodeWrapper.emptyCells.forEach { cell -> child(cell.reactNode) }
        }

        child(gridManager.placeholder.reactNode)
    }
}

external interface GridManagerProps : Props {
    var gridModel: GridModel
    var render: (String) -> ReactNode
    var renderEmptyCell: RenderEmptyCell?
    var isEditable: Boolean?
    var onChange: (GridModel) -> Unit
}

fun RBuilder.gridManager(handler: RHandler<GridManagerProps>) =
    child(GridManagerView, handler = handler)