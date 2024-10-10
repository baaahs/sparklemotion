package baaahs.app.ui.settings

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
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
    observe(appContext.webClient)

    val handleClick by mouseEventHandler(appContext.webClient) {
        appContext.webClient.toggleFullScreen()
    }
    val inFullScreen = appContext.webClient.inFullScreenMode

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