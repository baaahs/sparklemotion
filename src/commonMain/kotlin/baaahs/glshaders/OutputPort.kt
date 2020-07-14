package baaahs.glshaders

import baaahs.show.Shader

data class OutputPort(
    val id: String,
    val dataType: String,
    val description: String?,
    val contentType: ContentType,
    val isImplicit: Boolean = false,
    val synthetic: String? = null
) {
    private val isReturnValue: Boolean
        get() = id == Shader.ReturnValue

    fun varName(namespace: GlslCode.Namespace) =
        if (isReturnValue) namespace.internalQualify("result") else namespace.qualify(id)

    fun toGlsl(namespace: GlslCode.Namespace): String {
        return "$dataType ${varName(namespace)}"
    }
}