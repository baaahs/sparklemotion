package baaahs.glshaders

data class OutputPort(
    val type: String,
    val name: String,
    val description: String?
) {
    fun toGlsl(namespace: GlslCode.Namespace): String = "$type ${namespace.qualify(name)};"
}