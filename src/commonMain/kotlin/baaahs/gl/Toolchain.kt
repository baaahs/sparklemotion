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
import baaahs.util.Logger
import baaahs.util.Stats

interface Toolchain {
    val plugins: Plugins

    fun parse(src: String): GlslCode

    fun import(src: String): Shader

    fun analyze(shader: Shader): ShaderAnalysis

    fun openShader(shaderAnalysis: ShaderAnalysis): OpenShader

    fun wiringOptions(
        currentOpenShader: OpenShader,
        parentMutableShow: MutableShow,
        mutableShaderInstance: MutableShaderInstance
    ): ShaderInstanceOptions

    fun autoWire(
        shaders: Collection<OpenShader>,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        deviceTypes: Collection<DeviceType> = emptyList()
    ): UnresolvedPatch
}

class ToolchainStats : Stats() {
    val parse by statistic
    val import by statistic
    val analyze by statistic
    val openShader by statistic
    val autoWire by statistic
}

class RootToolchain(
    override val plugins: Plugins,
    private val glslParser: GlslParser = GlslParser(),
    val glslAnalyzer: GlslAnalyzer = GlslAnalyzer(plugins),
    val autoWirer: AutoWirer = AutoWirer(plugins)
) : Toolchain {
    val stats = ToolchainStats()

    override fun parse(src: String): GlslCode = stats.parse.time {
        glslParser.parse(src)
    }

    override fun import(src: String): Shader = stats.import.time {
        val glslCode = parse(src)
        glslAnalyzer.analyze(glslCode).shader
    }

    override fun analyze(shader: Shader): ShaderAnalysis {
        val glslCode = try {
            parse(shader.src)
        } catch (e: GlslException) {
            return ErrorsShaderAnalysis(shader.src, e, shader)
        }
        return stats.analyze.time {
            glslAnalyzer.analyze(glslCode, shader)
        }
    }

    override fun openShader(shaderAnalysis: ShaderAnalysis): OpenShader {
        return stats.openShader.time {
            glslAnalyzer.openShader(shaderAnalysis)
        }
    }

    override fun wiringOptions(
        currentOpenShader: OpenShader,
        parentMutableShow: MutableShow,
        mutableShaderInstance: MutableShaderInstance
    ): ShaderInstanceOptions = stats.autoWire.time {
        val channelsInfo = ChannelsInfo(parentMutableShow, emptyList(), this)
        ShaderInstanceOptions(
            currentOpenShader,
            ShaderChannel.Main,
            channelsInfo,
            currentLinks = mutableShaderInstance.incomingLinks,
            plugins = plugins
        )
    }

    override fun autoWire(
        shaders: Collection<OpenShader>,
        defaultPorts: Map<ContentType, MutablePort>,
        shaderChannel: ShaderChannel,
        deviceTypes: Collection<DeviceType>
    ): UnresolvedPatch {
        return autoWirer.autoWire(shaders, shaderChannel, defaultPorts, deviceTypes)
    }
}

class CachingToolchain(
    private val delegate: Toolchain,
    private val name: String = "Cache"
) : Toolchain by delegate {
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
        return shaderAnalysisCache[shader]?.also { hit(shader) }
            ?: cachedAnalysis(shader)
            ?: shaderAnalysisCache.getOrPut(shader) {
                uncachedAnalysis(shader).also { miss(shader) }
            }
    }

    private fun hit(shader: Shader) {
        hits++
        logger.debug { "$name: hit for ${shader.title} ($hits hits and $misses misses so far)" }
    }

    private fun miss(shader: Shader) {
        misses++
        logger.debug { "$name: miss for ${shader.title} ($hits hits and $misses misses so far)" }
    }

    companion object {
        private val logger = Logger<CachingToolchain>()
    }
}