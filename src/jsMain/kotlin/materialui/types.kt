package materialui

import baaahs.app.ui.getReactIcon
import baaahs.ui.Icon
import mui.icons.material.SvgIconComponent
import mui.material.SvgIconProps
import react.RBuilder
import react.ReactDsl
import react.create

fun RBuilder.icon(icon: Icon) = child(icon.getReactIcon().create())

fun RBuilder.icon(icon: SvgIconComponent, block: @ReactDsl SvgIconProps.() -> Unit = {}) =
    child(icon.create(block))

//fun Theme.createTransition(vararg props: String, options: Transitions): kotlinx.css.properties.Transitions =
//    transitions.create(props, options)