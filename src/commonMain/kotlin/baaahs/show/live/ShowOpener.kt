package baaahs.show.live

import baaahs.ShowPlayer
import baaahs.getBang
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.show.DataSource
import baaahs.show.Show

class ShowOpener(
    private val glslAnalyzer: GlslAnalyzer,
    private val show: Show,
    private val showPlayer: ShowPlayer
): OpenContext {
    val controls = show.controls.mapValues { (_, control) ->
        control.open(this)
    }
    override val allControls: List<OpenControl> get() = controls.values.toList()

    private val openShaders = show.shaders.mapValues { (_, shader) ->
        glslAnalyzer.openShader(shader)
    }

    private val resolver = ShaderInstanceResolver(
        openShaders,
        show.shaderInstances,
        show.dataSources
    )

    val allShaderInstances = resolver.getResolvedShaderInstances()

    override fun getControl(it: String): OpenControl = controls.getBang(it, "control")

    override fun getDataSource(id: String): DataSource = show.dataSources.getBang(id, "data source")

    override fun getShaderInstance(it: String): LiveShaderInstance = allShaderInstances.getBang(it, "shader instance")

    fun openShow(): OpenShow {
        return OpenShow(show, showPlayer, this)
    }

    override fun release() {
//        allControls.forEach { it.release() }
//        openShaders.forEach { it.release() }
//        allShaderInstances.forEach { it.release() }
    }
}