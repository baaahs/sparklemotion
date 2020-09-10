package baaahs.show.live

import baaahs.ShowPlayer
import baaahs.ShowState
import baaahs.getBang
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.shader.OpenShader
import baaahs.show.DataSource
import baaahs.show.Show
import baaahs.util.CacheBuilder

class ShowOpener(
    private val glslAnalyzer: GlslAnalyzer,
    private val show: Show,
    private val showPlayer: ShowPlayer
): OpenContext {
    private val openControlCache = CacheBuilder<String, OpenControl> { controlId ->
        show.getControl(controlId).open(controlId, this, showPlayer)
    }

    override val allControls: List<OpenControl> get() = openControlCache.all.values.toList()

    private val openShaders = CacheBuilder<String, OpenShader> { shaderId ->
        glslAnalyzer.openShader(show.shaders.getBang(shaderId, "shaders"))
    }

    private val resolver = ShaderInstanceResolver(
        openShaders,
        show.shaderInstances,
        show.dataSources
    )

    val allShaderInstances = resolver.getResolvedShaderInstances()

    override fun getControl(id: String): OpenControl = openControlCache[id]

    override fun getDataSource(id: String): DataSource =
        show.dataSources.getBang(id, "data source")

    override fun getShaderInstance(it: String): LiveShaderInstance =
        allShaderInstances.getBang(it, "shader instance")

    fun openShow(showState: ShowState? = null): OpenShow {
        return OpenShow(show, showPlayer, this)
            .also { if (showState != null) it.applyState(showState) }
            .also { it.applyConstraints() }
    }

    override fun release() {
//        allControls.forEach { it.release() }
//        openShaders.forEach { it.release() }
//        allShaderInstances.forEach { it.release() }
    }
}