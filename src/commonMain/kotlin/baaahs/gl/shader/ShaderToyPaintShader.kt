package baaahs.gl.shader

import baaahs.app.ui.CommonIcons
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.listOf
import baaahs.plugin.objectSerializer
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.ui.Icon
import kotlinx.serialization.SerialName

@SerialName("baaahs.Core:Paint/ShaderToy")
object ShaderToyPaintShader : ShaderPrototype("baaahs.Core:Paint/ShaderToy") {
    override val serializerRegistrar = objectSerializer(id, this)

    override val entryPointName: String = "mainImage"

    override val wellKnownInputPorts = listOf(
//              uniform vec3      iResolution;           // viewport resolution (in pixels)
//              uniform float     iTime;                 // shader playback time (in seconds)
//              uniform float     iTimeDelta;            // render time (in seconds)
//              uniform int       iFrame;                // shader playback frame
//              uniform float     iChannelTime[4];       // channel playback time (in seconds)
//              uniform vec3      iChannelResolution[4]; // channel resolution (in pixels)
//              uniform vec4      iMouse;                // mouse pixel coords. xy: current (if MLB down), zw: click
//              uniform samplerXX iChannel0..3;          // input channel. XX = 2D/Cube
//              uniform vec4      iDate;                 // (year, month, day, time in seconds)
//              uniform float     iSampleRate;           // sound sample rate (i.e., 44100)

        InputPort("iResolution", GlslType.Vec3, "Resolution", ContentType.Resolution),

//          float iTime (or iGlobalTime ) : seconds(+fracs) since the shader (re)started.
        InputPort("iTime", GlslType.Float, "Time", ContentType.Time),
        InputPort("iGlobalTime", GlslType.Float, "Global Time", ContentType.Time),

//          float iTimeDelta: duration since the previous frame.
        InputPort("iTimeDelta", GlslType.Float, "Time Delta"),

//          int iFrame: frames since the shader (re)started.
        InputPort("iFrame", GlslType.Float, "Frame"),
//          float iFrameRate: average FPS.
        InputPort("iFrameRate", GlslType.Float, "Frame Rate"),

//          float iChannelTime[4] : current time in video or sound.
        InputPort("iChannelTime", GlslType.from("float[4]"), "Channel Time"),
        InputPort("iMouse", GlslType.Vec2, "Mouse", ContentType.Mouse),

//          vec4 iDate: year-1, month-1, day, seconds(+fracs) since midnight.
        InputPort("iDate", GlslType.Vec4, "Date"),
        InputPort("iSampleRate", GlslType.Float, "Sample Rate"),
        InputPort("iChannelResolution", GlslType.from("vec3[4]"), "Channel Resolution"),

//          uniform samplerXX iChanneli;
        InputPort("iChannel0", GlslType.Sampler2D, "Channel 0", ContentType.Media),
        InputPort("iChannel1", GlslType.Sampler2D, "Channel 1", ContentType.Media),
        InputPort("iChannel2", GlslType.Sampler2D, "Channel 2", ContentType.Media),
        InputPort("iChannel3", GlslType.Sampler2D, "Channel 3", ContentType.Media)
    ).map { it.copy(isImplicit = true) }

    override val defaultInputPortsByType: Map<Pair<GlslType, Boolean>, InputPort> = listOf(
        InputPort("sm_FragCoord", GlslType.Vec2, "U/V Coordinates", ContentType.UvCoordinateStream)
    ).associateBy { it.type to (it.contentType?.isStream ?: false) }

    private val iPortsById = wellKnownInputPorts.associateBy { it.id }

    override val outputPort: OutputPort
        get() = OutputPort(ContentType.ColorStream, "Output Color")

    override fun findMagicInputPorts(glslCode: GlslCode): List<InputPort> {
        // ShaderToy shaders have a set of uniforms that are automatically declared;
        // see if we're using any of them.
        val iVars = glslCode.functions.flatMap { glslFunction ->
            Regex("\\w+").findAll(glslFunction.fullText).map { it.value }.filter { word ->
                iPortsById.containsKey(word)
            }.toList()
        }.toSet()

        return wellKnownInputPorts.filter { inputPort -> iVars.contains(inputPort.id) }
    }

    override val title: String = "Paint"
    override val suggestNew: Boolean = false
    override val defaultUpstreams: Map<ContentType, ShaderChannel> =
        mapOf(ContentType.UvCoordinateStream to ShaderChannel.Main)

    override val shaderType: ShaderType
        get() = ShaderType.Paint

    override val icon: Icon = CommonIcons.PaintShader

    override fun validate(glslCode: GlslCode): List<GlslError> {
        val errors = super.validate(glslCode)

        val entryPoint = findEntryPointOrNull(glslCode)
        return if (entryPoint != null) {
            val haveCoordInArg = entryPoint.params.any { it.isIn && it.type == GlslType.Vec2 }
            val haveColorOutArg = entryPoint.params.any { it.isOut && it.type == GlslType.Vec4 }
            return errors +
                    if (!haveCoordInArg || !haveColorOutArg)
                        GlslError(
                            "Missing arguments. " +
                                    "Signature should be \"void mainImage(in vec2 fragCoord, out vec4 fragColor)\".",
                            row = entryPoint.lineNumber
                        ).listOf()
                    else emptyList()
        } else errors
    }

    override val template: String get() = error("nope")
}