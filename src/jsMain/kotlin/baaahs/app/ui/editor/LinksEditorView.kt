package baaahs.app.ui.editor

import baaahs.app.ui.Colors
import baaahs.gl.shader.InputPort
import baaahs.show.mutable.EditingShader
import baaahs.ui.*
import js.core.jso
import materialui.icon
import mui.material.*
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.b
import react.dom.code
import web.cssom.px

private val LinksEditor = xComponent<LinksEditorProps>("LinksEditor") { props ->
    observe(props.editingShader)

    val lastShaderInputPorts = ref<List<InputPort>>()
    val shaderInputPorts = props.editingShader.inputPorts
        ?.also { lastShaderInputPorts.current = it }
        ?: lastShaderInputPorts.current

    if (shaderInputPorts == null) {
        CircularProgress {}
        typographyH6 { +"Analyzing Shader…" }
    } else Table {
        attrs.size = Size.small
        attrs.padding = TablePadding.none
        attrs.stickyHeader = true
        attrs.sx { borderSpacing = 3.px }

        TableHead {
            TableRow {
                TableCell { typographySubtitle2 { +"Port" } }
                TableCell { typographySubtitle2 { +"Source" } }
            }
        }

        TableBody {
            shaderInputPorts.forEach { inputPort ->
                TableRow {
                    TableCell {
                        Typography {

                        }
                        typographyBody1 { b { +inputPort.title } }
                        typographyBody2 {
                            if (inputPort.contentType.isUnknown()) {
                                attrs.sx = jso {
                                    color = Colors.error
                                }
                            }

                            +"(${inputPort.contentType.title})"
                        }
                    }

                    TableCell {
                        linkSourceEditor {
                            key = inputPort.id
                            attrs.editableManager = props.editableManager
                            attrs.editingShader = props.editingShader
                            attrs.inputPort = inputPort
                        }
                    }
                }
            }

            val extraLinks = props.editingShader.extraLinks
            if (extraLinks.isNotEmpty()) {
                TableRow {
                    TableCell { typographySubtitle2 { +"Unknown Port" } }
                    TableCell { typographySubtitle2 { +"Old Source" } }
                }

                extraLinks.forEach { (portId, link) ->
                    TableRow {
                        TableCell {
                            Typography {
                                attrs.sx = jso { color = Colors.error }

                                code { +portId }
                            }
                        }

                        TableCell {
                            Typography { +link.title }

                            IconButton {
                                attrs.onClick = { _ ->
                                    props.editingShader.changeInputPortLink(portId, null)
                                    props.editableManager.onChange()
                                }
                                icon(mui.icons.material.Delete)
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface LinksEditorProps : Props {
    var editableManager: EditableManager<*>
    var editingShader: EditingShader
}

fun RBuilder.linksEditor(handler: RHandler<LinksEditorProps>) =
    child(LinksEditor, handler = handler)
