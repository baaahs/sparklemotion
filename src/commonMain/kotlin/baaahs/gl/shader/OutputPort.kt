package baaahs.gl.shader

import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.show.ShaderOutPortRef

data class OutputPort(
    val dataType: GlslType,
    val id: String,
    val description: String?,
    val contentType: ContentType
) {
    fun isReturnValue() = id == ShaderOutPortRef.ReturnValue
}