package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.PluginRef
import kotlinx.serialization.json.JsonObject

data class InputPort(
    val id: String,
    val type: GlslType,
    val title: String,
    val contentType: ContentType? = null,
    val pluginRef: PluginRef? = null,
    val pluginConfig: JsonObject? = null,
    val glslArgSite: GlslCode.GlslArgSite? = null,
    val varName: String = id,

    /** There's no declaration for this input, e.g. gl_FragCoord. */
    val isImplicit: Boolean = false
) {
    val isGlobal: Boolean get() = glslArgSite?.isGlobalInput ?: isImplicit

    fun hasPluginRef() = pluginRef != null

    fun suggestVarName(): String {
        val postfix = pluginRef?.resourceName ?: type.glslLiteral
        return id.decapitalize() + postfix.capitalize()
    }

    fun dataTypeIs(glslType: GlslType, isStreaming: Boolean = false): Boolean {
        return type == glslType
                && (glslArgSite?.isVarying ?: false) == isStreaming
    }
}