package baaahs.gl.shader

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderType

abstract class PaintShader(
    shader: Shader,
    glslCode: GlslCode,
    plugins: Plugins
) : OpenShader.Base(shader, glslCode, plugins) {
    override val shaderType: ShaderType = ShaderType.Paint
}

class ShaderToyPaintShader(
    shader: Shader,
    glslCode: GlslCode,
    plugins: Plugins
) : PaintShader(shader, glslCode, plugins) {
    companion object {
        val proFormaInputPorts = listOf(
            InputPort("sm_FragCoord", GlslType.Vec2, "Coordinates", ContentType.UvCoordinateStream)
        )

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
        ).associateBy { it.id }

        val outputPort: OutputPort = OutputPort(ContentType.ColorStream, "Output Color")
    }

    override val entryPointName: String
        get() = "mainImage"
    override val proFormaInputPorts: List<InputPort>
        get() = ShaderToyPaintShader.proFormaInputPorts
    override val wellKnownInputPorts: Map<String, InputPort>
        get() = ShaderToyPaintShader.wellKnownInputPorts
    override val outputPort: OutputPort
        get() = ShaderToyPaintShader.outputPort

    override val inputPorts: List<InputPort> by lazy {
        // ShaderToy shaders have a set of uniforms that are automatically declared;
        // see if we're using any of them.
        val iVars = glslCode.functions.flatMap { glslFunction ->
            Regex("\\w+").findAll(glslFunction.fullText).map { it.value }.filter { word ->
                wellKnownInputPorts.containsKey(word)
            }.toList()
        }.toSet()

        val implicitUniforms = wellKnownInputPorts.mapNotNull { (k, v) ->
            if (iVars.contains(k)) v else null
        }

        val explicitUniforms = glslCode.uniforms.map { toInputPort(it) }

        explicitUniforms + implicitUniforms + proFormaInputPorts
    }

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String {
        return namespace.qualify(entryPoint.name) +
                "(${resultVar}, ${portMap["sm_FragCoord"] ?: "sm_FragCoord"}.xy)"
    }
}

class GenericPaintShader(
    shader: Shader,
    glslCode: GlslCode,
    plugins: Plugins
) : PaintShader(shader, glslCode, plugins) {
    companion object {
        val proFormaInputPorts = listOf(
            InputPort("gl_FragCoord", GlslType.Vec4, "Coordinates", ContentType.UvCoordinateStream)
        )

        val wellKnownInputPorts = listOf(
            InputPort("resolution", GlslType.Vec2, "Resolution", ContentType.Resolution),
            InputPort("mouse", GlslType.Vec2, "Mouse", ContentType.Mouse),
            InputPort("time", GlslType.Float, "Time", ContentType.Time)
//                        varying vec2 surfacePosition; TODO
        ).associateBy { it.id }

        val outputPort =
            OutputPort(ContentType.ColorStream, "Output Color", "gl_FragColor")
    }

    override val proFormaInputPorts: List<InputPort>
        get() = GenericPaintShader.proFormaInputPorts
    override val wellKnownInputPorts: Map<String, InputPort>
        get() = GenericPaintShader.wellKnownInputPorts
    override val outputPort: OutputPort
        get() = GenericPaintShader.outputPort

    override val entryPointName: String get() = "main"

    override fun invocationGlsl(
        namespace: GlslCode.Namespace,
        resultVar: String,
        portMap: Map<String, String>
    ): String {
        return namespace.qualify(entryPoint.name) + "()"
    }
}
