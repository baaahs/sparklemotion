package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.gl.glsl.GlslType
import baaahs.plugin.PluginRef
import baaahs.show.mutable.EditingShader
import baaahs.ui.markdown
import baaahs.ui.xComponent
import kotlinx.css.*
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.code
import react.useContext
import styled.StyleSheet
import styled.inlineStyles

val ShaderHelp = xComponent<ShaderHelpProps>("ShaderHelp") { props ->
    val appContext = useContext(appContext)

    table {
        tableHead {
            tableRow {
                thCell { +"Data Source" }
                thCell { +"Description" }
                thCell { +"Content Type" }
            }
        }

        tableBody {
            appContext.plugins.dataSourceBuilders.withPlugin
                .filterNot { (_, v) -> v.internalOnly }
                .sortedBy { (_, v) -> v.title }
                .forEach { (plugin, dataSourceBuilder) ->
                    tableRow {
                        tdCell { +dataSourceBuilder.title }
                        val contentType = dataSourceBuilder.contentType
                        tdCell {
                            markdown { +dataSourceBuilder.description }
                            code {
                                inlineStyles { whiteSpace = WhiteSpace.pre }

                                val type = contentType.glslType
                                val pluginRef = PluginRef(plugin.packageName, dataSourceBuilder.resourceName)

                                if (type is GlslType.Struct) {
                                    type.toGlsl(null, emptySet())
                                        .trim()
                                        .split("\n")
                                        .forEach { line ->
                                            +line
                                            +"\n"
                                        }
                                }
                                val varName = dataSourceBuilder.resourceName.replaceFirstChar { it.lowercase() }
                                +"uniform ${type.glslLiteral} $varName; // @@${pluginRef.shortRef()}\n"
                            }
                        }
                        tdCell { +contentType.id }
                    }
                }
        }
    }
}

object ShaderHelpStyles : StyleSheet("app-ui-editor-ShaderHelp", isStatic = true) {
    val shaderHelp by css {
        display = Display.flex
        flexDirection = FlexDirection.row
        gap = 1.em
    }
}

external interface ShaderHelpProps : Props {
    var editingShader: EditingShader
}

fun RBuilder.shaderHelp(handler: RHandler<ShaderHelpProps>) =
    child(ShaderHelp, handler = handler)