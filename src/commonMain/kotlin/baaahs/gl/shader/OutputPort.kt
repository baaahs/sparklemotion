package baaahs.gl.shader

import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType

data class OutputPort(
    val contentType: ContentType,
    val description: String? = contentType.title,
    val id: String = ReturnValue,
    val dataType: GlslType = contentType.glslType
) {
    fun isReturnValue() = id == ReturnValue

    companion object {
        const val ReturnValue = "_"
    }
}