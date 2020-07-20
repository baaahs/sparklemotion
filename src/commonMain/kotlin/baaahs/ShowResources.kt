package baaahs

import baaahs.glshaders.GlslAnalyzer
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.OpenShader
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslContext
import baaahs.model.ModelInfo
import baaahs.show.DataSource
import baaahs.show.Shader
import kotlinx.serialization.builtins.nullable

interface ShowResources {
    val plugins: Plugins
    val glslContext: GlslContext
    val modelInfo: ModelInfo
    val showWithStateTopic: PubSub.Topic<ShowWithState?>
    val dataSources: List<DataSource>

    fun <T : Gadget> createdGadget(id: String, gadget: T)
    fun <T : Gadget> useGadget(id: String): T

    fun openShader(shader: Shader): OpenShader
    fun openDataFeed(id: String, dataSource: DataSource): GlslProgram.DataFeed
    fun useDataFeed(dataSource: DataSource): GlslProgram.DataFeed

    fun createShowWithStateTopic(): PubSub.Topic<ShowWithState?> {
        return PubSub.Topic(
            "showWithState",
            ShowWithState.serializer().nullable,
            plugins.serialModule
        )
    }

    fun releaseUnused()
}

abstract class BaseShowResources(
    final override val plugins: Plugins,
    final override val modelInfo: ModelInfo
) : ShowResources {
    private val glslAnalyzer = GlslAnalyzer()

    override val showWithStateTopic: PubSub.Topic<ShowWithState?> by lazy { createShowWithStateTopic() }

    private val dataFeeds = mutableMapOf<DataSource, GlslProgram.DataFeed>()
    private val shaders = mutableMapOf<Shader, OpenShader>()

    override val dataSources: List<DataSource> get() = dataFeeds.keys.toList()

    override fun openDataFeed(id: String, dataSource: DataSource): GlslProgram.DataFeed {
        return dataFeeds.getOrPut(dataSource) { dataSource.createFeed(this, id) }
    }

    override fun useDataFeed(dataSource: DataSource): GlslProgram.DataFeed {
        return dataFeeds[dataSource]!!
    }

    override fun openShader(shader: Shader): OpenShader {
        return shaders.getOrPut(shader) { glslAnalyzer.asShader(shader) }
    }

    override fun releaseUnused() {
        ArrayList(dataFeeds.entries).forEach { (dataSource, dataFeed) ->
            if (!dataFeed.inUse()) dataFeeds.remove(dataSource)
        }

        ArrayList(shaders.entries).forEach { (shader, openShader) ->
            if (!openShader.inUse()) shaders.remove(shader)
        }
    }
}