package baaahs.app.ui.editor

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.app.ui.shaderCard
import baaahs.gl.openShader
import baaahs.gl.shader.type.ShaderType
import baaahs.show.Shader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShaderInstance
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.CacheBuilder
import kotlinx.html.js.onClickFunction
import materialui.components.card.card
import materialui.components.cardcontent.cardContent
import materialui.components.divider.divider
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
    val toolchain = props.editableManager.session!!.toolchain

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

    val handleNewShader = callback(props.mutablePatch, props.editableManager) { shader: MutableShader ->
        val newShaderInstance = props.mutablePatch.addShaderInstance(shader)
        handleShaderSelect[newShaderInstance].invoke()
        props.editableManager.onChange()
    }

    val newPatchCardRef = ref<Element>()
    var newPatchMenuAnchor by state<EventTarget?> { null }
    val handleNewPatchClick = callback { e: Event -> newPatchMenuAnchor = e.currentTarget }
    val handleNewPatchMenuClose = callback { _: Event, _: String -> newPatchMenuAnchor = null }
    val handleNewShaderMenuClick = callback(handleNewShader) { _: Event ->
        newPatchMenuAnchor = null
        handleNewShader(MutableShader("New Shader", ""))
    }
    val handleNewShaderFromTemplateMenuClick: CacheBuilder<ShaderType, (Event) -> Unit> = memo(handleNewShader) {
        CacheBuilder { type ->
            {
                newPatchMenuAnchor = null
                handleNewShader(type.newShaderFromTemplate())
            }
        }
    }

    var showShaderLibraryDialog by state { false }
    val handleNewFromShaderLibrary = callback { _: Event ->
        newPatchMenuAnchor = null
        showShaderLibraryDialog = true
    }
    val handleShaderLibrarySelect = callback { shader: Shader? ->
        showShaderLibraryDialog = false

        if (shader != null) {
            val newShaderInstance = props.mutablePatch.addShaderInstance(shader)
            handleShaderSelect[newShaderInstance].invoke()
            props.editableManager.onChange()
        }
    }

    div(+EditableStyles.patchOverview) {
        props.mutablePatch.mutableShaderInstances
            .map { it to toolchain.openShader(it.mutableShader) }
            .sortedWith(
                compareBy(
                    { (_, openShader) -> openShader.shaderType.displayOrder },
                    { (_, openShader) -> openShader.title }
                )
            )
            .forEach { (mutableShaderInstance, _) ->
                shaderCard {
                    key = mutableShaderInstance.id
                    attrs.mutableShaderInstance = mutableShaderInstance
                    attrs.onSelect = handleShaderSelect[mutableShaderInstance]
                    attrs.onDelete = handleShaderDelete[mutableShaderInstance]
                    attrs.toolchain = toolchain
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

            menuItem {
                attrs.onClickFunction = handleNewShaderMenuClick

                listItemIcon { icon(CommonIcons.Add) }
                listItemText { +"New Shader…" }
            }

            divider {}

            appContext.plugins.shaderTypes.all.forEach { type ->
                menuItem {
                    attrs.onClickFunction = handleNewShaderFromTemplateMenuClick[type]

                    listItemIcon { icon(type.icon) }
                    listItemText { +"New ${type.title} Shader…" }
                }
            }

            divider {}

            menuItem {
                attrs.onClickFunction = handleNewFromShaderLibrary

                listItemIcon { icon(CommonIcons.ShaderLibrary) }
                listItemText { +"From Shader Library…" }
            }

            menuItem {
                listItemIcon { icon(Icons.CloudDownload) }
                listItemText { +"Import… (TBD)" }
            }
        }
    }

    if (showShaderLibraryDialog) {
        shaderLibraryDialog {
            attrs.onSelect = handleShaderLibrarySelect
        }
    }
}

external interface PatchOverviewProps : RProps {
    var editableManager: EditableManager
    var mutablePatch: MutablePatch
}

fun RBuilder.patchOverview(handler: RHandler<PatchOverviewProps>) =
    child(PatchOverview, handler = handler)