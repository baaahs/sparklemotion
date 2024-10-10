package baaahs.app.ui.document

import baaahs.ui.asColor
import baaahs.ui.paperHighContrast
import baaahs.ui.paperLowContrast
import baaahs.ui.transition
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import kotlinx.css.properties.TextDecorationLine
import kotlinx.css.properties.s
import mui.material.styles.Theme
import styled.StyleSheet

class FileUploadStyles(private val theme: Theme) : StyleSheet("app-ui-document-fileupload", isStatic = true) {
    val container by css {
        padding = Padding(1.em)
        minWidth = 35.pct
        minHeight = 35.pct
    }

    val upload by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        alignItems = Align.center
        padding = Padding(1.em)

        color = theme.palette.text.disabled.asColor()
        transition(::color, duration = .5.s)

        borderColor = theme.paperLowContrast
        transition(::borderColor, duration = 1.s)
        backgroundColor = Color(theme.palette.background.paper)
        transition(::backgroundColor, duration = .5.s)
        borderWidth = 6.px
        borderRadius = 6.px
        borderStyle = BorderStyle.dashed
    }

    val dragAccept by css {
        color = theme.palette.text.primary.asColor()
        borderColor = theme.paperHighContrast
        backgroundColor = theme.palette.text.primary.asColor()
            .withAlpha(.125)
            .blend(Color(theme.palette.background.paper))
    }

    val dragActive by css {
        borderColor = theme.paperHighContrast
        backgroundColor = theme.palette.text.primary.asColor()
            .withAlpha(.25)
            .blend(Color(theme.palette.background.paper))
    }

    val dragReject by css {
        color = theme.palette.error.contrastText.asColor()
        backgroundColor = theme.palette.error.main.asColor()
        borderColor = theme.palette.error.dark.asColor()
    }

    val fileDialogActive by css {
    }

    val focused by css {
    }

    val linkink by css {
        color = theme.palette.primary.main.asColor()
        textDecoration = TextDecoration(setOf(TextDecorationLine.underline))
    }
}