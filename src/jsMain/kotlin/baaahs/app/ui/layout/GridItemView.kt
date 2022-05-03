package baaahs.app.ui.layout

import baaahs.app.ui.appContext
import baaahs.app.ui.controls.Styles
import baaahs.app.ui.controls.problemBadge
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenControl
import baaahs.ui.and
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import materialui.icon
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.events.MouseEvent
import react.dom.onMouseDown

private val GridItemView = xComponent<GridItemProps>("GridItem") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layout

    val layout = props.controlProps.layout
    val layoutEditor = props.controlProps.layoutEditor

    // We're inside a draggable; prevent the mousedown from starting a drag.
    val handleEditMouseDown by mouseEventHandler() { e: MouseEvent<*, *> ->
        e.stopPropagation()
    }

    val handleEditButtonClick = callback(props.control, layout, layoutEditor) { event: Event ->
        props.control.getEditIntent()
            ?.withLayout(layout, layoutEditor)
            ?.let { appContext.openEditor(it) }
        event.preventDefault()
    }

    with (props.control.getView(props.controlProps)) {
        render()
    }

    problemBadge(props.control)

    div(+styles.editButton and styles.editModeControl) {
        attrs.onMouseDown = handleEditMouseDown
        attrs.onClickFunction = handleEditButtonClick

        icon(mui.icons.material.Edit)
    }

    div(+Styles.dragHandle) {
        icon(mui.icons.material.DragIndicator)
    }
}

external interface GridItemProps : PropsWithClassName, PropsWithStyle {
    var control: OpenControl
    var controlProps: ControlProps
}

fun RBuilder.gridItem(handler: RHandler<GridItemProps>) =
    child(GridItemView, handler = handler)