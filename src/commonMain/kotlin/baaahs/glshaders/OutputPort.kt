package baaahs.glshaders

data class OutputPort(
    val type: String,
    val name: String,
    val description: String?,
    val contentType: GlslCode.ContentType
) {
    fun toGlsl(namespace: GlslCode.Namespace): String = "$type ${namespace.qualify(name)};"
}