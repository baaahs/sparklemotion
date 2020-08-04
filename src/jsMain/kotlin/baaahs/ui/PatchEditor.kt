package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.glshaders.OpenShader
import baaahs.show.PatchEditor
import baaahs.show.PatchyEditor
import baaahs.show.Shader
import baaahs.show.ShowBuilder
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

val PatchEditor = xComponent<PatchEditorProps>("PatchMappingEditor") { props ->
    val appContext = useContext(appContext)
    val showBuilder by state { ShowBuilder() }

    val patchEditor = props.patchEditor
    var selectedShaderIndex by state { -1 }

    val handleTabChange = useCallback() { event: Event, tabIndex: Int ->
        selectedShaderIndex = tabIndex
    }
    val shaderInstances = props.patchEditor.shaderInstances

    tableRow {
        attrs.key = patchEditor.id

        tdCell(PatchyStyles.patchTableSurfacesColumn on TableCellStyle.root) {
            attrs.key = "Surfaces"
            +patchEditor.surfaces.name
        }

        tdCell(PatchyStyles.patchTableShadersColumn on TableCellStyle.root) {
            attrs.key = "Shaders"

            tabs {
                attrs.variant = TabsVariant.scrollable
                attrs.value = if (selectedShaderIndex == -1) 0 else selectedShaderIndex
                attrs.onChange = handleTabChange

                shaderInstances.forEach { shaderInstance ->
                    val shader = Shader(shaderInstance.shader.shader.src)
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
                                        previewPatch.resolve().openForPreview()
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

                        attrs.items = OpenShader.Type.values().map { type ->
                            MenuItem("New ${type.name} Shader…") {
                                val newShader = Shader(type.template)
                                val contextShaders =
                                    patchEditor.shaderInstances.map { it.shader.shader } + newShader
                                val unresolvedPatchEditor = appContext.autoWirer.autoWire(
                                    *contextShaders.toTypedArray(),
                                    focus = newShader
                                )
                                patchEditor.addShaderInstance(newShader) {
                                    // TODO: Something better than this.
                                    val resolved = unresolvedPatchEditor
                                        .acceptSymbolicChannelLinks()
                                        .resolve()
                                        .shaderInstances[0]
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
                val shaderInstanceEditor = shaderInstances[selectedShaderIndex]

                expansionPanel {
                    expansionPanelSummary {
                        attrs.expandIcon { icon(ExpandMore) }
                        +"Links"
                    }

                    expansionPanelDetails {
                        linksEditor {
                            attrs.patchEditor = patchEditor
                            attrs.showBuilder = showBuilder
                            attrs.shaderInstance = shaderInstanceEditor
                            attrs.shaderChannels = props.patchyEditor.findShaderChannels()
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
                            attrs.shaderEditor = selectedShader.shader
                            attrs.onChange = props.onChange
                        }
                    }
                }
            }
        }
    }
}

external interface PatchEditorProps : RProps {
    var patchEditor: PatchEditor
    var patchyEditor: PatchyEditor
    var onChange: () -> Unit
}

fun RBuilder.patchEditor(handler: RHandler<PatchEditorProps>) =
    child(PatchEditor, handler = handler)