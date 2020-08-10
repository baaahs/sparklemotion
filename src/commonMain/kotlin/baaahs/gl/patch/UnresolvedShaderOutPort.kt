package baaahs.gl.patch

import baaahs.show.PortRef
import baaahs.show.mutable.MutableLink
import baaahs.show.mutable.ShowBuilder

class UnresolvedShaderOutPort(
    val unresolvedShaderInstance: UnresolvedShaderInstance,
    val portId: String
) : MutableLink.Port {
    override fun toRef(showBuilder: ShowBuilder): PortRef = TODO("not implemented")
    override fun displayName(): String = "Shader ${unresolvedShaderInstance.mutableShader.title} port $portId"
}