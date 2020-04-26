package baaahs.glshaders

abstract class ColorShader(glslCode: GlslCode) : ShaderFragment.Base(glslCode) {
    override val shaderType: ShaderFragment.Type = ShaderFragment.Type.Color
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

            "iResolution" to InputPort("vec3", "iResolution", "Resolution", GlslCode.ContentType.Resolution),

//                float iTime (or iGlobalTime ) : seconds(+fracs) since the shader (re)started.
            "iTime" to InputPort("float", "iTime", "Time", GlslCode.ContentType.Time),
            "iGlobalTime" to InputPort("float", "iGlobalTime", "Global Time", GlslCode.ContentType.Time),

//                float iTimeDelta: duration since the previous frame.
            "iTimeDelta" to InputPort("float", "iTimeDelta", "Time Delta", GlslCode.ContentType.Unknown),

//                int iFrame: frames since the shader (re)started.
            "iFrame" to InputPort("float", "iFrame", "Frame", GlslCode.ContentType.Unknown),
//                float iFrameRate: average FPS.
            "iFrameRate" to InputPort("float", "iFrameRate", "Frame Rate", GlslCode.ContentType.Unknown),

//                float iChannelTime[4] : current time in video or sound.
            "iChannelTime" to InputPort("float[4]", "iChannelTime", "Channel Time", GlslCode.ContentType.Unknown),
            "iMouse" to InputPort("vec2", "iMouse", "Mouse", GlslCode.ContentType.XyCoordinate),

//                vec4 iDate: year-1, month-1, day, seconds(+fracs) since midnight.
            "iDate" to InputPort("vec4", "iDate", "Date", GlslCode.ContentType.Unknown),
            "iSampleRate" to InputPort("float", "iSampleRate", "Sample Rate", GlslCode.ContentType.Unknown),
            "iChannelResolution" to InputPort("vec3[4]", "iChannelResolution", "Channel Resolution", GlslCode.ContentType.Unknown),

            "iChannel0" to InputPort("sampleXX", "iChannel0", "Channel 0", GlslCode.ContentType.Unknown),
            "iChannel1" to InputPort("sampleXX", "iChannel1", "Channel 1", GlslCode.ContentType.Unknown),
            "iChannel2" to InputPort("sampleXX", "iChannel2", "Channel 2", GlslCode.ContentType.Unknown),
            "iChannel3" to InputPort("sampleXX", "iChannel3", "Channel 3", GlslCode.ContentType.Unknown)
//            uniform samplerXX iChanneli;
        )

        val uvCoordPort = InputPort("vec2", "sm_FragCoord", "Coordinates", GlslCode.ContentType.UvCoordinate)
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
                    val desc = it.name.replace(Regex("^i"), "").nameify()
                    InputPort(it.type, it.name, desc, GlslCode.ContentType.Unknown, it)
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
            InputPort("vec4", "gl_FragCoord", "Coordinates", GlslCode.ContentType.UvCoordinate),
            InputPort("vec2", "resolution", "Resolution", GlslCode.ContentType.Resolution),
            InputPort("vec2", "mouse", "Mouse", GlslCode.ContentType.XyCoordinate),
            InputPort("float", "time", "Time", GlslCode.ContentType.Time)
//                        varying vec2 surfacePosition; TODO
        ).associateBy { it.name }

        val uvCoordPort = InputPort("vec4", "gl_FragCoord", "Coordinates", GlslCode.ContentType.UvCoordinate)
    }

    override val entryPoint: GlslCode.GlslFunction =
        glslCode.functions.find { it.name == "main" }!!

    override val inputPorts: List<InputPort> by lazy {
        glslCode.uniforms.map {
            magicUniforms[it.name]?.copy(type = it.type, glslVar = it)
                ?: InputPort(it.type, it.name, it.name.nameify(), GlslCode.ContentType.Unknown, it)
        } + uvCoordPort
    }

    override val outputPorts: List<OutputPort>
            = listOf(OutputPort("vec4", "gl_FragColor", "Output Color", GlslCode.ContentType.UvCoordinate))

    override fun invocationGlsl(namespace: GlslCode.Namespace, portMap: Map<String, String>): String {
        return StringBuilder().apply {
            append(namespace.qualify(entryPoint.name), "()")
        }.toString()
    }
}
