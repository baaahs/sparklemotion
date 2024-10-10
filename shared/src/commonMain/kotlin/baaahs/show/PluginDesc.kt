package baaahs.show

data class PluginDesc(
    val id: String,
    val title: String,
    val description: String?,
    val version: Int?,
    val url: String?
)