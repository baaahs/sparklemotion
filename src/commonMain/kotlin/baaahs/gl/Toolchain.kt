package baaahs.gl

import baaahs.fixtures.DeviceType
import baaahs.gl.glsl.*
import baaahs.gl.patch.*
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShaderInstance
import baaahs.show.mutable.MutableShow

interface Toolchain {
    val plugins: Plugins

    fun parse(src: String): GlslCode

    fun import(src: String): Shader

    fun analyze(shader: Shader): ShaderAnalysis

    fun openShader(shader: Shader): OpenShader {
        return openShader(analyze(shader))
    }

    fun openShader(shaderAnalysis: ShaderAnalysis): OpenShader

    fun wiringOptions(
        currentOpenShader: OpenShader,
        parentMutableShow: MutableShow,
        mutableShaderInstance: MutableShaderInstance
    ): ShaderInstanceOptions

    fun autoWire(
        vararg shaders: Shader,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        deviceTypes: Collection<DeviceType> = emptyList()
    ): UnresolvedPatch

    fun autoWire(
        vararg shaders: OpenShader,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        deviceTypes: Collection<DeviceType> = emptyList()
    ): UnresolvedPatch
}

class RootToolchain(
    override val plugins: Plugins,
    private val glslParser: GlslParser = GlslParser(),
    val glslAnalyzer: GlslAnalyzer = GlslAnalyzer(plugins),
    val autoWirer: AutoWirer = AutoWirer(plugins)
) : Toolchain {
    override fun parse(src: String): GlslCode {
        return glslParser.parse(src)
    }

    override fun import(src: String): Shader {
        val glslCode = parse(src)
        return glslAnalyzer.analyze(glslCode).shader
    }

    override fun analyze(shader: Shader): ShaderAnalysis {
        val glslCode = try {
            parse(shader.src)
        } catch (e: GlslException) {
            return ErrorsShaderAnalysis(shader.src, e, shader)
        }
        return glslAnalyzer.analyze(glslCode, shader)
    }

    override fun openShader(shaderAnalysis: ShaderAnalysis): OpenShader {
        return glslAnalyzer.openShader(shaderAnalysis)
    }

    override fun wiringOptions(
        currentOpenShader: OpenShader,
        parentMutableShow: MutableShow,
        mutableShaderInstance: MutableShaderInstance
    ): ShaderInstanceOptions {
        val channelsInfo = ChannelsInfo(parentMutableShow, emptyList(), this)
        return ShaderInstanceOptions(
            currentOpenShader,
            ShaderChannel.Main,
            channelsInfo,
            currentLinks = mutableShaderInstance.incomingLinks,
            plugins = plugins
        )
    }

    override fun autoWire(
        vararg shaders: Shader,
        defaultPorts: Map<ContentType, MutablePort>,
        shaderChannel: ShaderChannel,
        deviceTypes: Collection<DeviceType>
    ): UnresolvedPatch {
        val openShaders = shaders.associate { it to openShader(it) }
        return autoWirer.autoWire(openShaders.values, shaderChannel, defaultPorts, deviceTypes)
    }

    override fun autoWire(
        vararg shaders: OpenShader,
        defaultPorts: Map<ContentType, MutablePort>,
        shaderChannel: ShaderChannel,
        deviceTypes: Collection<DeviceType>
    ): UnresolvedPatch {
        return autoWirer.autoWire(shaders.toList(), shaderChannel, defaultPorts, deviceTypes)
    }
}

class CachingToolchain(private val delegate: Toolchain) : Toolchain by delegate {
    var hits = 0
    var misses = 0

    private val shaderAnalysisCache = mutableMapOf<Shader, ShaderAnalysis>()

    private fun cachedAnalysis(shader: Shader): ShaderAnalysis? {
        return shaderAnalysisCache[shader]
            ?: (delegate as? CachingToolchain)?.cachedAnalysis(shader)
    }

    private fun uncachedAnalysis(shader: Shader): ShaderAnalysis {
        return if (delegate is CachingToolchain) {
            delegate.uncachedAnalysis(shader)
        } else {
            // We've gotten to the RootToolchain.
            delegate.analyze(shader)
        }
    }

    override fun analyze(shader: Shader): ShaderAnalysis {
        return shaderAnalysisCache[shader]?.also { hits++ }
            ?: cachedAnalysis(shader)
            ?: shaderAnalysisCache.getOrPut(shader) {
                uncachedAnalysis(shader).also { misses++ }
            }
    }
}