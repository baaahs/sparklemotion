package baaahs.app.ui

import baaahs.show.LayoutNode
import baaahs.ui.and
import baaahs.ui.name
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.LinearDimension
import kotlinx.css.flex
import kotlinx.css.px
import react.*
import react.dom.div
import styled.StyleSheet

val LayoutContainer: FunctionalComponent<LayoutContainerProps> = xComponent("LayoutContainer") { props ->
    val node = props.node
    val sheet = LayoutNodeStyle(props.id, node)

    when (node) {
        is LayoutNode.Container -> {
            val directionStyle = if (node is LayoutNode.Columns) Styles.layoutColumns.name else Styles.layoutRows.name
            div(+Styles.layoutContainer and directionStyle and sheet.style) {
                node.items.forEachIndexed { index, childNode ->
                    layoutContainer {
                        attrs.node = childNode
                        attrs.renderPanel = props.renderPanel
                        attrs.id = props.id + "_" + index
                    }
                }
            }
        }
        is LayoutNode.Panel -> {
            (props.renderPanel)(node, +Styles.layoutPanel and Styles.layoutPanelFlows[node.flow]!! and sheet.style)
        }
        else -> throw IllegalStateException()
    }
}

class LayoutNodeStyle(id: String, node: LayoutNode) : StyleSheet(id, isStatic = true) {
    val style by css {
        val weight = node.size.toDoubleOrNull()
        if (weight != null) {
            flex(weight, weight, 0.px)
        } else {
            flex(0.0, 0.0, LinearDimension(node.size))
        }
    }
}

interface LayoutContainerProps : RProps {
    var node: LayoutNode
    var id: String
    var renderPanel: LayoutRenderer
}

typealias LayoutRenderer = RBuilder.(panel: LayoutNode.Panel, classes: String) -> Unit

fun RBuilder.layoutContainer(handler: RHandler<LayoutContainerProps>) =
    child(LayoutContainer, handler = handler)