package baaahs.gl.shader.dialect

import baaahs.Color
import baaahs.englishize
import baaahs.gl.glsl.*
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.gl.shader.ShaderStatementRewriter
import baaahs.gl.shader.ShaderSubstitutions
import baaahs.listOf
import baaahs.plugin.PluginRef
import baaahs.plugin.Plugins
import baaahs.plugin.core.feed.ColorPickerFeed
import baaahs.plugin.core.feed.XyPadFeed
import baaahs.show.Shader
import baaahs.show.Tag
import baaahs.util.Logger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * A dialect to support the [ISF format](https://docs.isf.video).
 *
 * ISF extends GLSL with the following functions:
 *
 * ```glsl
 * vec4 pixelColor = IMG_PIXEL(image imageName, vec2 pixelCoord);
 * vec4 pixelColor = IMG_NORM_PIXEL(image imageName, vec2 normalizedPixelCoord);
 * vec4 pixelColor = IMG_THIS_PIXEL(image imageName);
 * vec4 pixelColor = IMG_NORM_THIS_PIXEL(image imageName);
 * vec2 imageSize = IMG_SIZE(image imageName);
 * ```
 *
 * Unsupported ISF features:
 * * Multi-pass and persistent buffers
 * * Vertex shaders
 * * IMPORTED data
 */
object IsfShaderDialect : BaseShaderDialect("baaahs.Core:ISF") {
    override val title: String = "ISF"

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

    override fun match(glslCode: GlslCode, plugins: Plugins): ShaderAnalyzer =
        IsfShaderAnalyzer(glslCode, plugins)

    override fun buildStatementRewriter(substitutions: ShaderSubstitutions): ShaderStatementRewriter {
        return IsfStatementRewriter(substitutions)
    }

    override fun genGlslStatements(glslCode: GlslCode): List<GlslCode.GlslStatement> {
        return glslCode.globalVars.filter { !it.isUniform && !it.isVarying } +
                glslCode.functions.filterNot { it.isAbstract }
    }
}

class IsfStatementRewriter(substitutions: ShaderSubstitutions) : ShaderStatementRewriter(substitutions) {
    private var chompWhitespace = false

    override fun visit(token: String): GlslParser.Tokenizer.State {
        return if (!inComment && !inDotTraversal) {
            if (token.isBlank()) {
                if (chompWhitespace) return this
            } else {
                chompWhitespace = false
            }

            when (token) {
                "IMG_PIXEL" -> ImgPixelState()
                "IMG_NORM_PIXEL" -> ImgPixelState(normalizedCoords = true)
                "IMG_THIS_PIXEL" -> ImgPixelState(implicitCoords = true)
                "IMG_THIS_NORM_PIXEL" -> ImgPixelState(implicitCoords = true, normalizedCoords = true)
                else -> superVisit(token)
            }
        } else superVisit(token)
    }

    private fun superVisit(token: String) = super.visit(token)

    inner class ImgPixelState(
        val implicitCoords: Boolean = false,
        // TODO: Coords should always be normalized, right?
        val normalizedCoords: Boolean = false
    ) : GlslParser.Tokenizer.State {
        var expecting = "("

        override fun visit(token: String): GlslParser.Tokenizer.State {
            if (token.isBlank()) return this

            return when (expecting) {
                "(" -> {
                    if (token != expecting) error("Unexpected token \"$token\" (expected $expecting)")
                    expecting = "varName"
                    this
                }
                "varName" -> {
                    superVisit(token) // The image port variable name.
                    if (implicitCoords) {
                        superVisit("(")
                        superVisit("gl_FragCoord")
                        superVisit(".")
                        superVisit("xy")
                        this@IsfStatementRewriter
                    } else {
                        expecting = ","
                        this
                    }
                }
                "," -> {
                    if (token != expecting) error("Unexpected token \"$token\" (expected $expecting)")
                    superVisit("(")
                    chompWhitespace = true
                    this@IsfStatementRewriter
                }
                else -> error("Unexpected token \"$token\" (expected $expecting)")
            }
        }
    }
}

class IsfShaderAnalyzer(
    glslCode: GlslCode,
    plugins: Plugins
) : BaseShaderAnalyzer(glslCode, plugins) {
    override val dialect: ShaderDialect
        get() = IsfShaderDialect

    override val entryPointName: String = "main"

    private val isfShader: IsfShader?
    private val isfShaderError: Exception?
    init {
        var error: Exception? = null
        isfShader = try {
            findIsfShaderDeclaration(glslCode)
        } catch (e: Exception) {
            error = e
            null
        }
        isfShaderError = error
    }

    override val matchLevel: MatchLevel by lazy {
        if (startsWithJsonComment(glslCode) &&
            run {
                val entryPoint = glslCode.findFunctionOrNull("main")
                entryPoint?.returnType == GlslType.Void && entryPoint.params.isEmpty()
            }
        ) MatchLevel.Excellent else MatchLevel.NoMatch
    }

    override val implicitInputPorts = listOf(
        InputPort("gl_FragCoord", ContentType.UvCoordinate, GlslType.Vec4, "Coordinates"),
//        InputPort("isf_FragNormCoord", ContentType.UvCoordinate, GlslType.Vec2, "Coordinates"),
    )

    private val declaredInputPorts = run {
        isfShader ?: return@run emptyList()

        isfShader.INPUTS.mapNotNull { input ->
            val inputPort = createInputPortFor(input)
            inputPort ?: run {
                logger.warn { "Unsupported ISF input type \"${input.TYPE}\" for input \"${input.NAME}." }
                null
            }
        }
    }

    private fun createInputPortFor(input: IsfInput) = when (input) {
        is IsfEventInput -> null
        is IsfBoolInput -> createSwitch(input)
        is IsfLongInput -> createSelect(input)
        is IsfFloatInput -> createSlider(input)
        is IsfPoint2DInput -> createXyPad(input)
        is IsfColorInput -> createColor(input)
        is IsfImageInput -> createImage(input)
        is IsfAudioInput -> null
        is IsfAudioFftInput -> null
    }

    override fun findDeclaredInputPorts(): List<InputPort> =
        declaredInputPorts

    override fun findAuthor(): String? =
        isfShader?.CREDIT

    override fun findTags(): List<Tag> =
        isfShader?.CATEGORIES?.map { Tag.fromString(it) }
            ?: emptyList()

    private fun createSwitch(input: IsfBoolInput): InputPort {
        return InputPort(
            input.NAME, ContentType.Boolean, title = input.LABEL ?: input.NAME.englishize(),
            pluginRef = PluginRef("baaahs.Core", "Switch"),
            pluginConfig = buildJsonObject {
                input.DEFAULT?.let { put("default", JsonPrimitive(it.toBoolean())) }
            }
        )
    }

    private fun createSelect(input: IsfLongInput): InputPort {
        return InputPort(
            input.NAME, ContentType.Int, title = input.LABEL ?: input.NAME.englishize(),
            pluginRef = PluginRef("baaahs.Core", "Select"),
            pluginConfig = buildJsonObject {
                input.LABELS?.let { put("labels", JsonArray(it.map { n -> JsonPrimitive(n) })) }
                input.VALUES?.let { put("values", JsonArray(it.map { l -> JsonPrimitive(l) })) }
                input.DEFAULT?.let { put("default", JsonPrimitive(it.toInt())) }
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
                input.DEFAULT?.let { put("default", JsonPrimitive(it.toFloat())) }
            }
        )
    }

    private fun createXyPad(input: IsfPoint2DInput): InputPort {
        return InputPort(
            input.NAME, ContentType.XyCoordinate, title = input.LABEL ?: input.NAME.englishize(),
            pluginRef = XyPadFeed.pluginRef,
            pluginConfig = buildJsonObject {
                input.MIN?.let { put("min", JsonArray(it.map { n -> JsonPrimitive(n) })) }
                input.MAX?.let { put("max", JsonArray(it.map { n -> JsonPrimitive(n) })) }
                input.DEFAULT?.let { put("default", JsonArray(it.map { n -> JsonPrimitive(n) })) }
            }
        )
    }

    private fun createColor(input: IsfColorInput): InputPort {
        return InputPort(
            input.NAME, ContentType.Color, title = input.LABEL ?: input.NAME.englishize(),
            pluginRef = ColorPickerFeed.pluginRef,
            pluginConfig = buildJsonObject {
                input.DEFAULT?.let {
                    if (it.size == 4) {
                        val (r, g, b, a) = it
                        val defaultColor = Color(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())
                        put("default", JsonPrimitive(defaultColor.toHexString()))
                    }
                }
            }
        )
    }

    /**
     * Per [ISF FX: inputImage](https://docs.isf.video/ref_json.html#isf-attributes):
     *
     * ISF shaders that are to be used as image filters are expected to pass the image
     * to be filtered using the "inputImage" variable name. This input needs to be declared
     * like any other image input, and host developers can assume that any ISF shader
     * specifying an "image"-type input named "inputImage" can be operated as an image
     * filter.
     */
    private fun createImage(input: IsfImageInput): InputPort {
        val invocationFnName = input.NAME
        return InputPort(
                input.NAME, ContentType.Color, title = input.LABEL ?: invocationFnName.englishize(),
                isImplicit = true, injectedData = mapOf("uv" to ContentType.UvCoordinate),
                glslArgSite = GlslCode.GlslFunction(
                    invocationFnName, GlslType.Vec4,
                    listOf(GlslCode.GlslParam("uv", GlslType.Vec2, true)),
                    "vec4 $invocationFnName(vec2 uv);",
                    isAbstract = true, isGlobalInput = true
                )
            )
    }

    private fun findIsfShaderDeclaration(glslCode: GlslCode): IsfShader? {
        if (!startsWithJsonComment(glslCode)) return null
        val startOfJson = glslCode.src.indexOf("{")
        val endOfJson = glslCode.src.indexOf("*/")
        val jsonDecl = glslCode.src.substring(startOfJson, endOfJson)
        try {
            return json.decodeFromString(IsfShader.serializer(), jsonDecl)
        } catch (e: SerializationException) {
            throw AnalysisException(e.message ?: "Invalid JSON", 1)
        }
    }

    override fun additionalOutputPorts(): List<OutputPort> {
        return if (glslCode.refersToGlobal("gl_FragColor")) {
            OutputPort(ContentType.Color, id = "gl_FragColor", dataType = GlslType.Vec4, description = "Output Color")
                .listOf()
        } else emptyList()
    }

    override fun findAdditionalErrors(): List<GlslError> = buildList {
        val passes = isfShader?.PASSES ?: emptyList()
        val normalPasses = passes.isEmpty() || passes == listOf(IsfPass())
        if (!normalPasses) {
            add(GlslError("Multiple passes aren't supported."))
        }
    }

    override fun analyze(existingShader: Shader?): ShaderAnalysis =
        isfShaderError?.let {
            ErrorsShaderAnalysis(glslCode.src, WrappedException(it), existingShader ?: createShader())
        } ?: super.analyze(existingShader)

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

    companion object {
        private val logger = Logger<IsfShaderAnalyzer>()

        private val json = Json {
            classDiscriminator = "TYPE"
            ignoreUnknownKeys = true
            isLenient = true
        }

        private fun startsWithJsonComment(glslCode: GlslCode) =
            Regex("^\\s*/[*]\\s*[{]", RegexOption.MULTILINE)
                .matchesAt(glslCode.src, 0)
    }
}

/**
 * See [ISF JSON Attributes](https://docs.isf.video/ref_json.html).
 */
@Serializable
private data class IsfShader(
    val ISFVSN: String? = null,
    val VSN: String? = null,
    val DESCRIPTION: String? = null,
    val CATEGORIES: List<String> = emptyList(),
    val INPUTS: List<IsfInput> = emptyList(),
    val PASSES: List<IsfPass> = emptyList(),
    val IMPORTED: Map<String, IsfImported> = emptyMap(),
    val CREDIT: String? = null
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
    val LABELS: List<String>? = null,
    val VALUES: List<Int>? = null
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
    val DEFAULT: Array<Float>? = null,
    val MIN: Array<Float>? = null,
    val MAX: Array<Float>? = null
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

@Serializable
private class IsfPass(
    val TARGET: String? = null,
    val PERSISTENT: Boolean = false,
    val FLOAT: Boolean = false,
    val HEIGHT: String? = null,
    val WIDTH: String? = null
)

@Serializable
private class IsfImported(
    val PATH: String
)