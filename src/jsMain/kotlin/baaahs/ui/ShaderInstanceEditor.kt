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

    val updateShaderInstance =
        handler("updateShaderInstance", props.mutableShaderInstance) { block: MutableShaderInstance.() -> Unit ->
            props.mutableShaderInstance.block()
            forceRender()
        }

    val editingShader = ref<EditingShader> { nuffin() }
    onChange("shaderInstance", props.mutableShaderInstance) {
        val newEditingShader = EditingShader(props.mutableShaderInstance, appContext.autoWirer)
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
                attrs.siblingMutableShaderInstances = props.mutablePatch.mutableShaderInstances
                attrs.showBuilder = props.showBuilder
                attrs.mutableShaderInstance = props.mutableShaderInstance
                attrs.shaderChannels = props.shaderChannels
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
                        attrs.value = props.mutableShaderInstance.mutableShader.title
                        attrs.onChangeFunction = { event: Event ->
                            val str = event.target!!.asDynamic().value as String
                            updateShaderInstance { mutableShader.title = str }
                        }
                    }
                    formHelperText { +"Shader Name" }
                }

                formControl {
                    select {
                        attrs.value(props.mutableShaderInstance.shaderChannel?.id ?: "")
                        attrs.onChangeFunction = { event: Event ->
                            val channelId = event.target!!.asDynamic().value as String
                            updateShaderInstance {
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
                        attrs.value = props.mutableShaderInstance.priority
                        attrs.onChangeFunction = { event: Event ->
                            val priorityStr = event.target!!.asDynamic().value as String
                            updateShaderInstance { priority = priorityStr.toFloat() }
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
}

fun RBuilder.shaderInstanceEditor(handler: RHandler<ShaderInstanceEditorProps>) =
    child(ShaderInstanceEditor, handler = handler)