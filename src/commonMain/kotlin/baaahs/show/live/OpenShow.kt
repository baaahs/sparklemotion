package baaahs.show.live

import baaahs.*
import baaahs.gl.shader.OpenShader
import baaahs.show.*
import baaahs.show.mutable.MutableShow

interface OpenShaders {
    fun getOpenShader(shader: Shader): OpenShader
}

class ShaderLookup(private val openShaders: Map<String, OpenShader>) : OpenShaders {
    override fun getOpenShader(shader: Shader): OpenShader {
        return openShaders.getBang(shader.id, "open shader")
    }
}

interface ShowContext : OpenShaders {
    val allControls: List<Control>
    val allDataSources: List<DataSource>

    fun getControl(it: String): Control
    fun getDataSource(id: String): DataSource
    fun release()
}
