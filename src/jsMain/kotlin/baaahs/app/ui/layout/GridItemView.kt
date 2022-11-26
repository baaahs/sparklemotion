package baaahs.app.ui.layout

import baaahs.app.ui.appContext
import baaahs.app.ui.controls.problemBadge
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenControl
import baaahs.show.mutable.MutableShow
import baaahs.ui.and
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import kotlinx.html.org.w3c.dom.events.Event
import materialui.icon
import react.*
import react.dom.div
import react.dom.events.MouseEvent
import react.dom.onMouseDown

private val GridItemView = xComponent<GridItemProps>("GridItem") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layout
    val showManager = appContext.showManager

    val control = props.control
    val layout = props.controlProps.layout
    val layoutEditor = props.controlProps.layoutEditor
    val editMode = observe(appContext.showManager.editMode)

    // We're inside a draggable; prevent the mousedown from starting a drag.
    val handleEditMouseDown by mouseEventHandler { e: MouseEvent<*, *> -> e.stopPropagation() }
    val handleDeleteMouseDown by mouseEventHandler { e: MouseEvent<*, *> -> e.stopPropagation() }

    val handleEditButtonClick = callback(control, layout, layoutEditor) { event: Event ->
        control.getEditIntent()
            ?.withLayout(layout, layoutEditor)
            ?.let { appContext.openEditor(it) }
        event.preventDefault()
    }

    val handleDeleteButtonClick = callback(control, layoutEditor, showManager.show) { event: Event ->
        val mutableShow = MutableShow(showManager.show!!)
        layoutEditor!!.delete(mutableShow)
        showManager.onEdit(mutableShow, true)

        event.preventDefault()
    }

    with (control.getView(props.controlProps)) {
        render()
    }

    problemBadge(control)

    if (editMode.isAvailable) {
        div(+styles.deleteButton and styles.deleteModeControl) {
            attrs.onMouseDown = handleDeleteMouseDown
            attrs.onClickFunction = handleDeleteButtonClick

            icon(mui.icons.material.Delete)
        }

        div(+styles.editButton and styles.editModeControl) {
            attrs.onMouseDown = handleEditMouseDown
            attrs.onClickFunction = handleEditButtonClick

            icon(mui.icons.material.Edit)
        }
    }
}

external interface GridItemProps : PropsWithClassName, PropsWithStyle {
    var control: OpenControl
    var controlProps: ControlProps
}

fun RBuilder.gridItem(handler: RHandler<GridItemProps>) =
    child(GridItemView, handler = handler)