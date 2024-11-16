package baaahs.mapper

import baaahs.fixtures.ConfigPreview
import baaahs.ui.*
import mui.material.Chip
import mui.material.ChipVariant
import react.*

private val ConfigPreviewView = xComponent<ConfigPreviewProps>("ConfigPreview") { props ->
    props.configPreview.summary().forEach { (title, value) ->
        Chip {
            attrs.variant = ChipVariant.outlined
            attrs.title = title
            attrs.label = buildElement { +(value ?: "?") }
        }
    }
}

external interface ConfigPreviewProps : Props {
    var configPreview: ConfigPreview
}

fun RBuilder.configPreview(handler: RHandler<ConfigPreviewProps>) =
    child(ConfigPreviewView, handler = handler)