package baaahs.app.ui.settings

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.document
import baaahs.ui.asTextNode
import baaahs.ui.xComponent
import materialui.icon
import mui.material.IconButton
import mui.material.Tooltip
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val FullScreenToggleButtonView = xComponent<FullScreenToggleButtonProps>("FullScreenToggleButton") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.appUi

    var inFullScreen by state { false }

    val handleClick by mouseEventHandler {
        if (document.fullscreenElement == null) {
            document.documentElement.requestFullscreen()
            inFullScreen = true
        } else {
            document.exitFullscreen()
            inFullScreen = false
        }
    }

    Tooltip {
        attrs.title = when {
            inFullScreen -> "Exit Full Screen"
            else -> "Enter Full Screen"
        }.asTextNode()

        IconButton {
            attrs.onClick = handleClick
            icon(
                when {
                    inFullScreen -> CommonIcons.ExitFullScreen
                    else -> CommonIcons.EnterFullScreen
                }
            )
        }
    }
}

external interface FullScreenToggleButtonProps : Props {
}

fun RBuilder.fullScreenToggleButton(handler: RHandler<FullScreenToggleButtonProps>) =
    child(FullScreenToggleButtonView, handler = handler)