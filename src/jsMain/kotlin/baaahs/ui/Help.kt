package baaahs.ui

import kotlinx.css.LinearDimension
import materialui.icon
import mui.material.*
import org.w3c.dom.events.Event
import react.*
import web.html.HTMLElement
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

val Help = xComponent<HelpProps>("Help", isPure = true) { props ->
    var open by state { false }

    val toggleHelp = callback { open = !open }
    val closeHelp = callback { _: Event, _: String -> open = false }

    Tooltip {
        attrs.title = "Help".asTextNode()
//        ("${Styles.help.name} ${props.divClass ?: ""}") {
//        props.iconSize?.let { iconSize ->
//            inlineStyles { fontSize = iconSize }
//        }

        IconButton {
            attrs.onClick = toggleHelp.withMouseEvent()
            icon(mui.icons.material.HelpOutline) {
                if (props.iconSize != null) {
                    fontSize = SvgIconSize.inherit
                }
            }
        }
    }

    Dialog {
        attrs.open = open
        attrs.onClose = closeHelp

        if (open) {
            props.title?.let {
                DialogTitle { child(it) }
            }

            DialogContent {
                attrs.onClick = {
                    val classList = (it.target as? HTMLElement)?.classList
                    if (classList?.contains(Styles.helpAutoClose.name) == true) {
                        open = false
                    }
                }
                props.children?.forEach { child(it) }
            }
        }

        DialogActions {
            Button {
                attrs.onClick = toggleHelp.withMouseEvent()

                +"Close"
            }
        }
    }
}

external interface HelpProps : Props {
    var iconSize: LinearDimension?
    var divClass: String?
    var children: Array<ReactElement<*>>?
}

fun HelpProps.child(block: RBuilder.() -> Unit) {
    val rBuilder = RBuilder()
    rBuilder.block()
    if (children == null) {
        children = arrayOf()
    }
    rBuilder.childList.forEach { children.asDynamic().push(it) }
}

object materialProps : ReadWriteProperty<HelpProps, ReactElement<*>?> {
    override fun getValue(thisRef: HelpProps, property: KProperty<*>): ReactElement<*>? {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        return thisRef.asDynamic()[property.name] as? ReactElement<*>
    }

    override fun setValue(thisRef: HelpProps, property: KProperty<*>, value: ReactElement<*>?) {
        thisRef.asDynamic()[property.name] = value
    }
}

var HelpProps.title: ReactElement<*>? by materialProps
fun HelpProps.title(block: RBuilder.() -> Unit) { title = buildElement(block) }

fun helper(block: HelpProps.() -> Unit): HelpProps.() -> Unit {
    return { block(this) }
}

inline fun HelpProps.inject(block: HelpProps.() -> Unit) {
    block.invoke(this)
}


fun RBuilder.help(handler: RHandler<HelpProps>) =
    child(Help, handler = handler)