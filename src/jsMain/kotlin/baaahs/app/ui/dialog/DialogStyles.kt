package baaahs.app.ui.dialog

import baaahs.ui.important
import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import styled.StyleSheet

object DialogStyles : StyleSheet("app-ui-dialog-Dialog", isStatic = true) {
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

    val tabsListCol by css {
        flex(1.0, 1.0, FlexBasis.zero)
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
        paddingLeft = 0.px
        paddingTop = 1.em
    }

    val panelCol by css {
        flex(4.0, 1.0, FlexBasis.zero)
        display = Display.flex
        flexDirection = FlexDirection.column
        marginLeft = 2.em
    }
    val panel by css {
        display = Display.flex
        flex(Flex.GROW)
        flexDirection = FlexDirection.column
        alignContent = Align.stretch
        alignItems = Align.stretch
        justifyContent = JustifyContent.stretch
    }
    val columns by css {
        flexDirection = FlexDirection.row
    }
}