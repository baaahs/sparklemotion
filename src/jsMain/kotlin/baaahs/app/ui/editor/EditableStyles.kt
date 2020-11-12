package baaahs.app.ui.editor

import baaahs.ui.StuffThatShouldComeFromTheTheme
import baaahs.ui.child
import baaahs.ui.important
import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import styled.StyleSheet

object EditableStyles : StyleSheet("app-ui-Editable", isStatic = true) {
    val cardWidth = 175.px

    val drawer by css {
        margin(horizontal = 5.em)
        minHeight = 85.vh
        important(::maxHeight, 85.vh)
    }

    val dialogTitle by css {
        position = Position.relative
    }

    val dialogTitleButtons by css {
        display = Display.flex
        position = Position.absolute
        top = 1.em
        right = 1.em
    }

    val dialogContent by css {
        display = Display.flex
        alignItems = Align.stretch
        child("*") {
            flex(1.0)
        }
    }

    val panel by css {
        display = Display.flex
        grow(Grow.GROW)
        flexDirection = FlexDirection.column
        alignContent = Align.stretch
        alignItems = Align.stretch
        justifyContent = JustifyContent.stretch
    }

    val columns by css {
        flexDirection = FlexDirection.row
    }

    val tabsListCol by css {
        flex(1.0, flexBasis = FlexBasis.zero)
    }
    val tabsList by css {
        important(::paddingLeft, 1.em)
    }
    val tabsListItemIcon by css {
        important(::minWidth, 1.em)
        paddingRight = 1.em
    }
    val tabsSubheader by css {
        important(::lineHeight, LineHeight.normal)
    }

    val editorCol by css {
        flex(4.0, flexBasis = FlexBasis.zero)
        display = Display.flex
        flexDirection = FlexDirection.column
        marginLeft = 2.em
    }

    val patchOverview by css {
        backgroundColor = StuffThatShouldComeFromTheTheme.lightBackgroundColor
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns.repeat("auto-fit, minmax(175px, 1fr)")
        gap = Gap(1.em.toString())
        padding(1.em)
    }

    val shaderCard by css {
        maxWidth = cardWidth
    }

    val shaderCardContent by css {
    }
    val shaderCardActions by css {
        child(shaderCardContent) {
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