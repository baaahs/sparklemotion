package baaahs.glshaders

data class OutputPort(
    val dataType: String,
    val name: String,
    val description: String?,
    val contentType: ContentType
)