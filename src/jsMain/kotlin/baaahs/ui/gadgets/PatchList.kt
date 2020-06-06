package baaahs.ui.gadgets

import baaahs.PubSub
import baaahs.show.PatchSet
import kotlinx.html.Tag
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttongroup.buttonGroup
import materialui.components.buttongroup.enums.ButtonGroupOrientation
import react.*

val PatchSetList = functionalComponent<PatchSetListProps> { props ->
    buttonGroup {
        attrs.variant = ButtonVariant.outlined
        attrs.orientation = ButtonGroupOrientation.vertical
        props.patchSets.forEach { patchSet ->
            button {
                +patchSet.name
                attrs.color = ButtonColor.primary
                (attrs as Tag).disabled = patchSet == props.currentPatchSet
                attrs.onClickFunction = { props.onSelect(patchSet) }
            }
        }
    }
}

external interface PatchSetListProps: RProps {
    var pubSub: PubSub.Client
    var patchSets: List<PatchSet>
    var currentPatchSet: PatchSet
    var onSelect: (PatchSet) -> Unit
}

fun RBuilder.patchSetList(handler: PatchSetListProps.() -> Unit): ReactElement =
    child(PatchSetList) { attrs { handler() } }
