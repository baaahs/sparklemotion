package baaahs.gl.shader.dialect

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslType
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.plugin.PluginRef
import baaahs.plugin.Plugins
import baaahs.plugin.core.CorePlugin
import baaahs.show.Shader
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

object ShaderToyShaderDialect : BaseShaderDialect("baaahs.Core:ShaderToy") {
    override val title: String
        get() = "ShaderToy"

    val wellKnownInputPorts = listOf(
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

        InputPort("iResolution", ContentType.Resolution, GlslType.Vec3, "Resolution"),

//          float iTime (or iGlobalTime ) : seconds(+fracs) since the shader (re)started.
        InputPort("iTime", ContentType.Time, GlslType.Float, "Time"),
//        InputPort("iGlobalTime", ContentType.Time, GlslType.Float, "Global Time"),

//          float iTimeDelta: duration since the previous frame.
/*
        InputPort("iTimeDelta", GlslType.Float, "Time Delta"),

//          int iFrame: frames since the shader (re)started.
        InputPort("iFrame", GlslType.Float, "Frame"),
//          float iFrameRate: average FPS.
        InputPort("iFrameRate", GlslType.Float, "Frame Rate"),

//          float iChannelTime[4] : current time in video or sound.
        InputPort("iChannelTime", GlslType.from("float[4]"), "Channel Time"),
*/
        InputPort("iMouse", ContentType.Mouse, GlslType.Vec4, "Mouse"),

//          vec4 iDate: year, month-1, day, seconds(+fracs) since midnight.
        InputPort("iDate", ContentType.Date, GlslType.Vec4, "Date",
            pluginRef = PluginRef(CorePlugin.id, "Date"),
            pluginConfig = buildJsonObject { put("zeroBasedMonth", JsonPrimitive(true)) }
        ),
/*
        InputPort("iSampleRate", GlslType.Float, "Sample Rate"),
        InputPort("iChannelResolution", GlslType.from("vec3[4]"), "Channel Resolution"),
*/

//          uniform samplerXX iChanneli;
        InputPort("iChannel0", ContentType.Media, GlslType.Sampler2D, "Channel 0"),
        InputPort("iChannel1", ContentType.Media, GlslType.Sampler2D, "Channel 1"),
        InputPort("iChannel2", ContentType.Media, GlslType.Sampler2D, "Channel 2"),
        InputPort("iChannel3", ContentType.Media, GlslType.Sampler2D, "Channel 3")
    ).map { it.copy(isImplicit = true) }

    override fun match(glslCode: GlslCode, plugins: Plugins): ShaderAnalyzer =
        ShaderToyShaderAnalyzer(glslCode, plugins)
}

class ShaderToyShaderAnalyzer(
    glslCode: GlslCode,
    plugins: Plugins
) : HintedShaderAnalyzer(glslCode, plugins) {
    override val dialect: ShaderDialect
        get() = ShaderToyShaderDialect

    override val entryPointName: String = "mainImage"

    override val wellKnownInputPorts = ShaderToyShaderDialect.wellKnownInputPorts

    override val defaultInputPortsByType: Map<GlslType, InputPort> = listOf(
        InputPort("sm_FragCoord", ContentType.UvCoordinate, GlslType.Vec2, "U/V Coordinates")
    ).associateBy { it.type }

    override fun adjustInputPorts(inputPorts: List<InputPort>): List<InputPort> {
        return inputPorts.map {
            if (it.type == GlslType.Vec2 && it.contentType.isUnknown()) {
                it.copy(contentType = ContentType.UvCoordinate)
            } else it
        }
    }

    override fun adjustOutputPorts(outputPorts: List<OutputPort>): List<OutputPort> {
        return outputPorts.map {
            if (it.dataType == GlslType.Vec4 && !it.isReturnValue() && it.contentType.isUnknown()) {
                it.copy(contentType = ContentType.Color, description = "Output Color")
            } else it
        }
    }

    override fun analyze(existingShader: Shader?): ShaderAnalysis {
        val shaderAnalysis = super.analyze(existingShader)
        val entryPoint = shaderAnalysis.entryPoint
        return if (entryPoint == null) shaderAnalysis else {
            val haveCoordInArg = entryPoint.params.any { it.isIn && it.type == GlslType.Vec2 }
            val haveColorOutArg = entryPoint.params.any { it.isOut && it.type == GlslType.Vec4 }

            if (haveCoordInArg && haveColorOutArg) shaderAnalysis else {
                WrappedShaderAnalysis(
                    shaderAnalysis, listOf(
                        GlslError(
                            "Missing arguments. " +
                                    "Signature should be \"void mainImage(in vec2 fragCoord, out vec4 fragColor)\".",
                            row = entryPoint.lineNumber
                        )
                    )
                )
            }
        }
    }

    class WrappedShaderAnalysis(
        private val delegate: ShaderAnalysis,
        override val errors: List<GlslError>
    ) : ShaderAnalysis by delegate {
        override val isValid: Boolean
            get() = false
    }
}