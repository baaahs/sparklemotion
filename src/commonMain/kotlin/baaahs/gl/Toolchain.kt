package baaahs.gl

import baaahs.device.FixtureType
import baaahs.gl.glsl.*
import baaahs.gl.patch.*
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.Stream
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShow
import baaahs.util.Logger
import baaahs.util.Stats

interface Toolchain {
    val plugins: Plugins

    fun parse(src: String, fileName: String? = null): GlslCode

    fun import(src: String, fileName: String? = null): Shader

    fun analyze(shader: Shader): ShaderAnalysis

    fun openShader(shaderAnalysis: ShaderAnalysis): OpenShader

    fun wiringOptions(
        currentOpenShader: OpenShader,
        parentMutableShow: MutableShow,
        mutablePatch: MutablePatch
    ): PatchOptions

    fun autoWire(
        shaders: Collection<OpenShader>,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        stream: Stream = Stream.Main,
        fixtureTypes: Collection<FixtureType> = emptyList()
    ): UnresolvedPatches

    fun autoWire(
        openShader: OpenShader,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        stream: Stream = Stream.Main,
        fixtureTypes: Collection<FixtureType> = emptyList()
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

    override fun parse(src: String, fileName: String?): GlslCode = stats.parse.time {
        glslParser.parse(src, fileName)
    }

    override fun import(src: String, fileName: String?): Shader = stats.import.time {
        val glslCode = parse(src, fileName)
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
        mutablePatch: MutablePatch
    ): PatchOptions = stats.autoWire.time {
        val streamsInfo = StreamsInfo(parentMutableShow, emptyList(), this)
        PatchOptions(
            currentOpenShader,
            Stream.Main,
            streamsInfo,
            currentLinks = mutablePatch.incomingLinks,
            plugins = plugins
        )
    }

    override fun autoWire(
        shaders: Collection<OpenShader>,
        defaultPorts: Map<ContentType, MutablePort>,
        stream: Stream,
        fixtureTypes: Collection<FixtureType>
    ): UnresolvedPatches =
        autoWirer.autoWire(shaders, stream, defaultPorts, fixtureTypes)

    override fun autoWire(
        openShader: OpenShader,
        defaultPorts: Map<ContentType, MutablePort>,
        stream: Stream,
        fixtureTypes: Collection<FixtureType>
    ): UnresolvedPatch =
        autoWirer.autoWire(openShader, stream, defaultPorts, fixtureTypes)
}

class CachingToolchain(
    private val delegate: Toolchain,
    private val name: String = "Cache"
) : Toolchain by delegate {
    var hits = 0
        private set
    var misses = 0
        private set

    private val shaderAnalysisCache = mutableMapOf<Shader, ShaderAnalysis>()
    private var pruneCandidates: MutableSet<Shader>? = null

    private fun cachedAnalysis(shader: Shader): ShaderAnalysis? {
        return shaderAnalysisCache[shader]?.also { hit(shader) }
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
        return cachedAnalysis(shader)
            ?: shaderAnalysisCache.getOrPut(shader) {
                uncachedAnalysis(shader).also { miss(shader) }
            }
    }

    fun <T> pruneUnused(block: () -> T): T {
        if (pruneCandidates != null)
            error("Already in a pruneUnused block.")

        val pruneSet = HashSet(shaderAnalysisCache.keys)
        pruneCandidates = pruneSet
        return try {
            block()
        } finally {
            pruneSet.forEach { shaderAnalysisCache.remove(it) }
            logger.debug { "$name: pruned ${pruneSet.size} entries" }
            pruneCandidates = null
        }
    }

    private fun hit(shader: Shader) {
        hits++
        logger.debug { "$name: hit for ${shader.title} ($hits hits and $misses misses so far)" }
        pruneCandidates?.remove(shader)
    }

    private fun miss(shader: Shader) {
        misses++
        logger.debug { "$name: miss for ${shader.title} ($hits hits and $misses misses so far)" }
    }

    companion object {
        private val logger = Logger<CachingToolchain>()
    }
}