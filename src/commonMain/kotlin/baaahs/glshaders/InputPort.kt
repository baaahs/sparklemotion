package baaahs.glshaders

data class InputPort(
    val type: String,
    val name: String,
    val description: String?,
    val contentType: GlslCode.ContentType,
    val pluginId: String? = contentType.pluginId,
    val pluginConfig: Map<String, String> = emptyMap(),
    val glslVar: GlslCode.GlslVar? = null
)