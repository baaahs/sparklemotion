package baaahs.gl.patch

import baaahs.show.PortRef
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.ShowBuilder

class UnresolvedShaderOutPort(
    val unresolvedShaderInstance: UnresolvedShaderInstance,
    val portId: String
) : MutablePort {
    override fun toRef(showBuilder: ShowBuilder): PortRef = TODO("not implemented")
    override fun displayName(): String = "Shader ${unresolvedShaderInstance.mutableShader.title} port $portId"
}