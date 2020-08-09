package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.show.ShaderType
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderInstance
import kotlinx.html.js.onClickFunction
import materialui.AddCircleOutline
import materialui.components.avatar.avatar
import materialui.components.card.card
import materialui.components.cardheader.cardHeader
import materialui.components.paper.enums.PaperStyle
import materialui.components.typography.enums.TypographyColor
import materialui.components.typography.enums.TypographyDisplay
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.icon
import org.w3c.dom.events.Event
import react.*
import react.dom.div

val PatchOverview = xComponent<PatchOverviewProps>("PatchOverview") { props ->
    val mutablePatch = props.mutablePatch

    observe(mutablePatch)

    val appContext = useContext(appContext)
    val styles = PatchHolderEditorStyles

    val sortedShaderInstances =
        mutablePatch.mutableShaderInstances.sortedBy { it.mutableShader.type.priority }

    val handleSelects by state { hashMapOf<MutableShaderInstance, (Event) -> Unit>() }

    val x = this
    sortedShaderInstances.forEach { mutableShaderInstance ->
        val mutableShader = mutableShaderInstance.mutableShader

        observe(mutableShaderInstance, mutableShader)

        card(+styles.shaderCard on PaperStyle.root) {
            key = mutableShaderInstance.id

            attrs.onClickFunction = handleSelects.getOrPut(mutableShaderInstance) {
                { props.onSelectShaderInstance(mutableShaderInstance) }
            }

            cardHeader {
                attrs.avatar {
                    avatar { icon(Icons.forShader(mutableShader.type)) }
                }
                attrs.title { +mutableShader.title }
            }

            shaderPreview {
                attrs.shader = mutableShader.build()
                attrs.width = styles.cardWidth
                attrs.height = styles.cardWidth
            }

            div(+styles.shaderCardContent) {
                typography {
                    attrs.display = TypographyDisplay.block
                    attrs.variant = TypographyVariant.body2
                    attrs.color = TypographyColor.textSecondary
                    +"${mutableShader.type.name} Shader"
                }
            }
        }
    }

    card(+styles.shaderCard on PaperStyle.root) {
        menuButton {
            attrs.icon = AddCircleOutline
            attrs.label = "New Shader…"

            attrs.items = ShaderType.values().map { type ->
                MenuItem("New ${type.name} Shader…") {
                    val newShader = type.shaderFromTemplate().build()
                    val contextShaders =
                        mutablePatch.mutableShaderInstances.map { it.mutableShader.build() } + newShader
                    val unresolvedPatch = appContext.autoWirer.autoWire(
                        *contextShaders.toTypedArray(),
                        focus = newShader
                    )
                    mutablePatch.addShaderInstance(newShader) {
                        // TODO: Something better than this.
                        val resolved = unresolvedPatch
                            .acceptSymbolicChannelLinks()
                            .resolve()
                            .mutableShaderInstances[0]
                        incomingLinks.putAll(resolved.incomingLinks)
                        shaderChannel = resolved.shaderChannel
                    }
                    x.forceRender()
                }
            } + MenuItem("Import…") { }
        }

        typography {
            attrs.display = TypographyDisplay.block
            attrs.variant = TypographyVariant.subtitle1
            +"New Shader…"
        }
    }
}

external interface PatchOverviewProps : RProps {
    var mutablePatch: MutablePatch
    var onSelectShaderInstance: (MutableShaderInstance) -> Unit
}

fun RBuilder.patchOverview(handler: RHandler<PatchOverviewProps>) =
    child(PatchOverview, handler = handler)