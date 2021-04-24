package baaahs.gl.shader.dialect

import baaahs.englishize
import baaahs.gl.glsl.AnalysisException
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.listOf
import baaahs.plugin.PluginRef
import baaahs.plugin.Plugins
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

object IsfShaderDialect : BaseShaderDialect("baaahs.Core:ISF") {
    private val json = Json {
        classDiscriminator = "TYPE"
        ignoreUnknownKeys = true
        isLenient = true
    }

    override val title: String = "ISF"
    override val entryPointName: String = "main"

    override val implicitInputPorts = listOf(
        InputPort("gl_FragCoord", ContentType.UvCoordinate, GlslType.Vec4, "Coordinates"),
//        InputPort("isf_FragNormCoord", ContentType.UvCoordinate, GlslType.Vec2, "Coordinates"),
    )

    // See https://docs.isf.video/ref_variables.html#automatically-declared-variables
    override val wellKnownInputPorts = listOf(
        InputPort("PASSINDEX", ContentType.PassIndex, title = ContentType.PassIndex.title),
        InputPort("RENDERSIZE", ContentType.Resolution, title = ContentType.Resolution.title),
        InputPort("isf_FragNormCoord", ContentType.UvCoordinate, GlslType.Vec2, title = ContentType.UvCoordinate.title, isImplicit = true),
        InputPort("TIME", ContentType.Time, title = ContentType.Time.title),
        InputPort("TIMEDELTA", ContentType.TimeDelta, title = ContentType.TimeDelta.title),
        InputPort("DATE", ContentType.Date, title = ContentType.Date.title),
        InputPort("FRAMEINDEX", ContentType.FrameIndex, title = ContentType.FrameIndex.title),
    )

    override fun matches(glslCode: GlslCode): MatchLevel {
        return if (glslCode.src.startsWith("/*{") &&
            run {
                val entryPoint = glslCode.findFunctionOrNull("main")
                entryPoint?.returnType == GlslType.Void && entryPoint.params.isEmpty()
            }
        ) MatchLevel.Excellent else MatchLevel.NoMatch
    }

    override fun findDeclaredInputPorts(
        glslCode: GlslCode,
        plugins: Plugins
    ): List<InputPort> {
        val isfShader = findIsfShaderDeclaration(glslCode)
            ?: return emptyList()

        return isfShader.INPUTS.map { input ->
            when (input) {
                is IsfEventInput -> null
                is IsfBoolInput -> createSwitch(input)
                is IsfLongInput -> null
                is IsfFloatInput -> createSlider(input)
                is IsfPoint2DInput -> null
                is IsfColorInput -> createColor(input)
                is IsfImageInput -> createImage(input)
                is IsfAudioInput -> null
                is IsfAudioFftInput -> null
                else -> throw AnalysisException("unknown ISF input type \"${input.TYPE}\"")
            } ?: throw AnalysisException("unsupported ISF input type \"${input.TYPE}\"")
        }
    }

    private fun createSwitch(input: IsfBoolInput): InputPort {
        return InputPort(
            input.NAME, ContentType.Boolean, title = input.LABEL ?: input.NAME.englishize(),
            pluginRef = PluginRef("baaahs.Core", "Switch"),
            pluginConfig = buildJsonObject {
                input.DEFAULT?.let<String, Unit> { put("default", JsonPrimitive(it.toFloat() != 0f)) }
            }
        )
    }

    private fun createSlider(input: IsfFloatInput): InputPort {
        return InputPort(
            input.NAME, ContentType.Float, title = input.LABEL ?: input.NAME.englishize(),
            pluginRef = PluginRef("baaahs.Core", "Slider"),
            pluginConfig = buildJsonObject {
                input.MIN?.let { put("min", JsonPrimitive(it.toFloat())) }
                input.MAX?.let { put("max", JsonPrimitive(it.toFloat())) }
                input.DEFAULT?.let<String, Unit> { put("default", JsonPrimitive(it.toFloat())) }
            }
        )
    }

    private fun createColor(input: IsfColorInput): InputPort {
        return InputPort(
            input.NAME, ContentType.Color, title = input.LABEL ?: input.NAME.englishize(),
            pluginRef = PluginRef("baaahs.Core", "ColorPicker"),
            pluginConfig = buildJsonObject {
            }
        )
    }

