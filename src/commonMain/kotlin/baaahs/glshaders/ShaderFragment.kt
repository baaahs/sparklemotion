package baaahs.glshaders

import baaahs.glshaders.GlslCode.*

interface ShaderFragment {
    val id: String
    val name: String
    val description: String?
    val shaderType: ShaderType
    val entryPoint: GlslFunction
    val inputPorts: List<InputPort>
    val outputPorts: List<OutputPort>
//    TODO val inputDefaults: Map<String, InputDefault>

    fun toGlsl(namespace: Namespace, portMap: Map<String, String> = emptyMap()): String
    fun invocationGlsl(namespace: Namespace, portMap: Map<String, String> = emptyMap()): String

    abstract class Base(protected val glslCode: GlslCode) : ShaderFragment {
        override val id: String = glslCode.title
        override val name: String = glslCode.title
        override val description: String? = null

        override fun toGlsl(namespace: Namespace, portMap: Map<String, String>): String {
            val buf = StringBuilder()

            val nonUniformGlobalsMap = hashMapOf<String, String>()
            glslCode.globalVars.forEach { glslVar ->
                if (!glslVar.isUniform) {
                    nonUniformGlobalsMap[glslVar.name] = namespace.qualify(glslVar.name)
                    buf.append(glslVar.toGlsl(namespace, glslCode.symbolNames, emptyMap()))
                    buf.append("\n")
                }
            }

            val symbolsToNamespace = glslCode.symbolNames.toSet()
            val symbolMap = portMap + nonUniformGlobalsMap
            glslCode.functions.forEach { glslFunction ->
                buf.append(glslFunction.toGlsl(namespace, symbolsToNamespace, symbolMap))
                buf.append("\n")
            }

            return buf.toString()
        }
    }

    companion object {
        fun tryColorShader(glslCode: GlslCode): ColorShader? {
            return when {
                glslCode.functionNames.contains("mainImage") ->
                    ShaderToyColorShader(glslCode)

                glslCode.functionNames.contains("main") ->
                    GenericColorShader(glslCode)

                else -> null
            }
        }

        fun tryUvTranslatorShader(glslCode: GlslCode): UvShader? {
            return when {
                glslCode.functionNames.contains("mainUvFromRaster") ->
                    UvShader(glslCode)

                else -> null
            }
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
                "iTimeDelta" to InputPort("float", "iTimeDelta", "Time Delta", ContentType.Unknown),

//                int iFrame: frames since the shader (re)started.
                "iFrame" to InputPort("float", "iFrame", "Frame", ContentType.Unknown),
//                float iFrameRate: average FPS.
                "iFrameRate" to InputPort("float", "iFrameRate", "Frame Rate", ContentType.Unknown),

//                float iChannelTime[4] : current time in video or sound.
                "iChannelTime" to InputPort("float[4]", "iChannelTime", "Channel Time", ContentType.Unknown),
                "iMouse" to InputPort("vec2", "iMouse", "Mouse", ContentType.XyCoordinate),

//                vec4 iDate: year-1, month-1, day, seconds(+fracs) since midnight.
                "iDate" to InputPort("vec4", "iDate", "Date", ContentType.Unknown),
                "iSampleRate" to InputPort("float", "iSampleRate", "Sample Rate", ContentType.Unknown),
                "iChannelResolution" to InputPort("vec3[4]", "iChannelResolution", "Channel Resolution", ContentType.Unknown),

                "iChannel0" to InputPort("sampleXX", "iChannel0", "Channel 0", ContentType.Unknown),
                "iChannel1" to InputPort("sampleXX", "iChannel1", "Channel 1", ContentType.Unknown),
                "iChannel2" to InputPort("sampleXX", "iChannel2", "Channel 2", ContentType.Unknown),
                "iChannel3" to InputPort("sampleXX", "iChannel3", "Channel 3", ContentType.Unknown)
//            uniform samplerXX iChanneli;
            )

            val uvCoordPort = InputPort("vec2", "sm_FragCoord", "Coordinates", ContentType.UvCoordinate)
        }

        override val entryPoint: GlslFunction =
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
                        InputPort(it.type, it.name, desc, ContentType.Unknown, it)
                    }()
            }

            val implicitUniforms = magicUniforms.mapNotNull { (k, v) -> if (iVars.contains(k)) v else null }

            explicitUniforms + implicitUniforms + uvCoordPort
        }
        override val outputPorts: List<OutputPort>
            get() = listOf()

        override fun invocationGlsl(namespace: Namespace, portMap: Map<String, String>): String {
            return "${namespace.qualify(entryPoint.name)}(sm_pixelColor, gl_FragCoord.xy)"
        }
    }

    class GenericColorShader(glslCode: GlslCode) : ColorShader(glslCode) {
        companion object {
            val magicUniforms = listOf(
                InputPort("vec4", "gl_FragCoord", "Coordinates", ContentType.UvCoordinate),
                InputPort("vec2", "resolution", "Resolution", ContentType.Resolution),
                InputPort("vec2", "mouse", "Mouse", ContentType.XyCoordinate),
                InputPort("float", "time", "Time", ContentType.Time)
            ).associateBy { it.name }

            val uvCoordPort = InputPort("vec4", "gl_FragCoord", "Coordinates", ContentType.UvCoordinate)
        }

        override val entryPoint: GlslFunction =
            glslCode.functions.find { it.name == "main" }!!

        override val inputPorts: List<InputPort> by lazy {
            glslCode.uniforms.map {
                magicUniforms[it.name]?.copy(type = it.type, glslVar = it)
                    ?: InputPort(it.type, it.name, it.name.nameify(), ContentType.Unknown, it)
            } + uvCoordPort
        }

        override val outputPorts: List<OutputPort>
            = listOf(OutputPort("vec4", "gl_FragColor", "Output Color", ContentType.UvCoordinate))

        override fun invocationGlsl(namespace: Namespace, portMap: Map<String, String>): String {
            return StringBuilder().apply {
                append(namespace.qualify(entryPoint.name), "()")
            }.toString()
        }
    }

    abstract class ColorShader(glslCode: GlslCode) : Base(glslCode) {
        override val shaderType: ShaderType = ShaderType.Color
    }

    class UvShader(glslCode: GlslCode) : Base(glslCode) {
        override val shaderType: ShaderType = ShaderType.Projection

        override val entryPoint: GlslFunction
            = glslCode.functions.find { it.name == "mainUvFromRaster" }!!

        override val inputPorts: List<InputPort> by lazy {
            glslCode.uniforms.map {
                GenericColorShader.magicUniforms[it.name]?.copy(type = it.type, glslVar = it)
                    ?: InputPort(it.type, it.name, it.name.nameify(), ContentType.Unknown, it)
            }
        }

        override val outputPorts: List<OutputPort>
            = listOf(OutputPort("vec2", "uvCoord", "U/V Coordinate", ContentType.UvCoordinate))

        override fun invocationGlsl(namespace: Namespace, portMap: Map<String, String>): String {
            val buf = StringBuilder()
            buf.append(namespace.qualify(entryPoint.name), "(gl_FragCoord.xy)")
            return buf.toString()
        }
    }

    fun String.nameify(): String {
        return replace(Regex("[A-Z]+")) { match -> " ${match.value}"}
            .trim().capitalize()
    }
}