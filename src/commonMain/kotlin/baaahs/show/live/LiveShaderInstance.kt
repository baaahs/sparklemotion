package baaahs.show.live

import baaahs.getBang
import baaahs.glshaders.OpenShader
import baaahs.show.DataSourceRef
import baaahs.show.PortRef
import baaahs.show.ShaderInstance
import baaahs.show.ShaderRole

class LiveShaderInstance(
    val shader: OpenShader,
    val incomingLinks: Map<String, PortRef>,
    val role: ShaderRole?
) {
    fun findDataSourceRefs(): List<DataSourceRef> {
        return incomingLinks.values.filterIsInstance<DataSourceRef>()
    }

    companion object {
        fun from(
            shaderInstance: ShaderInstance,
            openShaders: Map<String, OpenShader>
        ): LiveShaderInstance {
            return LiveShaderInstance(
                openShaders.getBang(shaderInstance.shaderId, "shader"),
                shaderInstance.incomingLinks,
                shaderInstance.role
            )
        }
    }
}