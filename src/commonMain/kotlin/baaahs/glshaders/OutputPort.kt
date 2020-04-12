package baaahs.glshaders

data class OutputPort(
    val type: String,
    val name: String,
    val description: String?
) {
    fun toGlsl(namespace: String): String = "$type ${namespace}_$name;"
}