package baaahs.glshaders

import baaahs.show.DataSource

interface OpenPort

data class OpenDataSourcePort(val dataSource: DataSource) : OpenPort
data class OpenShaderPort(val shader: OpenShader, val portId: String) : OpenPort
data class OpenRolePort(val roleId: String, val portId: String) : OpenPort
data class OpenOutputPort(val portId: String) : OpenPort