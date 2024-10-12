package baaahs.sm.webapi

import baaahs.randomId
import baaahs.ui.Markdown
import kotlinx.serialization.Serializable

@Serializable
data class ServerNotice(
    val title: String,
    @Markdown
    val message: String?,
    val stackTrace: String?,
    val id: String = randomId("error")
)