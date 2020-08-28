package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.gl.shader.InputPort
import baaahs.plugin.BeatLinkPlugin
import baaahs.show.mutable.MutableSourcePort
import kotlinx.html.js.onChangeFunction
import materialui.AddCircleOutline
import materialui.Icon
import materialui.NotInterested
import materialui.components.divider.divider
import materialui.components.formcontrol.formControl
import materialui.components.inputlabel.inputLabel
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.listsubheader.listSubheader
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import materialui.icon
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
            this@xComponent.forceRender()
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
            val selected = sourcePortOptions.find { it == props.currentSourceSourcePort }
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
    override val sourcePort: SourcePort get() = error("not implemented")
    override val groupName: String? get() = null
    override val icon: Icon get() = NotInterested
    override fun isAppropriateFor(inputPort: InputPort): Boolean = true
}

private object NewSourcePortOption : SourcePortOption {
    override val title: String get() = "Create New…"
    override val sourcePort: SourcePort get() = error("not implemented")
    override val groupName: String? get() = null
    override val icon: Icon get() = AddCircleOutline
    override fun isAppropriateFor(inputPort: InputPort): Boolean = true
}

external interface LinkSourceEditorProps : RProps {
    var inputPort: InputPort
    var currentSourceSourcePort: MutableSourcePort?
    var sourcePortOptions: List<SourcePortOption>
    var onChange: (SourcePortOption?) -> Unit
}

fun RBuilder.linkSourceEditor(handler: RHandler<LinkSourceEditorProps>) =
    child(LinkSourceEditor, handler = handler)
