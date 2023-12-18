package baaahs.sm.webapi

import baaahs.app.ui.CommonIcons
import baaahs.io.Fs
import baaahs.randomId
import baaahs.ui.Icon
import kotlinx.serialization.Serializable

@Serializable
data class ClientData(
    val fsRoot: Fs.File
)

@Serializable
data class Problem(
    val title: String,
    val message: String? = null,
    val severity: Severity = Severity.ERROR,
    val id: String = randomId("error")
)

fun Collection<Problem>.severity() = maxOfOrNull { it.severity }

enum class Severity(val icon: Icon) {
    INFO(CommonIcons.Info),
    WARN(CommonIcons.Warning),
    ERROR(CommonIcons.Error)
}