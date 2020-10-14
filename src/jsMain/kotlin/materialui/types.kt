package materialui

import baaahs.ui.Icon
import kotlinext.js.jsObject
import materialui.components.svgicon.SvgIconProps
import react.*

typealias Icon = RComponent<SvgIconProps, RState>

fun RBuilder.icon(icon: RComponent<SvgIconProps, *>, handler: RHandler<SvgIconProps> = { }): ReactElement =
    child(icon, jsObject() { fontSize = "inherit" }, handler = handler)

fun RBuilder.icon(icon: Icon, handler: RHandler<SvgIconProps> = { }): ReactElement =
    child(icon.getReactIcon(), jsObject() { fontSize = "inherit" }, handler = handler)
