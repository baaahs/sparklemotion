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

private val GridItemView = xComponent<GridItemProps>("GridItem") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layout

    val onEditButtonClick = callback(props.control) { event: Event ->
        props.control.getEditIntent()?.let { appContext.openEditor(it) }
        event.preventDefault()
    }

    with (props.control.getView(props.controlProps)) {
        render()
    }

    problemBadge(props.control)

    div(+styles.editButton and styles.editModeControl) {
        attrs.onClickFunction = onEditButtonClick

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