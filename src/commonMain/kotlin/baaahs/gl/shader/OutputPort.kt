package baaahs.gl.shader

import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.show.ShaderOutPortRef

data class OutputPort(
    val contentType: ContentType,
    val description: String? = contentType.title,
    val id: String = ShaderOutPortRef.ReturnValue,
    val dataType: GlslType = contentType.glslType
) {
    fun isReturnValue() = id == ShaderOutPortRef.ReturnValue
}