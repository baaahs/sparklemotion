package baaahs.app.ui.editor.layout

import baaahs.app.ui.Styles
import baaahs.ui.typographyH6
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import materialui.icon
import mui.icons.material.NotificationImportant
import mui.material.Box
import mui.material.Paper
import react.Props
import react.RBuilder
import react.RHandler

private val LegacyLayoutWarningView = xComponent<LegacyLayoutEditorProps>("LegacyLayoutEditor") { props ->
    Paper {
        attrs.className = -Styles.warningPaper

        Box {
            icon(NotificationImportant)
            typographyH6 { +"Old-style layouts are no longer supported." }
            +"Sorry!"
        }
    }
}

external interface LegacyLayoutEditorProps : Props {
}

fun RBuilder.legacyLayoutWarning(handler: RHandler<LegacyLayoutEditorProps>) =
    child(LegacyLayoutWarningView, handler = handler)