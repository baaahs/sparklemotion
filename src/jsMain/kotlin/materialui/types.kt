package materialui

import baaahs.ui.Icon
import mui.icons.material.SvgIconComponent
import mui.material.styles.Theme
import mui.material.styles.Transitions
import react.RBuilder
import react.create

fun RBuilder.icon(icon: Icon) = child(icon.getReactIcon().create())
fun RBuilder.icon(icon: SvgIconComponent) = child(icon.create())

fun Theme.createTransition(vararg props: String, options: Transitions): kotlinx.css.properties.Transitions =
    transitions.create(props, options)