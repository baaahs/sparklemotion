package baaahs.gl.shader.dialect

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.listOf
import baaahs.plugin.Plugins

object GenericShaderDialect : ShaderDialect {
    override val id: String
        get() = "baaahs.Core:Generic"
    override val title: String
        get() = "Generic"

    override fun match(glslCode: GlslCode, plugins: Plugins): ShaderAnalyzer =
        GenericShaderAnalyzer(glslCode, plugins)
}

class GenericShaderAnalyzer(
    glslCode: GlslCode,
    plugins: Plugins
) : HintedShaderAnalyzer(glslCode, plugins) {
    override val dialect: ShaderDialect
        get() = GenericShaderDialect

    override val entryPointName: String = "main"

    override val matchLevel: MatchLevel =
        glslCode.findFunctionOrNull(entryPointName)
            ?.let { MatchLevel.Poor }
            ?: MatchLevel.NoMatch

    override val implicitInputPorts = listOf(
        InputPort("gl_FragCoord", ContentType.UvCoordinate, GlslType.Vec4, "Coordinates")
    )

    override val wellKnownInputPorts = listOf(
        InputPort("resolution", ContentType.Resolution, GlslType.Vec2, "Resolution"),
        InputPort("mouse", ContentType.Mouse, GlslType.Vec2, "Mouse"),
        InputPort("time", ContentType.Time, GlslType.Float, "Time")
    )

    override fun additionalOutputPorts(glslCode: GlslCode, plugins: Plugins): List<OutputPort> {
        return if (glslCode.refersToGlobal("gl_FragColor")) {
            OutputPort(ContentType.Color, id = "gl_FragColor", dataType = GlslType.Vec4, description = "Output Color")
                .listOf()
        } else emptyList()
    }

    private val defaultContentTypes = mapOf<GlslType, ContentType>(
        GlslType.Vec4 to ContentType.Color
    )

    override fun adjustOutputPorts(outputPorts: List<OutputPort>): List<OutputPort> {
        return outputPorts.map {
            if (it.contentType == ContentType.Unknown) {
                it.copy(contentType = defaultContentTypes[it.dataType] ?: ContentType.Unknown)
            } else it
        }
    }
}