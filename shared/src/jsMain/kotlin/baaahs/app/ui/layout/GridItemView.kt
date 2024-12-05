package baaahs.app.ui.layout

import baaahs.app.ui.appContext
import baaahs.app.ui.controls.problemBadge
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenControl
import baaahs.show.mutable.MutableShow
import baaahs.ui.and
import baaahs.ui.render
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import materialui.icon
import mui.material.ListItemText
import mui.material.Menu
import mui.material.MenuItem
import react.*
import react.dom.div
import react.dom.events.MouseEvent
import react.dom.onClick
import react.dom.onMouseDown
import react.dom.onTouchEnd
import web.dom.Element

private val showItemControlsMenu = true

private val GridItemView = xComponent<GridItemProps>("GridItem") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layout
    val showManager = appContext.showManager

    val control = props.control
    val layout = props.controlProps.layout
    val layoutEditor = props.controlProps.layoutEditor
    val editMode = observe(appContext.showManager.editMode)

    var menuAnchor by state<Element?> { null }

    // We're inside a draggable; prevent the mousedown from starting a drag.
    val handleItemControlsMouseDown by mouseEventHandler { e: MouseEvent<*, *> -> e.stopPropagation() }
    val handleEditMouseDown by mouseEventHandler { e: MouseEvent<*, *> -> e.stopPropagation() }
    val handleDeleteMouseDown by mouseEventHandler { e: MouseEvent<*, *> -> e.stopPropagation() }

    val handleItemControlsButtonClick by mouseEventHandler(control, layout, layoutEditor) { event ->
        menuAnchor = event.target as Element?
        event.preventDefault()
    }
    val handleMenuClose by handler {
        menuAnchor = null
    }

    val handleEditButtonClick by mouseEventHandler(control, layout, layoutEditor) { event ->
        control.getEditIntent()
            ?.withLayout(layout, layoutEditor)
            ?.let { appContext.openEditor(it) }

        event.preventDefault()
        menuAnchor = null
    }

    val handleDeleteButtonClick by mouseEventHandler(control, layoutEditor, showManager.show) { event ->
        val mutableShow = MutableShow(showManager.show!!)
        layoutEditor!!.delete(mutableShow)
        showManager.onEdit(mutableShow, true)

        event.preventDefault()
        menuAnchor = null
    }

    control.getView(props.controlProps)
        .render(this)

    problemBadge(control)

    if (editMode.isAvailable) {
        if (showItemControlsMenu) {
            div(+styles.itemControlsButton and styles.itemControlsModeControl) {
                attrs.onMouseDown = handleItemControlsMouseDown
                attrs.onClick = handleItemControlsButtonClick
                // onClick doesn't work on iOS but onTouchEnd does.
                attrs.onTouchEnd = handleItemControlsButtonClick.asDynamic()

                icon(mui.icons.material.Settings)
            }
        } else {
            div(+styles.deleteButton and styles.deleteModeControl) {
                attrs.onMouseDown = handleDeleteMouseDown
                attrs.onClick = handleDeleteButtonClick
                // onClick doesn't work on iOS but onTouchEnd does.
                attrs.onTouchEnd = handleDeleteButtonClick.asDynamic()

                icon(mui.icons.material.Delete)
            }

            div(+styles.editButton and styles.editModeControl) {
                attrs.onMouseDown = handleEditMouseDown
                attrs.onClick = handleEditButtonClick
                // onClick doesn't work on iOS but onTouchEnd does.
                attrs.onTouchEnd = handleEditButtonClick.asDynamic()

                icon(mui.icons.material.Edit)
            }
        }
    }

    if (menuAnchor != null) {
        Menu {
            attrs.anchorEl = menuAnchor.asDynamic()
            attrs.open = true
            attrs.onClose = handleMenuClose

            MenuItem {
                attrs.onClick = handleEditButtonClick
                ListItemText { +"Edit" }
            }

            MenuItem {
                attrs.onClick = handleDeleteButtonClick
                ListItemText { +"Delete" }
            }
        }
    }
}

external interface GridItemProps : PropsWithClassName, PropsWithStyle {
    var control: OpenControl
    var controlProps: ControlProps
}

fun RBuilder.gridItem(handler: RHandler<GridItemProps>) =
    child(GridItemView, handler = handler)