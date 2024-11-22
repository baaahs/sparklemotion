package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.fixtures.ConfigPreview
import baaahs.ui.buildElements
import baaahs.ui.muiClasses
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.Chip
import mui.material.ChipVariant
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.html.ReactHTML.div
import react.useContext

private val ConfigPreviewView = xComponent<ConfigPreviewProps>("ConfigPreview") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.controllerEditor

    props.configPreview.summary().forEach { nugget ->
        Chip {
            attrs.classes = muiClasses { label = +styles.previewChip }
            attrs.variant = ChipVariant.outlined
            attrs.title = nugget.title
            attrs.label = buildElements {
                if (nugget.shortTitle != null) {
                    div { +(nugget.value ?: "?") }
                    div { +(nugget.shortTitle) }
                } else {
                    +(nugget.value ?: "?")
                }
            }
        }
    }
}

external interface ConfigPreviewProps : Props {
    var configPreview: ConfigPreview
}

fun RBuilder.configPreview(handler: RHandler<ConfigPreviewProps>) =
    child(ConfigPreviewView, handler = handler)