package baaahs.glshaders

import baaahs.show.ShaderOutPortRef

data class OutputPort(
    val dataType: String,
    val id: String,
    val description: String?,
    val contentType: ContentType
) {
    fun isReturnValue() = id == ShaderOutPortRef.ReturnValue
}