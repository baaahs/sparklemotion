package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderCard
import baaahs.gl.shader.type.ShaderType
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderInstance
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.CacheBuilder
import kotlinx.html.js.onClickFunction
import materialui.components.card.card
import materialui.components.cardcontent.cardContent
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.menu.menu
import materialui.components.menuitem.menuItem
import materialui.components.paper.enums.PaperStyle
import materialui.components.typography.enums.TypographyDisplay
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.icon
import materialui.icons.Icons
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.*
import react.dom.div

val PatchOverview = xComponent<PatchOverviewProps>("PatchOverview") { props ->
    val appContext = useContext(appContext)
    val styles = EditableStyles

    val handleShaderSelect: CacheBuilder<MutableShaderInstance, () -> Unit> =
        CacheBuilder {
            {
                props.editableManager.openPanel(
                    it.getEditorPanel(props.mutablePatch.getEditorPanel(props.editableManager))
                )
            }
        }
    val handleShaderDelete: CacheBuilder<MutableShaderInstance, () -> Unit> =
        CacheBuilder {
            {
                props.mutablePatch.remove(it)
                props.editableManager.onChange()
            }
        }

    val newPatchCardRef = ref<Element>()
    var newPatchMenuAnchor by state<EventTarget?> { null }
    val handleNewPatchClick = baaahs.ui.useCallback { e: Event -> newPatchMenuAnchor = e.currentTarget }
    val handleNewPatchMenuClose = baaahs.ui.useCallback { _: Event, _: String -> newPatchMenuAnchor = null }
    val handleNewShaderMenuClick: CacheBuilder<ShaderType, (Event) -> Unit> =
        CacheBuilder { type ->
            {
                newPatchMenuAnchor = null
                val newShaderInstance = props.mutablePatch.addShaderInstance(type.newShaderFromTemplate())
                handleShaderSelect[newShaderInstance].invoke()
                props.editableManager.onChange()
            }
        }

    div(+EditableStyles.patchOverview) {
        props.mutablePatch.mutableShaderInstances
            .sortedWith(MutableShaderInstance.defaultOrder)
            .forEach { mutableShaderInstance ->
                shaderCard {
                    key = mutableShaderInstance.id
                    attrs.mutableShaderInstance = mutableShaderInstance
                    attrs.onSelect = handleShaderSelect[mutableShaderInstance]
                    attrs.onDelete = handleShaderDelete[mutableShaderInstance]
                }
            }

        card(+styles.shaderCard on PaperStyle.root) {
            key = "new patch"
            ref = newPatchCardRef

            attrs.onClickFunction = handleNewPatchClick

            cardContent {
                icon(Icons.AddCircleOutline)
                typography {
                    attrs.display = TypographyDisplay.block
                    attrs.variant = TypographyVariant.subtitle1
                    +"New Shader…"
                }
            }
        }

        menu {
            attrs.getContentAnchorEl = null
            attrs.anchorEl(newPatchMenuAnchor)
            attrs.open = newPatchMenuAnchor != null
            attrs.onClose = handleNewPatchMenuClose

            appContext.plugins.shaderTypes.all.forEach { type ->
                menuItem {
                    attrs.onClickFunction = handleNewShaderMenuClick[type]

                    listItemIcon { icon(type.icon) }
                    listItemText { +"New ${type.title} Shader…" }
                }
            }

            menuItem {
                listItemIcon { icon(Icons.CloudDownload) }
                listItemText { +"Import… (TBD)" }
            }
        }
    }
}

external interface PatchOverviewProps : RProps {
    var editableManager: EditableManager
    var mutablePatch: MutablePatch
}

fun RBuilder.patchOverview(handler: RHandler<PatchOverviewProps>) =
    child(PatchOverview, handler = handler)