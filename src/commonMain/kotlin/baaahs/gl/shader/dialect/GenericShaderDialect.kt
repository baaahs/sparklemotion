package baaahs.gl.shader.dialect

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.listOf
import baaahs.plugin.Plugins

object GenericShaderDialect : HintedShaderDialect("baaahs.Core:Generic") {
    override val title: String = "Generic"
    override val entryPointName: String = "main"

    override val implicitInputPorts = listOf(
        InputPort("gl_FragCoord", ContentType.UvCoordinate, GlslType.Vec4, "Coordinates")
    )

    override val wellKnownInputPorts = listOf(
        InputPort("resolution", ContentType.Resolution, GlslType.Vec2, "Resolution"),
        InputPort("mouse", ContentType.Mouse, GlslType.Vec2, "Mouse"),
        InputPort("time", ContentType.Time, GlslType.Float, "Time")
    )

    override fun match(glslCode: GlslCode, plugins: Plugins): ShaderAnalyzer {
        return BaseShaderAnalyzer(
            glslCode.findFunctionOrNull(entryPointName)
                ?.let { MatchLevel.Poor }
                ?: MatchLevel.NoMatch
        )
    }

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