    private fun createImage(input: IsfImageInput): InputPort {
        // TODO: image, not color?
        return InputPort(
            input.NAME, ContentType.Color, title = input.LABEL ?: input.NAME.englishize(),
            pluginRef = PluginRef("baaahs.Core", "ColorPicker"),
            pluginConfig = buildJsonObject {
            }
        )
    }

    private fun findIsfShaderDeclaration(glslCode: GlslCode): IsfShader? {
        val match = Regex("^/\\*(\\{[\\s\\S]*})\\*/").find(glslCode.src)
            ?: return null

        val (jsonDecl) = match.destructured
        try {
            return json.decodeFromString(IsfShader.serializer(), jsonDecl)
        } catch (e: SerializationException) {
            throw AnalysisException(e.message ?: "Invalid JSON", 1)
        }
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

    override fun adjustInputPorts(inputPorts: List<InputPort>): List<InputPort> {
        return super.adjustInputPorts(inputPorts).map { it.copy(isImplicit = true) }
    }

    override fun adjustOutputPorts(outputPorts: List<OutputPort>): List<OutputPort> {
        return outputPorts.map {
            if (it.contentType == ContentType.Unknown) {
                it.copy(contentType = defaultContentTypes[it.dataType] ?: ContentType.Unknown)
            } else it
        }
    }

    override fun findEntryPointOutputPort(entryPoint: GlslCode.GlslFunction?, plugins: Plugins): OutputPort? = null

    override fun toInputPort(it: GlslCode.GlslFunction, plugins: Plugins): InputPort {
        TODO("not implemented")
    }
}

@Serializable
private data class IsfShader(
    val DESCRIPTION: String? = null,
    val CREDIT: String? = null,
    val ISFVSN: String? = null,
    val VSN: String? = null,
    val CATEGORIES: List<String> = emptyList(),
    val INPUTS: List<IsfInput> = emptyList()
)

@Serializable
@Suppress("PropertyName")
private sealed class IsfInput {
    abstract val NAME: String
    abstract val TYPE: String
    abstract val LABEL: String?
}

@Serializable
@SerialName("event")
private class IsfEventInput(
    override val NAME: String,
    override val TYPE: String,
    override val LABEL: String? = null,
    val DEFAULT: String? = null,
    val MIN: String? = null,
    val MAX: String? = null
) : IsfInput()

@Serializable
@SerialName("bool")
private class IsfBoolInput(
    override val NAME: String,
    override val TYPE: String,
    override val LABEL: String? = null,
    val DEFAULT: String? = null,
    val MIN: String? = null,
    val MAX: String? = null
) : IsfInput()

@Serializable
@SerialName("long")
private class IsfLongInput(
    override val NAME: String,
    override val TYPE: String,
    override val LABEL: String? = null,
    val DEFAULT: String? = null,
    val MIN: String? = null,
    val MAX: String? = null
) : IsfInput()

@Serializable
@SerialName("float")
private class IsfFloatInput(
    override val NAME: String,
    override val TYPE: String,
    override val LABEL: String? = null,
    val DEFAULT: String? = null,
    val MIN: String? = null,
    val MAX: String? = null
) : IsfInput()

@Serializable
@SerialName("point2D")
private class IsfPoint2DInput(
    override val NAME: String,
    override val TYPE: String,
    override val LABEL: String? = null,
) : IsfInput()

@Serializable
@SerialName("color")
private class IsfColorInput(
    override val NAME: String,
    override val TYPE: String,
    override val LABEL: String? = null,
    val DEFAULT: Array<String>? = null,
    ) : IsfInput()

@Serializable
@SerialName("image")
private class IsfImageInput(
    override val NAME: String,
    override val TYPE: String,
    override val LABEL: String? = null,
) : IsfInput()

@Serializable
@SerialName("audio")
private class IsfAudioInput(
    override val NAME: String,
    override val TYPE: String,
    override val LABEL: String? = null,
) : IsfInput()

@Serializable
@SerialName("audioFFT")
private class IsfAudioFftInput(
    override val NAME: String,
    override val TYPE: String,
    override val LABEL: String? = null,
) : IsfInput()
