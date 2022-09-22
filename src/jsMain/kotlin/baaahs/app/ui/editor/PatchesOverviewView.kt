package baaahs.app.ui.editor

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.app.ui.shaderCard
import baaahs.gl.openShader
import baaahs.gl.shader.type.ShaderType
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShader
import baaahs.ui.*
import baaahs.util.CacheBuilder
import kotlinx.js.jso
import materialui.icon
import mui.icons.material.AddCircleOutline
import mui.icons.material.CloudDownload
import mui.material.*
import org.w3c.dom.Element
import kotlinx.html.org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.html.ReactHTML
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
    var newPatchMenuAnchor by state<Element?> { null }
    val handleNewPatchClick by eventHandler { e: Event -> newPatchMenuAnchor = e.currentTarget as Element? }
    val handleNewPatchMenuClose = callback { _: Event, _: String -> newPatchMenuAnchor = null }
    val handleNewShaderMenuClick by eventHandler(handleNewShader) { _: Event ->
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
            props.editableManager.maybeChangeTitle(to = newPatch.title)

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

            if (props.mutablePatchHolder.patches.isEmpty()) {
                Card {
                    MenuList {
                        populateNewShaderMenu(
                            appContext.plugins.shaderTypes,
                            handleNewShaderMenuClick,
                            handleNewShaderFromTemplateMenuClick,
                            handleNewFromShaderLibrary
                        )
                    }
                }
            } else {
                Card {
                    attrs.classes = jso { this.root = -styles.shaderCard }
                    key = "new patch"
                    ref = newPatchCardRef

                    attrs.onClick = handleNewPatchClick.withMouseEvent()

                    CardContent {
                        icon(AddCircleOutline)
                        typographySubtitle1 {
                            attrs.component = ReactHTML.div
                            +"New Shader…"
                        }
                    }

                    Menu {
                        attrs.anchorEl = newPatchMenuAnchor.asDynamic()
                        attrs.open = newPatchMenuAnchor != null
                        attrs.onClose = handleNewPatchMenuClose

                        populateNewShaderMenu(
                            appContext.plugins.shaderTypes,
                            handleNewShaderMenuClick,
                            handleNewShaderFromTemplateMenuClick,
                            handleNewFromShaderLibrary
                        )
                    }
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

private fun RBuilder.populateNewShaderMenu(
    shaderTypes: Plugins.ShaderTypes,
    handleNewShaderMenuClick: (Event) -> Unit,
    handleNewShaderFromTemplateMenuClick: CacheBuilder<ShaderType, (Event) -> Unit>,
    handleNewFromShaderLibrary: (Event) -> Unit
) {
    MenuItem {
        attrs.onClick = handleNewShaderMenuClick.withMouseEvent()

        ListItemIcon { icon(CommonIcons.Add) }
        ListItemText { +"New Shader…" }
    }

    Divider {}

    shaderTypes.all.forEach { type ->
        MenuItem {
            attrs.onClick = handleNewShaderFromTemplateMenuClick[type].withMouseEvent()

            ListItemIcon { icon(type.icon) }
            ListItemText { +"New ${type.title} Shader…" }
        }
    }

    Divider {}

    MenuItem {
        attrs.onClick = handleNewFromShaderLibrary.withMouseEvent()

        ListItemIcon { icon(CommonIcons.ShaderLibrary) }
        ListItemText { +"From Shader Library…" }
    }

    MenuItem {
        ListItemIcon { icon(CloudDownload) }
        ListItemText { +"Import… (TBD)" }
    }
}

external interface PatchesOverviewProps : Props {
    var editableManager: EditableManager<*>
    var mutablePatchHolder: MutablePatchHolder
}

fun RBuilder.patchesOverview(handler: RHandler<PatchesOverviewProps>) =
    child(PatchesOverview, handler = handler)