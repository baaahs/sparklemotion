package baaahs.ui

import baaahs.app.ui.appContext
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.dialog.dialog
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.link.link
import materialui.icon
import materialui.icons.Icons
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

val Help = xComponent<HelpProps>("Help", isPure = true) { props ->
    val styles = useContext(appContext).allStyles.appUi

    var open by state { false }

    val toggleHelp = useCallback { open = !open }
    val closeHelp = useCallback { _: Event, _: String -> open = false }

    div("${styles.help.name} ${props.divClass}") {
        link {
            attrs.onClickFunction = toggleHelp.withEvent()
            icon(Icons.HelpOutline)
        }
    }

    dialog {
        attrs.open = open
        attrs.onClose = closeHelp

        if (open) {
            props.title?.let {
                dialogTitle { child(it) }
            }

            dialogContent {
                props.children?.forEach { child(it) }
            }
        }

        dialogActions {
            button {
                attrs.onClickFunction = toggleHelp.withEvent()

                +"Close"
            }
        }
    }
}

external interface HelpProps : RProps {
    var divClass: String?
    var children: Array<ReactElement>?
}

fun HelpProps.child(block: RBuilder.() -> Unit) {
    val rBuilder = RBuilder()
    rBuilder.block()
    if (children == null) {
        children = arrayOf()
    }
    rBuilder.childList.forEach { children.asDynamic().push(it) }
}

object materialProps : ReadWriteProperty<HelpProps, ReactElement?> {
    override fun getValue(thisRef: HelpProps, property: KProperty<*>): ReactElement? {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        return thisRef.asDynamic()[property.name] as? ReactElement
    }

    override fun setValue(thisRef: HelpProps, property: KProperty<*>, value: ReactElement?) {
        thisRef.asDynamic()[property.name] = value
    }
}

var HelpProps.title: ReactElement? by materialProps
fun HelpProps.title(block: RBuilder.() -> Unit) { title = buildElement(block) }

fun helper(block: HelpProps.() -> Unit): HelpProps.() -> Unit {
    return { block(this) }
}

inline fun HelpProps.inject(block: HelpProps.() -> Unit) {
    block.invoke(this)
}


fun RBuilder.help(handler: RHandler<HelpProps>) =
    child(Help, handler = handler)