package baaahs.show.mutable

import baaahs.app.ui.CommonIcons
import baaahs.englishize
import baaahs.gl.glsl.GlslType
import baaahs.show.*
import baaahs.ui.Icon

interface MutablePort {
    val title: String
    val icon: Icon
    val groupName: String?

    fun toRef(showBuilder: ShowBuilder): PortRef
    fun accept(visitor: MutableShowVisitor, log: VisitationLog)
}

data class MutableDataSourcePort(val dataSource: DataSource) : MutablePort {
    override val title: String get() = dataSource.title
    override val icon: Icon get() = CommonIcons.DataSource
    override val groupName: String get() = "Data Source:"

    override fun toRef(showBuilder: ShowBuilder): PortRef =
        DataSourceRef(showBuilder.idFor(dataSource))

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        if (log.dataSources.add(this)) visitor.visit(dataSource)
    }
}
fun DataSource.editor() = MutableDataSourcePort(this)

data class MutableShaderChannel(val id: String) : MutablePort {
    override val title: String get() = "${id.englishize()} Channel"
    override val icon: Icon get() = CommonIcons.ShaderChannel
    override val groupName: String get() = "Channel:"

    override fun toRef(showBuilder: ShowBuilder): PortRef =
        ShaderChannelRef(build())

    fun build(): ShaderChannel =
        ShaderChannel(id)

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        if (log.shaderChannels.add(this)) visitor.visit(this)
    }

    companion object {
        fun from(id: String): MutableShaderChannel {
            return if (id.isNotBlank())
                MutableShaderChannel(id) else ShaderChannel.Main.toMutable()
        }
    }
}
fun ShaderChannel.editor() = MutableShaderChannel(this.id)

data class MutableOutputPort(private val portId: String) : MutablePort {
    override val title: String get() = "$portId Output"
    override val icon: Icon get() = error("not implemented")
    override val groupName: String get() = error("not implemented")

    override fun toRef(showBuilder: ShowBuilder): PortRef =
        OutputPortRef(portId)

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {}
}

data class MutableConstPort(private val glsl: String, private val type: GlslType) : MutablePort {
    override val title: String get() = "const($glsl)"
    override val icon: Icon get() = error("not implemented")
    override val groupName: String get() = error("not implemented")

    override fun toRef(showBuilder: ShowBuilder): PortRef =
        ConstPortRef(glsl, type.glslLiteral)

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {}
}
