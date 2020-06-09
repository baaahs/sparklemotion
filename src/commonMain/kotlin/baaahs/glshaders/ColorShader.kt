package baaahs.glshaders

import baaahs.glshaders.GlslCode.ContentType
import kotlinx.serialization.Serializable

abstract class ColorShader(glslCode: GlslCode) : ShaderFragment.Base(glslCode) {
    override val shaderType: ShaderFragment.Type = ShaderFragment.Type.Color

    protected fun guessInputPort(it: GlslCode.GlslVar, title: String = it.name.capitalize()): InputPort {
        val contentType = when (it.type) {
            "float" -> ContentType.Float
            "vec2" -> ContentType.XyCoordinate
            "vec3" -> ContentType.Color
            "vec4" -> ContentType.Color
            else -> ContentType.Unknown
        }
        return InputPort(
            it.type, it.name, title, contentType,
            it.hint?.plugin ?: contentType.pluginId, it.hint?.map ?: emptyMap()
        )
    }
}

class ShaderToyColorShader(glslCode: GlslCode) : ColorShader(glslCode) {
    companion object {
        val magicUniforms = linkedMapOf(
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

            "iResolution" to InputPort("vec3", "iResolution", "Resolution", ContentType.Resolution),

//                float iTime (or iGlobalTime ) : seconds(+fracs) since the shader (re)started.
            "iTime" to InputPort("float", "iTime", "Time", ContentType.Time),
            "iGlobalTime" to InputPort("float", "iGlobalTime", "Global Time", ContentType.Time),

//                float iTimeDelta: duration since the previous frame.
            "iTimeDelta" to InputPort("float", "iTimeDelta", "Time Delta", ContentType.Float),

//                int iFrame: frames since the shader (re)started.
            "iFrame" to InputPort("float", "iFrame", "Frame", ContentType.Float),
//                float iFrameRate: average FPS.
            "iFrameRate" to InputPort("float", "iFrameRate", "Frame Rate", ContentType.Float),

//                float iChannelTime[4] : current time in video or sound.
            "iChannelTime" to InputPort("float[4]", "iChannelTime", "Channel Time", ContentType.Float),
            "iMouse" to InputPort("vec2", "iMouse", "Mouse", ContentType.XyCoordinate),

//                vec4 iDate: year-1, month-1, day, seconds(+fracs) since midnight.
            "iDate" to InputPort("vec4", "iDate", "Date", ContentType.Float),
            "iSampleRate" to InputPort("float", "iSampleRate", "Sample Rate", ContentType.Float),
            "iChannelResolution" to InputPort("vec3[4]", "iChannelResolution", "Channel Resolution", ContentType.Float),

            "iChannel0" to InputPort("sampleXX", "iChannel0", "Channel 0", ContentType.Float),
            "iChannel1" to InputPort("sampleXX", "iChannel1", "Channel 1", ContentType.Float),
            "iChannel2" to InputPort("sampleXX", "iChannel2", "Channel 2", ContentType.Float),
            "iChannel3" to InputPort("sampleXX", "iChannel3", "Channel 3", ContentType.Float)
//            uniform samplerXX iChanneli;
        )

        val uvCoordPort = InputPort("vec2", "sm_FragCoord", "Coordinates", ContentType.UvCoordinate)
    }

    override val entryPoint: GlslCode.GlslFunction =
        glslCode.functions.find { it.name == "mainImage" }!!

    override val inputPorts: List<InputPort> by lazy {
        val iVars = glslCode.functions.flatMap { glslFunction ->
            Regex("\\w+").findAll(glslFunction.fullText).map { it.value }.filter { word ->
                magicUniforms.containsKey(word)
            }.toList()
        }.toSet()

        val explicitUniforms = glslCode.uniforms.map {
            magicUniforms[it.name]?.copy(type = it.type, glslVar = it)
                ?: {
                    val desc = it.name.replace(Regex("^i"), "").capitalize()
                    guessInputPort(it, desc)
                }()
        }

        val implicitUniforms = magicUniforms.mapNotNull { (k, v) -> if (iVars.contains(k)) v else null }

        explicitUniforms + implicitUniforms + uvCoordPort
    }
    override val outputPorts: List<OutputPort>
        get() = listOf()

    override fun invocationGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String {
        return namespace.qualify(entryPoint.name) +
                "(sm_pixelColor, ${portMap["sm_FragCoord"] ?: "sm_FragCoord"}.xy)"
    }
}

class GenericColorShader(glslCode: GlslCode) : ColorShader(glslCode) {
    companion object {
        val magicUniforms = listOf(
            InputPort("vec4", "gl_FragCoord", "Coordinates", ContentType.UvCoordinate),
            InputPort("vec2", "resolution", "Resolution", ContentType.Resolution),
            InputPort("vec2", "mouse", "Mouse", ContentType.XyCoordinate),
            InputPort("float", "time", "Time", ContentType.Time)
//                        varying vec2 surfacePosition; TODO
        ).associateBy { it.id }

        val uvCoordPort = InputPort("vec4", "gl_FragCoord", "Coordinates", ContentType.UvCoordinate)
    }

    override val entryPoint: GlslCode.GlslFunction =
        glslCode.functions.find { it.name == "main" }!!

    override val inputPorts: List<InputPort> by lazy {
        glslCode.uniforms.map {
            magicUniforms[it.name]?.copy(type = it.type, glslVar = it)
                ?: guessInputPort(it)
        } + uvCoordPort
    }

    override val outputPorts: List<OutputPort>
            = listOf(OutputPort("vec4", "gl_FragColor", "Output Color", ContentType.UvCoordinate))

    override fun invocationGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String {
        return StringBuilder().apply {
            append(namespace.qualify(entryPoint.name), "()")
        }.toString()
    }
}
