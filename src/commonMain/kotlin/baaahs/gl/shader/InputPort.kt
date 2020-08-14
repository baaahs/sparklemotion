package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.PluginRef
import kotlinx.serialization.json.JsonObject

data class InputPort(
    val id: String,
    val dataType: String,
    val title: String,
    val contentType: ContentType? = null,
    val pluginRef: PluginRef? = null,
    val pluginConfig: JsonObject? = null,
    val glslVar: GlslCode.GlslVar? = null,
    val varName: String = id,
    val isImplicit: Boolean = false
) {
    fun hasPluginRef() = pluginRef != null

    fun suggestVarName(): String {
        val postfix = pluginRef?.resourceName ?: dataType
        return id.decapitalize() + postfix.capitalize()
    }

    fun dataTypeIs(glslType: GlslType, isStreaming: Boolean = false): Boolean {
        return dataType == glslType.glslLiteral
                && (glslVar?.isVarying ?: false) == isStreaming
    }
}