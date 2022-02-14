package baaahs.mapper

import kotlinx.css.*
import materialui.styles.muitheme.MuiTheme
import styled.StyleSheet

class SceneEditorStyles(val theme: MuiTheme) : StyleSheet("app-ui-scene-editor", isStatic = true) {
    val controllerConfigPaper by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        height = 100.pct
    }

    val controllersTable by css {
        display = Display.block
        overflowY = Overflow.scroll
        height = 100.pct
    }
}