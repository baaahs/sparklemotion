package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderInstance
import baaahs.show.mutable.ShowBuilder
import baaahs.ui.preview.gadgetsPreview
import kotlinx.css.*
import kotlinx.html.js.onChangeFunction
import materialui.components.divider.divider
import materialui.components.formcontrol.formControl
import materialui.components.formhelpertext.formHelperText
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import materialui.components.textfield.textField
import materialui.components.typography.typographyH6
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import styled.styledDiv

val ShaderInstanceEditor = xComponent<ShaderInstanceEditorProps>("ShaderInstanceEditor") { props ->
    val appContext = useContext(appContext)

    val selectedPatch = props.mutablePatch
    val shaderInstance = props.mutableShaderInstance

    val handleUpdate =
        handler("handleShaderUpdate", shaderInstance) { block: MutableShaderInstance.() -> Unit ->
            shaderInstance.block()
            forceRender()
        }

    val editingShader = ref<EditingShader> { nuffin() }
    onChange("shaderInstance", props.mutableShaderInstance) {
        val newEditingShader = EditingShader(shaderInstance, appContext.autoWirer)
        editingShader.current = newEditingShader

        val observer = newEditingShader.addObserver {
            if (it.state == EditingShader.State.Success) {
                val shader = it.previewShaderBuilder.shader
                val wiringGuess = appContext.autoWirer.autoWire(shader, focus = shader)
                    .acceptSymbolicChannelLinks()
                    .resolve()
                // TODO Improve on this.
                val editingIncomingLinks = props.mutableShaderInstance.incomingLinks
                val guessIncomingLinks = wiringGuess.mutableShaderInstances.first().incomingLinks

                editingIncomingLinks.clear()
                editingIncomingLinks.putAll(guessIncomingLinks)

            }
            forceRender()
        }
        withCleanup { observer.remove() }
    }

    styledDiv {
        css.display = Display.grid
        css.gridTemplateColumns = GridTemplateColumns("auto min-content auto")
        css.gap = Gap(1.em.toString())

        div {
            linksEditor {
                attrs.mutablePatch = selectedPatch
                attrs.showBuilder = props.showBuilder
                attrs.mutableShaderInstance = shaderInstance
                attrs.shaderChannels = props.shaderChannels
                attrs.onChange = props.onChange
            }
        }

        div {
            shaderPreview {
                attrs.previewShaderBuilder = editingShader.current.previewShaderBuilder
                attrs.width = 250.px
                attrs.height = 250.px
            }
        }

        div {
            typographyH6 { +"Meta and gadgets and stuff!" }

            gadgetsPreview {
                attrs.editingShader = editingShader.current
            }

            div(+Styles.shaderMeta) {
                formControl {
                    textField {
                        attrs.autoFocus = false
                        attrs.fullWidth = true
                        attrs.value = shaderInstance.mutableShader.title
                        attrs.onChangeFunction = { event: Event ->
                            val str = event.target!!.asDynamic().value as String
                            handleUpdate { mutableShader.title = str }
                        }
                    }
                    formHelperText { +"Shader Name" }
                }

                formControl {
                    select {
                        attrs.value(shaderInstance.shaderChannel?.id ?: "")
                        attrs.onChangeFunction = { event: Event ->
                            val channelId = event.target!!.asDynamic().value as String
                            handleUpdate {
                                shaderChannel = if (channelId.isNotBlank()) ShaderChannel(channelId) else null
                            }
                        }
                        props.shaderChannels.sortedBy { it.id }.forEach { shaderChannel ->
                            menuItem {
                                attrs["value"] = shaderChannel.id
                                +shaderChannel.id
                            }
                        }

                        divider {}
                        menuItem {
                            attrs["value"] = ""
                            +"None"
                        }
                    }
                    formHelperText { +"Channel" }
                }

                formControl {
                    textField {
                        attrs.value = shaderInstance.priority
                        attrs.onChangeFunction = { event: Event ->
                            val priorityStr = event.target!!.asDynamic().value as String
                            handleUpdate { priority = priorityStr.toFloat() }
                        }
                    }
                    formHelperText { +"Priority" }
                }
            }

        }
    }

    shaderEditor {
        attrs.editingShader = editingShader.current
        attrs.shaderChannels = props.shaderChannels
    }
}

external interface ShaderInstanceEditorProps : RProps {
    var mutablePatch: MutablePatch
    var mutableShaderInstance: MutableShaderInstance
    var shaderChannels: Set<ShaderChannel>
    var showBuilder: ShowBuilder
    var onChange: () -> Unit
}

fun RBuilder.shaderInstanceEditor(handler: RHandler<ShaderInstanceEditorProps>) =
    child(ShaderInstanceEditor, handler = handler)