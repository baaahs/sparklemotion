package baaahs.app.ui.layout

import baaahs.SparkleMotion
import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.app.ui.controls.ControlContext
import baaahs.app.ui.controls.controlContext
import baaahs.app.ui.controls.problemBadge
import baaahs.app.ui.editor.Editor
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenControl
import baaahs.show.live.OpenTab
import baaahs.show.mutable.MutableIGridLayout
import baaahs.ui.render
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import js.objects.jso
import materialui.icon
import mui.material.ListItemIcon
import mui.material.ListItemText
import mui.material.Menu
import mui.material.MenuItem
import react.*
import react.dom.div
import web.dom.Element
import web.events.EventType
import web.events.addEventListener
import web.html.HTMLElement
import web.uievents.MouseButton

private val GridItemView = xComponent<GridItemProps>("GridItem") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layout
    val showManager = appContext.showManager

    val control = props.control
    val tab = props.tab
    val editMode = observe(appContext.showManager.editMode)

    var menuAnchor by state<Element?> { null }

    val handleMenuClose by handler {
        menuAnchor = null
    }

    val handleEditMenuClick by mouseEventHandler(control, tab) { event ->
        control.getEditIntent()
            ?.let { appContext.openEditor(it) }

        event.preventDefault()
        menuAnchor = null
    }

    val handleDeleteMenuClick by mouseEventHandler(control, showManager.show, tab, props.tabEditor) { event ->
        val mutableShow = showManager.show!!.edit()
        props.tabEditor.edit(mutableShow) {
            removeControl(control.id)
        }
        showManager.onEdit(mutableShow, true)

        event.preventDefault()
        menuAnchor = null
    }

    val controlContextValue = memo(props.parentControl) {
        jso<ControlContext> {
            this.parentControl = props.parentControl
        }
    }

    div(+styles.gridItem) {
        ref = RefCallback<HTMLElement> { el ->
            el?.setAttribute("data-long-press-delay", SparkleMotion.LONG_PRESS_DELAY_MS.toString())
            el?.addEventListener(EventType("long-press"), { e ->
                val originalEvent = e.asDynamic().detail.originalEvent as web.uievents.PointerEvent
                val isPrimaryButton = originalEvent.button == MouseButton.MAIN && !originalEvent.ctrlKey
                if (isPrimaryButton && appContext.showManager.editMode.isOn) {
                    menuAnchor = el
                }
            })
        }

        controlContext.Provider {
            attrs.value = controlContextValue

            control.getView(props.controlProps)
                .render(this)
        }
    }

    problemBadge(control)

    if (menuAnchor != null) {
        Menu {
            attrs.anchorEl = menuAnchor.asDynamic()
            attrs.anchorOrigin = jso {
                horizontal = "center"
                vertical = "center"
            }
            attrs.open = true
            attrs.onClose = handleMenuClose

            MenuItem {
                attrs.onClick = handleEditMenuClick
                ListItemIcon { icon(CommonIcons.Edit) }
                ListItemText { +"Edit" }
            }

            MenuItem {
                attrs.onClick = handleDeleteMenuClick
                ListItemIcon { icon(CommonIcons.Delete) }
                ListItemText { +"Delete" }
            }
        }
    }
}

external interface GridItemProps : PropsWithClassName, PropsWithStyle {
    var tab: OpenTab
    var tabEditor: Editor<MutableIGridLayout>
    var control: OpenControl
    var parentControl: OpenControl?
    var controlProps: ControlProps
}

fun RBuilder.gridItem(handler: RHandler<GridItemProps>) =
    child(GridItemView, handler = handler)