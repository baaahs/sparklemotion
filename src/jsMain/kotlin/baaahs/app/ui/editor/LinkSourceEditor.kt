package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.gl.shader.InputPort
import baaahs.plugin.BeatLinkPlugin
import baaahs.show.mutable.MutablePort
import baaahs.ui.asTextNode
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.Icon
import materialui.components.divider.divider
import materialui.components.formcontrol.formControl
import materialui.components.inputlabel.inputLabel
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.listsubheader.listSubheader
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import materialui.icon
import materialui.icons.Icons
import react.*

val LinkSourceEditor = xComponent<LinkSourceEditorProps>("LinkSourceEditor", isPure = true) { props ->
    val appContext = useContext(appContext)

    val suggestedDataSources = appContext.plugins.suggestDataSources(
        props.inputPort, setOf(BeatLinkPlugin.beatDataContentType)
    )

    val handleChange =
        eventHandler("change to ${props.inputPort.id}", props.onChange) { event ->
            val value = event.target.asDynamic().value as SourcePortOption?
            props.onChange(
                when (value) {
                    NoSourcePortOption -> null
                    NewSourcePortOption -> error("new not yet implemented") // TODO
                    else -> value
                }
            )
        }

    val sourcePortOptions = props.sourcePortOptions + suggestedDataSources.map { DataSourceOption(it) } +
            NoSourcePortOption + NewSourcePortOption

    formControl {
        inputLabel { +"Source" }

        select {
            attrs.renderValue<SourcePortOption> {
                it.title.asTextNode()
            }
            attrs.onChangeFunction = handleChange
            val selected = sourcePortOptions.find { it.matches(props.currentSourcePort) }
            attrs.value = selected
            if (selected == null) {
                this@xComponent.logger.warn { "Huh? None of the SourcePortOptions are active for ${props.inputPort.id}?" }
            }

            var dividerGroup: String? = null
            sourcePortOptions.forEach { option ->
                if (option.isAppropriateFor(props.inputPort)) {
                    if (dividerGroup != option.groupName) {
                        if (dividerGroup != null) {
                            divider {}
                        }
                        option.groupName?.let { listSubheader { +it } }
                        dividerGroup = option.groupName
                    }

                    menuItem {
                        attrs.dense = true
                        attrs["value"] = option
                        listItemIcon { icon(option.icon) }
                        listItemText { +option.title }
                    }
                }
            }
        }
    }
}

private object NoSourcePortOption : SourcePortOption {
    override val title: String get() = "Nothing"
    override val portEditor: MutablePort get() = error("not implemented")
    override val groupName: String? get() = null
    override val icon: Icon get() = Icons.NotInterested
    override fun matches(otherPort: MutablePort?): Boolean = otherPort == null
    override fun isAppropriateFor(inputPort: InputPort): Boolean = true
}

private object NewSourcePortOption : SourcePortOption {
    override val title: String get() = "Create New…"
    override val portEditor: MutablePort get() = error("not implemented")
    override val groupName: String? get() = null
    override val icon: Icon get() = Icons.AddCircleOutline
    override fun matches(otherPort: MutablePort?): Boolean = false
    override fun isAppropriateFor(inputPort: InputPort): Boolean = true
}

external interface LinkSourceEditorProps : RProps {
    var inputPort: InputPort
    var currentSourcePort: MutablePort?
    var sourcePortOptions: List<SourcePortOption>
    var onChange: (SourcePortOption?) -> Unit
}

fun RBuilder.linkSourceEditor(handler: RHandler<LinkSourceEditorProps>) =
    child(LinkSourceEditor, handler = handler)
