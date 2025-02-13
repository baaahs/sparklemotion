package baaahs.gl.shader

import baaahs.englishize
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.PluginRef
import kotlinx.serialization.json.JsonObject
import baaahs.decapitalize

data class InputPort(
    val id: String,
    val contentType: ContentType,
    val type: GlslType = contentType.glslType,
    val title: String = id.englishize(),
    val pluginRef: PluginRef? = null,
    val pluginConfig: JsonObject? = null,
    val glslArgSite: GlslCode.GlslArgSite? = null,
    val varName: String = id,

    /** `true` if there's no declaration for this input, e.g. gl_FragCoord. */
    val isImplicit: Boolean = false,
    val injectedData: Map<String, ContentType> = emptyMap()
) {
    val isGlobal: Boolean get() = glslArgSite?.isGlobalInput ?: isImplicit
    val isAbstractFunction: Boolean get() = glslArgSite?.isAbstractFunction ?: false

    fun hasPluginRef() = pluginRef != null

    fun suggestVarName(): String {
        val postfix = pluginRef?.resourceName ?: type.glslLiteral
        return id.replaceFirstChar { it.lowercase() } + postfix.capitalize()
    }

    fun dataTypeIs(glslType: GlslType): Boolean {
        return type.matches(glslType)
    }
}