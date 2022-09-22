package baaahs.app.ui.editor

import baaahs.app.ui.ShaderPreviewStyles
import baaahs.app.ui.controls.gridArea
import baaahs.ui.asColor
import baaahs.ui.child
import baaahs.ui.important
import kotlinx.css.*
import kotlinx.css.properties.*
import mui.material.styles.Theme
import styled.StyleSheet

class ShaderEditorStyles(private val theme: Theme) : StyleSheet("app-ui-editor-ShaderEditor", isStatic = true) {
    val container by css {
        grow(Grow.GROW)
        display = Display.grid
        gridTemplateAreas = GridTemplateAreas("\"editor preview\" \"editor properties\"")
        gridTemplateColumns = GridTemplateColumns(1.fr, LinearDimension.minContent)
        gridTemplateRows = GridTemplateRows(LinearDimension.minContent, LinearDimension.auto)
    }

    val shaderEditor by css {
        gridArea = "editor"
    }

    val propsAndPreview by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        gap = 1.em

        child(ShaderPreviewStyles, ShaderPreviewStyles::container) {
            grow(Grow.NONE)
        }
    }

    val propsTabsAndPanels by css {
        gridArea = "properties"
        grow(Grow.GROW)
        display = Display.flex
        flexDirection = FlexDirection.column
        position = Position.relative
    }

    val tabsContainer by css {
        backgroundColor = Color(theme.palette.primary.dark.asDynamic())
    }

    val propsPanel by css {
        display = Display.flex
        flexDirection = FlexDirection.row
        padding = "1em"
        maxWidth = 300.px
        height = 100.pct
        position = Position.relative
        overflow = Overflow.scroll

        child("TABLE") {
            position = Position.absolute
            display = Display.block
        }
    }

    val adjustGadgetsSwitch by css {
        position = Position.absolute
        right = 1.em
        top = 0.px
        transform { rotate((-90).deg) }
        declarations["transformOrigin"] = "top right"
    }

    val shaderProperties by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns("50% 50%")
        gridTemplateAreas = GridTemplateAreas("\"name name\" \"priority stream\" \"returnType returnType\"")
        gap = 1.em
        alignContent = Align.start

    }

    val shaderName by css {
        gridArea = "name"
    }

    val shaderPriority by css {
        gridArea = "priority"
    }

    val stream by css {
        gridArea = "stream"
    }

    val shaderReturnType by css {
        gridArea = "returnType"
    }

    val tab by css {
        important(::backgroundColor, theme.palette.background.paper)
        important(::textAlign, TextAlign.right)
    }

    val previewContainer by css {
        gridArea = "preview"
        position = Position.relative
        width = previewWidth
        height = previewHeight
        margin = "auto"
    }

    val settingsMenuAffordance by css {
        position = Position.absolute
        padding(2.px)
        backgroundColor = Color.white.withAlpha(.5)
        width = 2.em
        height = 2.em
        right = .5.em
        bottom = .5.em
    }

    val showAdvancedMenuItem by css {
        paddingTop = 0.px
        paddingBottom = 0.px
        paddingLeft = 8.px

        svg {
            width = .75.em
            height = .75.em
        }

        span {
            fontSize = .95.em
        }
    }

    val editorActionMenuAffordance by css {
        minWidth = 10.em
        border(2.px, BorderStyle.solid, theme.palette.primary.light.asColor())
        borderRadius = 5.px
        position = Position.fixed
        marginTop = 5.px
        transform { scale(.5) }
        padding(2.px)
        display = Display.flex
        minWidth = 0.px
        backgroundColor = Color(theme.palette.background.paper)
        boxShadow = theme.shadows[3]
    }

    val refactorMarker by css {
        display = Display.block
        position = Position.absolute
        border(2.px, BorderStyle.solid, theme.palette.primary.main.asColor())
        marginLeft = (-1).px
        marginRight = (-1).px
        zIndex = 10
    }

    companion object {
        val previewWidth = 250.px
        val previewHeight = 250.px
    }
}