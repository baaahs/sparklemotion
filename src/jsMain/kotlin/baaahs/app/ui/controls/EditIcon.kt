package baaahs.app.ui.controls

import baaahs.Severity
import baaahs.show.live.OpenControl
import baaahs.show.live.OpenPatchHolder
import baaahs.ui.and
import baaahs.ui.unaryPlus
import kotlinx.css.RuleSet
import materialui.icon
import materialui.icons.Icons
import react.RBuilder
import react.dom.div

fun RBuilder.problemBadge(openControl: OpenControl) {
    if (openControl is OpenPatchHolder) {
        problemBadge(openControl as OpenPatchHolder)
    }
}

fun RBuilder.problemBadge(openPatchHolder: OpenPatchHolder, cssClass: RuleSet = Styles.cardProblemBadge) {
    openPatchHolder.problemLevel?.let { severity ->
        val (severityClass, severityIcon) = when (severity) {
            Severity.INFO -> Styles.cardProblemInfo to Icons.Info
            Severity.WARN -> Styles.cardProblemWarning to Icons.Warning
            Severity.ERROR -> Styles.cardProblemError to Icons.Error
        }
        div(+cssClass and severityClass) {
            icon(severityIcon)
        }
    }
}