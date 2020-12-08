package baaahs.app.ui.controls

import baaahs.Severity
import baaahs.show.live.OpenControl
import baaahs.show.live.OpenPatchHolder
import baaahs.ui.on
import kotlinext.js.jsObject
import materialui.components.badge.badge
import materialui.components.badge.enums.BadgeColor
import materialui.components.badge.enums.BadgeStyle
import materialui.components.badge.enums.BadgeVariant
import materialui.icon
import materialui.icons.Icons
import react.RBuilder

fun RBuilder.editIconWithBadge(
    openControl: OpenControl,
    editMode: Boolean
) {
    if (openControl is OpenPatchHolder) {
        editIconWithBadge(openControl as OpenPatchHolder, editMode)
    } else {
        icon(Icons.Edit)
    }
}

fun RBuilder.editIconWithBadge(
    openPatchHolder: OpenPatchHolder,
    editMode: Boolean
) {
    if (editMode && openPatchHolder.problems.isNotEmpty()) {
        val isError = openPatchHolder.problems.any { it.severity >= Severity.ERROR }
        val badgeStyle = if (isError) Styles.editButtonErrorBadge else Styles.editButtonWarningBadge
        badge(badgeStyle on BadgeStyle.colorError) {
            attrs.color = BadgeColor.error
            attrs.variant = BadgeVariant.dot
            attrs["anchorOrigin"] = jsObject<AnchorOrigin> {
                horizontal = "right"
                vertical = "bottom"
            }
            icon(Icons.Edit)
        }
    } else {
        icon(Icons.Edit)
    }
}

external interface AnchorOrigin {
    var horizontal: String
    var vertical: String
}