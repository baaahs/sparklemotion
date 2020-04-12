package baaahs.glshaders

data class InputPort(
    val type: String,
    val name: String,
    val description: String?,
    val contentType: GlslCode.ContentType,
    val glslVar: GlslCode.GlslVar? = null
) {
    fun toGlsl(namespace: String): Any {
        return glslVar?.toGlsl(namespace) ?: "uniform $type ${namespace}_$name;"
    }
}