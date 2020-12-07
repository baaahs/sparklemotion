package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.listOf
import baaahs.plugin.ObjectSerializer
import baaahs.plugin.Plugins
import baaahs.plugin.SerializerRegistrar
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.show.mutable.MutableShader
import baaahs.ui.Icon
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable(with = ObjectSerializer::class)
@Polymorphic
abstract class ShaderPrototype(
    val id: String
) {
    abstract val serializerRegistrar: SerializerRegistrar<out ShaderPrototype>

    abstract val title: String
    open val implicitInputPorts: List<InputPort> = emptyList()
    open val wellKnownInputPorts: List<InputPort> = emptyList()
    open val defaultInputPortsByType: Map<Pair<GlslType, Boolean>, InputPort> = emptyMap()
    open val defaultUpstreams: Map<ContentType, ShaderChannel> = emptyMap()
    abstract val shaderType: ShaderType
    abstract val outputPort: OutputPort
    abstract val entryPointName: String
    abstract val icon: Icon
    open val suggestNew: Boolean = true
    abstract val template: String

    open fun matches(glslCode: GlslCode): MatchLevel {
        return glslCode.findFunctionOrNull(entryPointName)
            ?.let { MatchLevel.Good }
            ?: MatchLevel.NoMatch
    }

    open fun validate(glslCode: GlslCode): List<GlslError> {
        val entryPoint = findEntryPoint(glslCode)
        return if (entryPoint.params.count { it.isOut } > 1)
            GlslError("Multiple out parameters aren't allowed on $entryPointName().", row = entryPoint.lineNumber)
                .listOf()
        else emptyList()
    }

    open fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>,
        entryPoint: GlslCode.GlslFunction
    ): String = entryPoint.invocationGlsl(namespace, resultVar, portMap)

    open fun findTitle(glslCode: GlslCode): String? {
        return Regex("^// (.*)").find(glslCode.src)?.groupValues?.get(1)
    }

    fun findEntryPoint(glslCode: GlslCode): GlslCode.GlslFunction {
        return glslCode.findFunction(entryPointName)
    }

    open fun findOutputPort(glslCode: GlslCode, plugins: Plugins): OutputPort = outputPort

    open fun findMagicInputPorts(glslCode: GlslCode): List<InputPort> = emptyList()

    fun newShaderFromTemplate(): MutableShader {
        return MutableShader("Untitled $title Shader", this, outputPort.contentType, template)
    }
}

enum class MatchLevel {
    NoMatch,
    Poor,
    Good,
    Excellent
}