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

data class MutableFeedPort(val feed: Feed) : MutablePort {
    override val title: String get() = feed.title
    override val icon: Icon get() = CommonIcons.Feed
    override val groupName: String get() = "Feed:"

    override fun toRef(showBuilder: ShowBuilder): PortRef =
        FeedRef(showBuilder.idFor(feed))

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        if (log.feeds.add(this)) visitor.visit(feed)
    }
}
fun Feed.editor() = MutableFeedPort(this)

data class MutableStream(val id: String) : MutablePort {
    override val title: String get() = "${id.englishize()} Stream"
    override val icon: Icon get() = CommonIcons.Stream
    override val groupName: String get() = "Stream:"

    override fun toRef(showBuilder: ShowBuilder): PortRef =
        StreamRef(build())

    fun build(): Stream =
        Stream(id)

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        if (log.streams.add(this)) visitor.visit(this)
    }

    companion object {
        fun from(id: String): MutableStream {
            return if (id.isNotBlank())
                MutableStream(id) else Stream.Main.toMutable()
        }
    }
}
fun Stream.editor() = MutableStream(this.id)

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
