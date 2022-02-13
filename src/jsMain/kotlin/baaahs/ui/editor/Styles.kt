package baaahs.ui.editor

import kotlinx.css.*
import kotlinx.css.properties.border
import kotlinx.css.properties.scale
import kotlinx.css.properties.transform
import mui.material.styles.Theme
import styled.StyleSheet

class Styles(theme: Theme) : StyleSheet("ui-editor", isStatic = true) {
    val editorActionMenuAffordance by css {
        minWidth = 10.em
        border(2.px, BorderStyle.solid, Color(theme.palette.primary.light))
        borderRadius = 5.px
        position = Position.fixed
        marginTop = 5.px
        transform { scale(.5) }
        padding(2.px)
        display = Display.flex
        minWidth = 0.px
        backgroundColor = theme.palette.background.paper
        boxShadow = theme.shadows[3]
    }

    val refactorMarker by css {
        display = Display.block
        position = Position.absolute
        border(2.px, BorderStyle.solid, Color(theme.palette.primary.main))
        marginLeft = (-1).px
        marginRight = (-1).px
        zIndex = 10
    }
}