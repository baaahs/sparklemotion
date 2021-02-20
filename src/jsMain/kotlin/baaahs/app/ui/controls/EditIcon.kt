package baaahs.app.ui.controls

import baaahs.Severity
import baaahs.app.ui.appContext
import baaahs.show.live.OpenControl
import baaahs.show.live.OpenPatchHolder
import baaahs.ui.and
import baaahs.ui.unaryPlus
import kotlinx.css.RuleSet
import materialui.icon
import materialui.icons.Icons
import react.RBuilder
import react.dom.div
import react.useContext

fun RBuilder.problemBadge(openControl: OpenControl) {
    if (openControl is OpenPatchHolder) {
        problemBadge(openControl as OpenPatchHolder)
    }
}

fun RBuilder.problemBadge(openPatchHolder: OpenPatchHolder, cssClass: RuleSet? = null) {
    problemBadge(openPatchHolder.problemLevel, cssClass ?: run {
        val appContext = useContext(appContext)
        appContext.allStyles.appUiControls.cardProblemBadge
    })
}

fun RBuilder.problemBadge(problemLevel: Severity?, cssClass: RuleSet? = null) {
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.appUiControls

    problemLevel?.let { severity ->
        val (severityClass, severityIcon) = when (severity) {
            Severity.INFO -> styles.cardProblemInfo to Icons.Info
            Severity.WARN -> styles.cardProblemWarning to Icons.Warning
            Severity.ERROR -> styles.cardProblemError to Icons.Error
        }
        div(+(cssClass ?: styles.cardProblemBadge) and severityClass) {
            icon(severityIcon)
        }
    }
}