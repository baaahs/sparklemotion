package baaahs.glshaders

data class InputPort(
    val type: String,
    val name: String,
    val description: String?,
    val contentType: GlslCode.ContentType
)