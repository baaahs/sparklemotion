package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.show.ShaderType
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.ShowBuilder
import kotlinx.css.px
import kotlinx.html.js.onClickFunction
import materialui.*
import materialui.components.container.container
import materialui.components.expansionpanel.expansionPanel
import materialui.components.expansionpaneldetails.expansionPanelDetails
import materialui.components.expansionpanelsummary.expansionPanelSummary
import materialui.components.tab.tab
import materialui.components.tablecell.enums.TableCellStyle
import materialui.components.tablecell.tdCell
import materialui.components.tablerow.tableRow
import materialui.components.tabs.enums.TabsVariant
import materialui.components.tabs.tabs
import materialui.components.typography.typography
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.key

val PatchEditor = xComponent<PatchEditorProps>("PatchEditor") { props ->
    val appContext = useContext(appContext)
    val showBuilder by state { ShowBuilder() }

    val mutablePatch = props.mutablePatch
    var selectedShaderIndex by state { -1 }

    val handleTabChange = useCallback() { event: Event, tabIndex: Int ->
        selectedShaderIndex = tabIndex
    }
    val shaderInstances = props.mutablePatch.mutableShaderInstances

    tableRow {
        attrs.key = mutablePatch.id

        tdCell(PatchHolderStyles.patchTableSurfacesColumn on TableCellStyle.root) {
            attrs.key = "Surfaces"
            +mutablePatch.surfaces.name
        }

        tdCell(PatchHolderStyles.patchTableShadersColumn on TableCellStyle.root) {
            attrs.key = "Shaders"

            tabs {
                attrs.variant = TabsVariant.scrollable
                attrs.value = if (selectedShaderIndex == -1) 0 else selectedShaderIndex
                attrs.onChange = handleTabChange

                shaderInstances.forEach { shaderInstance ->
                    val shader = shaderInstance.mutableShader.build()
                    val openShader = appContext.showPlayer.openShaderOrNull(shader, addToCache = false)

                    tab {
//                        var name = shaderInstance.name
//                        if (shaderInstance.isModified) name += " *"
                        attrs.label {
                            div(+Styles.shaderTab) {
                                div(+baaahs.app.ui.controls.Styles.editButton) {
                                    icon(Edit)
                                    attrs.onClickFunction = {}
                                }

                                div {
                                    val linkedPatch = openShader?.let {
                                        val previewPatch = appContext.autoWirer.autoWire(openShader)
                                        previewPatch.resolve().openForPreview(appContext.autoWirer)
                                    }
                                    if (linkedPatch != null) {
                                        patchPreview {
                                            attrs.patch = linkedPatch
                                            attrs.width = 60.px
                                            attrs.height = 35.px
                                            attrs.onSuccess = {}
                                            attrs.onGadgetsChange = {}
                                            attrs.onError = {}
                                        }
                                    } else {
                                        icon(Warning)
                                    }
                                }

                                typography { +(openShader?.title ?: "Unknown") }
                            }
                        }
                    }
                }

                container {
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
                                this@xComponent.forceRender()
                            }
                        } + MenuItem("Import…") { }
                    }
                }
            }

            if (selectedShaderIndex != -1) {
                val selectedShader = shaderInstances[selectedShaderIndex]
                val mutableShaderInstance = shaderInstances[selectedShaderIndex]

                expansionPanel {
                    expansionPanelSummary {
                        attrs.expandIcon { icon(ExpandMore) }
                        +"Links"
                    }

                    expansionPanelDetails {
                        linksEditor {
                            attrs.mutablePatch = mutablePatch
                            attrs.showBuilder = showBuilder
                            attrs.mutableShaderInstance = mutableShaderInstance
                            attrs.shaderChannels = props.mutablePatchHolder.findShaderChannels()
                            attrs.onChange = props.onChange
                        }
                    }
                }

                expansionPanel {
                    expansionPanelSummary {
                        attrs.expandIcon { icon(ExpandMore) }
                        +"Code"
                    }

                    expansionPanelDetails {
                        shaderEditor {
                            attrs.mutableShader = selectedShader.mutableShader
                            attrs.onChange = props.onChange
                        }
                    }
                }
            }
        }
    }
}

external interface PatchEditorProps : RProps {
    var mutablePatch: MutablePatch
    var mutablePatchHolder: MutablePatchHolder
    var onChange: () -> Unit
}

fun RBuilder.patchEditor(handler: RHandler<PatchEditorProps>) =
    child(PatchEditor, handler = handler)