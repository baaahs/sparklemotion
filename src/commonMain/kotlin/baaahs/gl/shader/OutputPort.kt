package baaahs.gl.shader

import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType

data class OutputPort(
    val contentType: ContentType,
    val description: String? = contentType.title,
    val id: String = ReturnValue,
    val dataType: GlslType = contentType.glslType,
    val isParam: Boolean = false,
    val lineNumber: Int? = null
) {
    val argSiteName: String = if (isReturnValue()) "[return value]" else id

    fun isReturnValue() = id == ReturnValue

    companion object {
        const val ReturnValue = "_"
    }
}