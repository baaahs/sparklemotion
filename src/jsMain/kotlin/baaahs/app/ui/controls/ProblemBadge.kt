package baaahs.app.ui.controls

import baaahs.show.live.OpenControl
import baaahs.show.live.OpenPatchHolder
import baaahs.sm.webapi.Severity
import baaahs.ui.and
import baaahs.ui.unaryPlus
import kotlinx.css.RuleSet
import materialui.icon
import react.RBuilder
import react.dom.div

fun RBuilder.problemBadge(openControl: OpenControl) {
    if (openControl is OpenPatchHolder) {
        problemBadge(openControl as OpenPatchHolder)
    }
}

fun RBuilder.problemBadge(openPatchHolder: OpenPatchHolder, cssClass: RuleSet = Styles.cardProblemBadge) {
    problemBadge(openPatchHolder.problemLevel, cssClass)
}

fun RBuilder.problemBadge(problemLevel: Severity?, cssClass: RuleSet = Styles.cardProblemBadge) {
    problemLevel?.let { severity ->
        val (severityClass, severityIcon) = when (severity) {
            Severity.INFO -> Styles.cardProblemInfo to mui.icons.material.Info
            Severity.WARN -> Styles.cardProblemWarning to mui.icons.material.Warning
            Severity.ERROR -> Styles.cardProblemError to mui.icons.material.Error
        }
        div(+cssClass and severityClass) {
            icon(severityIcon)
        }
    }
}