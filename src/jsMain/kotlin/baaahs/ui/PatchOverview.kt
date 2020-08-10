package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.show.ShaderType
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderInstance
import kotlinx.html.js.onClickFunction
import materialui.AddCircleOutline
import materialui.CloudDownload
import materialui.Delete
import materialui.components.avatar.avatar
import materialui.components.card.card
import materialui.components.cardactions.cardActions
import materialui.components.cardcontent.cardContent
import materialui.components.cardheader.cardHeader
import materialui.components.iconbutton.iconButton
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.menu.menu
import materialui.components.menuitem.menuItem
import materialui.components.paper.enums.PaperStyle
import materialui.components.typography.enums.TypographyColor
import materialui.components.typography.enums.TypographyDisplay
import materialui.components.typography.enums.TypographyStyle
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.icon
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.*

val PatchOverview = xComponent<PatchOverviewProps>("PatchOverview") { props ->
    val appContext = useContext(appContext)
    val styles = PatchHolderEditorStyles

    val handleShaderInstanceClicks: CacheBuilder<MutableShaderInstance, (Event) -> Unit> =
        CacheBuilder { { _: Event -> props.onSelectShaderInstance(it) } }
    val handleShaderDeleteClicks: CacheBuilder<MutableShaderInstance, (Event) -> Unit> =
        CacheBuilder { { e: Event ->
            props.mutablePatch.remove(it)
            e.stopPropagation()
            forceRender()
        } }

    val newPatchCardRef = ref<Element>()
    var newPatchMenuAnchor by state<EventTarget?> { null }
    val handleNewPatchClick = useCallback { e: Event -> newPatchMenuAnchor = e.currentTarget }
    val handleNewPatchMenuClose = useCallback { _: Event, _: String -> newPatchMenuAnchor = null }
    val handleNewShaderMenuClick: CacheBuilder<ShaderType, (Event) -> Unit> =
        CacheBuilder { type ->
            {
                newPatchMenuAnchor = null
                val newShader = type.shaderFromTemplate().build()
                val contextShaders =
                    props.mutablePatch.mutableShaderInstances.map { it.mutableShader.build() } + newShader
                val unresolvedPatch = appContext.autoWirer.autoWire(
                    *contextShaders.toTypedArray()
                )
                val newShaderInstance = props.mutablePatch.addShaderInstance(newShader) {
                    // TODO: Something better than this.
                    val resolved = unresolvedPatch
                        .acceptSymbolicChannelLinks()
                        .resolve()
                        .mutableShaderInstances[0]
                    incomingLinks.putAll(resolved.incomingLinks)
                    shaderChannel = resolved.shaderChannel
                }
                props.onSelectShaderInstance(newShaderInstance)
            }
        }

    props.mutablePatch.mutableShaderInstances
        .sortedBy { it.mutableShader.type.priority }
        .forEach { mutableShaderInstance ->
            val shader = mutableShaderInstance.mutableShader.build()

            card(+styles.shaderCard on PaperStyle.root) {
                key = mutableShaderInstance.id

                attrs.onClickFunction = handleShaderInstanceClicks[mutableShaderInstance]

                cardHeader {
                    attrs.avatar {
                        avatar { icon(Icons.forShader(shader.type)) }
                    }
                    attrs.title { +shader.title }
//                                attrs.subheader { +"${shader.type.name} Shader" }
                }

                shaderPreview {
                    attrs.shader = shader
                    attrs.width = styles.cardWidth
                    attrs.height = styles.cardWidth
                }

                cardActions {
                    typography(styles.shaderCardContent on TypographyStyle.root) {
                        attrs.display = TypographyDisplay.block
                        attrs.variant = TypographyVariant.body2
                        attrs.color = TypographyColor.textSecondary
                        +"${shader.type.name} Shader"
                    }

                    iconButton {
                        attrs.onClickFunction = handleShaderDeleteClicks[mutableShaderInstance]

                        icon(Delete)
                    }
                }
            }
        }

    card(+styles.shaderCard on PaperStyle.root) {
        key = "new patch"
        ref = newPatchCardRef

        attrs.onClickFunction = handleNewPatchClick

        cardContent {
            icon(AddCircleOutline)
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

        ShaderType.values().forEach { type ->
            menuItem {
                attrs.onClickFunction = handleNewShaderMenuClick[type]

                listItemIcon { icon(Icons.forShader(type)) }
                listItemText { +"New ${type.name} Shader…" }
            }
        }

        menuItem {
            listItemIcon { icon(CloudDownload) }
            listItemText { +"Import… (TBD)" }
        }
    }
}

class CacheBuilder<K, V>(val createFn: (K) -> V) {
    private val map = mutableMapOf<K, V>()

    operator fun get(key: K): V {
        return map.getOrPut(key) { createFn(key) }
    }
}

external interface PatchOverviewProps : RProps {
    var mutablePatch: MutablePatch
    var onSelectShaderInstance: (MutableShaderInstance) -> Unit
}

fun RBuilder.patchOverview(handler: RHandler<PatchOverviewProps>) =
    child(PatchOverview, handler = handler)