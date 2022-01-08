package baaahs.app.ui.editor

import baaahs.app.ui.PatchHolderEditorHelpText
import baaahs.show.Show
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.ui.*
import kotlinx.html.js.onClickFunction
import materialui.components.list.enums.ListStyle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.icon
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.br
import react.dom.header

val FixturesList = xComponent<FixturesListProps>("FixturesList") { props ->
    list(EditableStyles.tabsList on ListStyle.root) {
        header {
            +"Fixtures"
            br {}

            help {
                attrs.divClass = Styles.helpInline.name
                attrs.inject(PatchHolderEditorHelpText.fixtures)
            }
        }

        props.mutablePatchHolder.patches.forEach { mutablePatch ->
            listItem {
                attrs.button = true
                attrs.onClickFunction = {
                    props.editableManager.openPanel(mutablePatch.getEditorPanel(props.editableManager))
                }

                listItemIcon { icon(materialui.icons.FilterList) }
                listItemText { +mutablePatch.surfaces.name }
            }
        }

        listItem {
            listItemIcon { icon(materialui.icons.AddCircleOutline) }
            listItemText { +"New…" }

            attrs.onClickFunction = {
                val newPatch = MutablePatch()
                props.mutablePatchHolder.patches.add(newPatch)
                props.editableManager.openPanel(newPatch.getEditorPanel(props.editableManager))
            }
        }
    }
}

external interface FixturesListProps : Props {
    var editableManager: EditableManager<Show>
    var mutablePatchHolder: MutablePatchHolder
}

fun RBuilder.fixturesList(handler: RHandler<FixturesListProps>) =
    child(FixturesList, handler = handler)