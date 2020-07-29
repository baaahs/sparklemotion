package baaahs.glshaders

import baaahs.show.ShaderOutPortRef

abstract class ColorShader(glslCode: GlslCode) : OpenShader.Base(glslCode) {
    override val shaderType: OpenShader.Type = OpenShader.Type.Color
}

class ShaderToyColorShader(glslCode: GlslCode) : ColorShader(glslCode) {
    companion object {
        val magicUniforms = listOf(
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

            InputPort("iResolution", "vec3", "Resolution", ContentType.Resolution),

//          float iTime (or iGlobalTime ) : seconds(+fracs) since the shader (re)started.
            InputPort("iTime", "float", "Time", ContentType.Time),
            InputPort("iGlobalTime", "float", "Global Time", ContentType.Time),

//          float iTimeDelta: duration since the previous frame.
            InputPort("iTimeDelta", "float", "Time Delta"),

//          int iFrame: frames since the shader (re)started.
            InputPort("iFrame", "float", "Frame"),
//          float iFrameRate: average FPS.
            InputPort("iFrameRate", "float", "Frame Rate"),

//          float iChannelTime[4] : current time in video or sound.
            InputPort("iChannelTime", "float[4]", "Channel Time"),
            InputPort("iMouse", "vec2", "Mouse", ContentType.Mouse),

//          vec4 iDate: year-1, month-1, day, seconds(+fracs) since midnight.
            InputPort("iDate", "vec4", "Date"),
            InputPort("iSampleRate", "float", "Sample Rate"),
            InputPort("iChannelResolution", "vec3[4]", "Channel Resolution"),

//          uniform samplerXX iChanneli;
            InputPort("iChannel0", "sampler2D", "Channel 0", ContentType.Media),
            InputPort("iChannel1", "sampler2D", "Channel 1", ContentType.Media),
            InputPort("iChannel2", "sampler2D", "Channel 2", ContentType.Media),
            InputPort("iChannel3", "sampler2D", "Channel 3", ContentType.Media)
        ).associateBy { it.id }

        val uvCoordPort = InputPort("sm_FragCoord", "vec2", "Coordinates", ContentType.UvCoordinate)
    }

    override val entryPoint: GlslCode.GlslFunction =
        glslCode.functions.find { it.name == "mainImage" }!!

    override val inputPorts: List<InputPort> by lazy {
        // ShaderToy shaders have a set of uniforms that are automatically declared;
        // see if we're using any of them.
        val iVars = glslCode.functions.flatMap { glslFunction ->
            Regex("\\w+").findAll(glslFunction.fullText).map { it.value }.filter { word ->
                magicUniforms.containsKey(word)
            }.toList()
        }.toSet()

        val implicitUniforms = magicUniforms.mapNotNull { (k, v) ->
            if (iVars.contains(k)) v else null
        }

        val explicitUniforms = glslCode.uniforms.map { toInputPort(it) }

        explicitUniforms + implicitUniforms + uvCoordPort
    }

    override val outputPort: OutputPort =
        OutputPort("vec4", ShaderOutPortRef.ReturnValue, "Output Color", ContentType.Color)

    override fun invocationGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String {
        return namespace.qualify(entryPoint.name) +
                "(sm_pixelColor, ${portMap["sm_FragCoord"] ?: "sm_FragCoord"}.xy)"
    }
}

class GenericColorShader(glslCode: GlslCode) : ColorShader(glslCode) {
    companion object {
        val wellKnownInputPorts = listOf(
            InputPort("gl_FragCoord", "vec4", "Coordinates", ContentType.UvCoordinate),
            InputPort("resolution", "vec2", "Resolution", ContentType.Resolution),
            InputPort("mouse", "vec2", "Mouse", ContentType.Mouse),
            InputPort("time", "float", "Time", ContentType.Time)
//                        varying vec2 surfacePosition; TODO
        ).associateBy { it.id }

        val uvCoordPort = InputPort("gl_FragCoord", "vec4", "Coordinates", ContentType.UvCoordinate)
    }

    val wellKnownInputPorts: Map<String, InputPort>
        get() = GenericColorShader.wellKnownInputPorts

    override val entryPoint: GlslCode.GlslFunction =
        glslCode.functions.find { it.name == "main" }!!

    override val inputPorts: List<InputPort> by lazy {
        glslCode.uniforms.map {
            wellKnownInputPorts[it.name]?.copy(dataType = it.dataType, glslVar = it)
                ?: toInputPort(it)
        } + uvCoordPort
    }
//    it.type, it.name, title, contentType,
//    it.hint?.plugin ?: contentType.pluginId, it.hint?.map ?: emptyMap()

    override val outputPort: OutputPort =
        OutputPort("vec4", "gl_FragColor", "Output Color", ContentType.Color)

    override fun invocationGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String {
        return StringBuilder().apply {
            append(namespace.qualify(entryPoint.name), "()")
        }.toString()
    }
}
