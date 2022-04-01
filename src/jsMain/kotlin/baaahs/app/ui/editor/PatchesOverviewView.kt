package baaahs.app.ui.editor

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.app.ui.shaderCard
import baaahs.gl.openShader
import baaahs.gl.shader.type.ShaderType
import baaahs.show.Shader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShader
import baaahs.ui.on
import baaahs.ui.sharedGlContext
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
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val PatchesOverview = xComponent<PatchesOverviewProps>("PatchesOverview") { props ->
    val appContext = useContext(appContext)
    val styles = EditableStyles
    val toolchain = (props.editableManager.session!! as ShowEditableManager.ShowSession).toolchain

    val handleShaderSelect: CacheBuilder<MutablePatch, () -> Unit> =
        CacheBuilder {
            {
                props.editableManager.openPanel(it.getEditorPanel(props.editableManager))
            }
        }
    val handleShaderDelete: CacheBuilder<MutablePatch, () -> Unit> =
        CacheBuilder {
            {
                props.mutablePatchHolder.patches.remove(it)
                props.editableManager.onChange()
            }
        }

    val handleNewShader = callback(props.mutablePatchHolder, props.editableManager) { shader: MutableShader ->
        val newPatch = MutablePatch(shader)
        props.mutablePatchHolder.addPatch(newPatch)
        handleShaderSelect[newPatch].invoke()
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
    val handleShaderLibrarySelect = callback(props.mutablePatchHolder, props.editableManager) { shader: Shader? ->
        showShaderLibraryDialog = false

        if (shader != null) {
            val newPatch = MutablePatch(shader)
            props.mutablePatchHolder.addPatch(newPatch)
            handleShaderSelect[newPatch].invoke()
            props.editableManager.onChange()
        }
    }

    sharedGlContext {
        attrs.inFront = true

        div(+EditableStyles.patchOverview) {
            props.mutablePatchHolder.patches
                .map { it to toolchain.openShader(it.mutableShader) }
                .sortedWith(
                    compareBy(
                        { (_, openShader) -> openShader.shaderType.displayOrder },
                        { (_, openShader) -> openShader.title }
                    )
                )
                .forEach { (mutablePatch, _) ->
                    shaderCard {
                        key = mutablePatch.id
                        attrs.mutablePatch = mutablePatch
                        attrs.onSelect = handleShaderSelect[mutablePatch]
                        attrs.onDelete = handleShaderDelete[mutablePatch]
                        attrs.toolchain = toolchain
                    }
                }

            card(+styles.shaderCard on PaperStyle.root) {
                key = "new patch"
                ref = newPatchCardRef

                attrs.onClickFunction = handleNewPatchClick

                cardContent {
                    icon(materialui.icons.AddCircleOutline)
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
                    listItemIcon { icon(materialui.icons.CloudDownload) }
                    listItemText { +"Import… (TBD)" }
                }
            }
        }
    }

    if (showShaderLibraryDialog) {
        shaderLibraryDialog {
            attrs.onSelect = handleShaderLibrarySelect
        }
    }
}

external interface PatchesOverviewProps : Props {
    var editableManager: EditableManager<*>
    var mutablePatchHolder: MutablePatchHolder
}

fun RBuilder.patchesOverview(handler: RHandler<PatchesOverviewProps>) =
    child(PatchesOverview, handler = handler)