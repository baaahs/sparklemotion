package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.gl.glsl.GlslType
import baaahs.plugin.PluginRef
import baaahs.ui.*
import baaahs.window
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import kotlinx.css.properties.TextDecorationLine.lineThrough
import kotlinx.js.jso
import mui.material.*
import mui.material.styles.Theme
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.code
import react.dom.div
import react.dom.pre
import react.dom.span
import react.useContext
import styled.StyleSheet

private val ShaderHelpView = xComponent<ShaderHelpProps>("ShaderHelp", isPure = true) { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.shaderHelp

    Table {
        attrs.stickyHeader = true

        TableHead {
            TableRow {
                TableCell { +"Data Source" }
                TableCell { +"Description" }
                TableCell { +"Content Type" }
            }
        }

        TableBody {
            appContext.plugins.dataSourceBuilders.withPlugin
                .filterNot { (_, v) -> v.internalOnly }
                .sortedBy { (_, v) -> v.title }
                .forEach { (plugin, dataSourceBuilder) ->
                    TableRow {
                        TableCell { +dataSourceBuilder.title }
                        val contentType = dataSourceBuilder.contentType
                        TableCell {
                            markdown { +dataSourceBuilder.description }
                            div(+styles.codeContainer) {
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
                                        code { +"};\n" }
                                    }
                                    val varName = dataSourceBuilder.resourceName.replaceFirstChar { it.lowercase() }

                                    code {
                                        val funDef = dataSourceBuilder.funDef(varName)
                                    if (funDef != null) {
                                        +funDef
                                        +" "
                                    } else {
                                        +"uniform ${type.glslLiteral} $varName; "
                                    }
                                        span(+styles.comment) { +"// @@${pluginRef.shortRef()}\n" }
                                    }
                                }

                                Button {
                                    attrs.classes = jso { this.root = -styles.copyButton }
                                    attrs.onClick = { event ->
                                        val target = event.currentTarget as HTMLElement?
                                        val pre = target
                                            ?.parentElement
                                            ?.getElementsByTagName("pre")
                                            ?.get(0) as HTMLElement?
                                        pre?.innerText?.let {
                                            window.navigator.clipboard.writeText(it)
                                            target?.innerText = "Copied!"
                                        }
                                    }
                                    +"Copy…"
                                }
                            }
                        }
                        TableCell { code { +contentType.id } }
                    }
                }
        }
    }
}

class ShaderHelpStyles(
    private val theme: Theme
) : StyleSheet("app-ui-editor-ShaderHelp", isStatic = true) {
    val codeContainer by css {
        position = Position.relative
        color = Color(theme.palette.info.contrastText.asDynamic())
        backgroundColor = Color(theme.palette.info.main.asDynamic())
        padding = 0.5.em.value
        border = "2px inset ${theme.palette.info.main}"
    }

    val code by css {
        whiteSpace = WhiteSpace.preWrap
        userSelect = UserSelect.all

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

            nthChild("even") {
                background = theme.palette.info.main.asColor()
                    .withAlpha(.1)
                    .blend(Color(theme.palette.background.paper))
                    .value
            }
        }
    }

    val copyButton by css {
        color = theme.palette.primary.contrastText.asColor()
        backgroundColor = theme.palette.secondary.main.asColor()
        position = Position.absolute
        top = .5.em
        right = .5.em
    }

    val normal by css {}

    val deprecated by css {
        textDecoration = TextDecoration(setOf(lineThrough))
        declarations["textDecorationThickness"] = "1px"
    }

    val comment by css {
        color = Color(theme.palette.info.contrastText.asDynamic()).withAlpha(.75)
    }
}

external interface ShaderHelpProps : Props {
}

fun RBuilder.shaderHelp(handler: RHandler<ShaderHelpProps>) =
    child(ShaderHelpView, handler = handler)