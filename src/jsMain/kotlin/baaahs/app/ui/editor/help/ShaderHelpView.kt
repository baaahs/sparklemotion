package baaahs.app.ui.editor.help

import baaahs.app.ui.appContext
import baaahs.show.mutable.EditingShader
import baaahs.ui.asColor
import baaahs.ui.components.palette
import baaahs.ui.xComponent
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import kotlinx.css.properties.TextDecorationLine.lineThrough
import mui.material.Button
import mui.material.styles.Theme
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.p
import react.useContext
import styled.StyleSheet

private val ShaderHelpView = xComponent<ShaderHelpProps>("ShaderHelp", isPure = true) { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.shaderHelp
    val editingShader = observe(props.editingShader)
    val openShader = editingShader.openShader

    var showFeedTypes by state { false }

    div {
        if (openShader != null) {
            p {
                +"It looks like your shader is written in the ${openShader.shaderDialect.title} dialect."
            }
        }

        Button {
            attrs.onClick = {
                showFeedTypes = !showFeedTypes
            }
            +"Show Feed Types"
        }

        if (showFeedTypes) {
            palette {
                attrs.title = "Feed Types"
                attrs.onClose = { showFeedTypes = false }
                attrs.autoScroll = true

                feedDescriptions {}
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
        padding = Padding(0.5.em)
        border = Border(2.px, baaahs.ui.inset, theme.palette.info.main.asColor())
    }

    val code by css {
        whiteSpace = WhiteSpace.preWrap
        userSelect = UserSelect.all

        // Line numbers:
        before {
            declarations["counterReset"] = "listing"
        }

        "code" {
            declarations["counterIncrement"] = "listing"

            before {
                declarations["content"] = "counter(listing) \"  \""
                display = Display.inlineBlock
                width = 4.em
                paddingLeft = LinearDimension.auto
                marginLeft = LinearDimension.auto
                textAlign = TextAlign.right
            }

            nthChild("even") {
                background = theme.palette.info.main.asColor()
                    .withAlpha(.8)
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
    var editingShader: EditingShader
}

fun RBuilder.shaderHelp(handler: RHandler<ShaderHelpProps>) =
    child(ShaderHelpView, handler = handler)