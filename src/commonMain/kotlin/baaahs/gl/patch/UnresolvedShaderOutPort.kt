package baaahs.gl.patch

import baaahs.show.PortRef
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShowVisitor
import baaahs.show.mutable.ShowBuilder
import baaahs.show.mutable.VisitationLog
import baaahs.ui.Icon

class UnresolvedShaderOutPort(
    val unresolvedShaderInstance: UnresolvedShaderInstance,
    val portId: String
) : MutablePort {
    override fun toRef(showBuilder: ShowBuilder): PortRef = error("not implemented")
    override val title: String = "Shader ${unresolvedShaderInstance.mutableShader.title} port $portId"
    override val icon: Icon get() = TODO("not implemented")
    override val groupName: String? get() = TODO("not implemented")

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) = error("not implemented")
}