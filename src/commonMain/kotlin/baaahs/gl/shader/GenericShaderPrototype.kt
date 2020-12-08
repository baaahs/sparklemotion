package baaahs.gl.shader

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.GlslCode
import baaahs.gl.patch.ContentType
import baaahs.plugin.Plugins
import baaahs.plugin.objectSerializer
import baaahs.show.ShaderType
import baaahs.ui.Icon
import kotlinx.serialization.SerialName

@SerialName("baaahs.Core:Generic")
object GenericShaderPrototype : ShaderPrototype("baaahs.Core:Generic") {
    override val serializerRegistrar = objectSerializer(id, this)

    override val title: String = "Generic"
    override val shaderType: ShaderType get() = ShaderType.Unknown
    override val entryPointName: String = "main"
    override val icon: Icon = CommonIcons.UnknownShader
    override val suggestNew: Boolean = false
    override val template: String get() = error("not implemented")

    override fun matches(glslCode: GlslCode): MatchLevel {
        return glslCode.findFunctionOrNull(entryPointName)
            ?.let { MatchLevel.Poor }
            ?: MatchLevel.NoMatch
    }

    override val outputPort: OutputPort get() =
        error("That doesn't work on generic shaders.")

    override fun findOutputPort(glslCode: GlslCode, plugins: Plugins): OutputPort {
        val entryPoint = findEntryPoint(glslCode)
        return entryPoint.hint?.contentType(plugins = plugins)
            ?.let { OutputPort(it, dataType = entryPoint.returnType) }
            ?: OutputPort(ContentType.Unknown)
    }
}