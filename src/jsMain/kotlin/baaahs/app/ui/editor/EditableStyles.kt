package baaahs.app.ui.editor

import baaahs.ui.StuffThatShouldComeFromTheTheme
import baaahs.ui.child
import baaahs.ui.important
import kotlinx.css.*
import styled.StyleSheet

object EditableStyles : StyleSheet("app-ui-editor-Editable", isStatic = true) {
    val cardWidth = 175.px

    val drawer by css {
        margin(horizontal = 5.em)
        minHeight = 85.vh
        important(::maxHeight, 85.vh)
    }

    val tabsList by css {
        important(::paddingLeft, 1.em)
    }

    val patchOverview by css {
        position = Position.relative
        backgroundColor = StuffThatShouldComeFromTheTheme.lightBackgroundColor
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns.repeat("auto-fit, minmax(175px, 1fr)")
        gap = 1.em
        padding(1.em)
    }

    val shaderCard by css {
        maxWidth = cardWidth
    }

    val shaderCardContent by css {
    }
    val shaderCardActions by css {
        child(this@EditableStyles, ::shaderCardContent) {
            flex(1.0)
        }

        child("button") {
            flex(0.0)
            padding(0.px)
        }
    }

    val propertiesSection by css {
        paddingTop = .5.em
        paddingBottom = 1.em
    }
}