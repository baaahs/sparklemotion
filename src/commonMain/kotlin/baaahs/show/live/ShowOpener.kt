package baaahs.show.live

import baaahs.ShowPlayer
import baaahs.ShowState
import baaahs.driverack.Channel
import baaahs.driverack.RackMap
import baaahs.getBang
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.shader.OpenShader
import baaahs.show.DataSource
import baaahs.show.Panel
import baaahs.show.Shader
import baaahs.show.Show
import baaahs.util.CacheBuilder
import baaahs.util.Logger
import kotlinx.serialization.KSerializer

open class ShowOpener(
    private val toolchain: Toolchain,
    private val show: Show,
    private val showPlayer: ShowPlayer
): OpenContext {
    init { logger.debug { "Opening show ${show.title}" } }

    private val openControlCache = CacheBuilder<String, OpenControl> { controlId ->
        show.getControl(controlId).open(controlId, this, showPlayer)
    }

    override val allControls: List<OpenControl> get() = openControlCache.all.values.toList()
    override val channels: MutableMap<String, OpenContext.RegisteredChannel<*>> = mutableMapOf()
    override val altChannels: MutableMap<String, OpenContext.RegisteredChannel<*>> = mutableMapOf()
    private val openShaders = CacheBuilder<String, OpenShader> { shaderId ->
        openShader(show.shaders.getBang(shaderId, "shaders"))
    }

    private val resolver = ShaderInstanceResolver(
        openShaders,
        show.shaderInstances,
        show.dataSources
    )

    private val allShaderInstances = resolver.getResolvedShaderInstances()

    override fun findControl(id: String): OpenControl? =
        if (show.controls.containsKey(id)) openControlCache[id] else null

    override fun getControl(id: String): OpenControl = openControlCache[id]

    override fun getDataSource(id: String): DataSource =
        show.dataSources.getBang(id, "data source")

    override fun getPanel(id: String): Panel =
        show.layouts.panels.getBang(id, "panel")

    override fun getShaderInstance(it: String): LiveShaderInstance =
        allShaderInstances.getBang(it, "shader instance")

    fun openShow(showState: ShowState? = null): OpenShow {
        return OpenShow(show, showPlayer, this)
            .also { if (showState != null) it.applyState(showState) }
            .also { it.applyConstraints() }
    }

    open fun openShader(shader: Shader) =
        toolchain.openShader(shader)

    override fun <T> registerChannel(
        id: String,
        initialValue: T,
        serializer: KSerializer<T>,
        controlledDataSource: DataSource
    ): Channel<T> {
        return OpenContext.RegisteredChannel(RackMap.Entry(id, initialValue, serializer), controlledDataSource).also {
            channels[id] = it
        }

    }

    override fun <T> registerAltChannel(
        id: String,
        initialValue: T,
        serializer: KSerializer<T>,
        controlledDataSource: DataSource
    ): Channel<T> {
        return OpenContext.RegisteredChannel(RackMap.Entry(id, initialValue, serializer), controlledDataSource).also {
            altChannels[id] = it
        }
    }

    override fun release() {
//        allControls.forEach { it.release() }
//        openShaders.forEach { it.release() }
//        allShaderInstances.forEach { it.release() }
    }

    companion object {
        private val logger = Logger<ShowOpener>()
    }
}