package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.gl.glsl.GlslType
import baaahs.plugin.PluginRef
import baaahs.show.mutable.EditingShader
import baaahs.ui.markdown
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import kotlinx.css.properties.TextDecorationLine.lineThrough
import materialui.components.table.enums.TablePadding
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import materialui.styles.muitheme.MuiTheme
import materialui.styles.palette.contrastText
import materialui.styles.palette.main
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.code
import react.dom.pre
import react.dom.setProp
import react.dom.span
import react.useContext
import styled.StyleSheet

private val ShaderHelpView = xComponent<ShaderHelpProps>("ShaderHelp") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.shaderHelp

    table {
        attrs.padding = TablePadding.dense
        setProp("stickyHeader", true)

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
                            pre(+styles.code) {
                                val type = contentType.glslType
                                val pluginRef = PluginRef(plugin.packageName, dataSourceBuilder.resourceName)

                                if (type is GlslType.Struct) {
                                    code { +"struct ${type.name} {\n" }

                                    type.fields.forEach { field ->
                                        val typeStr = if (field.type is GlslType.Struct) field.type.name else field.type.glslLiteral
                                        val style = if (field.deprecated) styles.deprecated else styles.normal
                                        val comment = if (field.deprecated) "Deprecated. ${field.description}" else field.description

                                        code {
                                            +"    "
                                            span(+style) { +"$typeStr ${field.name};" }
                                            comment?.run { +" "; span(+styles.comment) { +"// $comment" } }
                                            +"\n"
                                        }
                                    }
                                    code { +"}\n" }
                                }
                                val varName = dataSourceBuilder.resourceName.replaceFirstChar { it.lowercase() }

                                code {
                                    +"uniform ${type.glslLiteral} $varName; "
                                    span(+styles.comment) { +"// @@${pluginRef.shortRef()}\n" }
                                }
                            }
                        }
                        tdCell { code { +contentType.id } }
                    }
                }
        }
    }
}

class ShaderHelpStyles(
    private val theme: MuiTheme
) : StyleSheet("app-ui-editor-ShaderHelp", isStatic = true) {
    val code by css {
        whiteSpace = WhiteSpace.preWrap
        color = theme.palette.info.contrastText
        backgroundColor = theme.palette.info.main
        padding = 0.5.em.value
        border = "2px inset ${theme.palette.info.main.value}"

        // Line numbers:
        before {
            declarations["counterReset"] = "listing"
        }

        code {
            declarations["counterIncrement"] = "listing"

            before {
                declarations["content"] = "counter(listing) \". \""
                display = Display.inlineBlock
                width = 2.em
                paddingLeft = LinearDimension.auto
                marginLeft = LinearDimension.auto
                textAlign = TextAlign.right
            }
        }
    }

    val normal by css {}

    val deprecated by css {
        textDecoration = TextDecoration(setOf(lineThrough))
        declarations["textDecorationThickness"] = "1px"
    }

    val comment by css {
        color = theme.palette.info.contrastText.withAlpha(.75)
    }
}

external interface ShaderHelpProps : Props {
    var editingShader: EditingShader
}

fun RBuilder.shaderHelp(handler: RHandler<ShaderHelpProps>) =
    child(ShaderHelpView, handler = handler)