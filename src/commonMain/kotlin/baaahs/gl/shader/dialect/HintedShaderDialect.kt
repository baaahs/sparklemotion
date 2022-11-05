package baaahs.gl.shader.dialect

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.plugin.Plugins

abstract class HintedShaderAnalyzer(
    glslCode: GlslCode,
    plugins: Plugins
) : BaseShaderAnalyzer(glslCode, plugins) {
    override fun findDeclaredInputPorts(): List<InputPort> {
        val entryPoint = findEntryPointOrNull()
        val entryPointParams =
            entryPoint?.params?.filter { it.isIn } ?: emptyList()

        return (glslCode.globalInputVars + entryPointParams).map {
            it.resolveInputPort(entryPoint, plugins)
        } + glslCode.functions.filter { it.isAbstract }.map {
            toInputPort(it, plugins)
        }
    }

    override fun toInputPort(it: GlslCode.GlslFunction, plugins: Plugins) =
        it.toInputPort(plugins, null)

    override fun findEntryPointOutputPort(
        entryPoint: GlslCode.GlslFunction?,
        plugins: Plugins
    ) = if (entryPoint == null || entryPoint.returnType == GlslType.Void) null else {
        val contentType = entryPoint.hint?.contentType("return", plugins)
            ?: ContentType.unknown(entryPoint.returnType)
        OutputPort(contentType, dataType = entryPoint.returnType)
    }
}

fun GlslCode.GlslFunction.findContentType(param: GlslCode.GlslParam, plugins: Plugins): ContentType? =
    param.hint?.contentType(plugins)
        ?: findParamHint(param.name, plugins)

fun GlslCode.GlslFunction.findParamHint(paramName: String, plugins: Plugins): ContentType? {
    return hint?.tags("param")
        ?.map { it.split(Regex("\\s+"), limit = 2) }
        ?.filter { it.size == 2 && it.first() == paramName }
        ?.map { plugins.resolveContentType(it.last()) }
        ?.firstOrNull()
}

fun GlslCode.GlslFunction.getParamOutputPorts(plugins: Plugins) =
    params.filter { it.isOut && it.type != GlslType.Void }
        .map { param ->
            val contentType = findContentType(param, plugins) ?: ContentType.unknown(param.type)
            OutputPort(contentType, dataType = param.type, id = param.name, isParam = true)
        }

fun GlslCode.GlslArgSite.toInputPort(plugins: Plugins, parent: GlslCode.GlslFunction?): InputPort {
    val contentTypeFromPlugin = try {
        hint?.pluginRef
            ?.let { plugins.findDataSourceBuilder(it).contentType }
    } catch (e: Exception) {
        null
    }

    return InputPort(
        name,
        contentType = contentTypeFromPlugin
            ?: findContentType(plugins, parent)
            ?: plugins.resolveContentType(type),
        type = type,
        title = title,
        pluginRef = hint?.pluginRef,
        pluginConfig = hint?.config,
        glslArgSite = this,
        injectedData = findInjectedData(plugins)
    )
}

fun GlslCode.GlslArgSite.findContentType(plugins: Plugins, parent: GlslCode.GlslFunction?): ContentType? {
    return when (this) {
        is GlslCode.GlslFunction ->
            hint?.contentType("return", plugins)
                ?: hint?.contentType("type", plugins)

        is GlslCode.GlslParam ->
            hint?.contentType("type", plugins)
                ?: parent?.findContentType(this, plugins)

        else ->
            hint?.contentType("type", plugins)
    }
}
