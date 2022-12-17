package baaahs.show

import baaahs.getBang
import baaahs.gl.glsl.GlslType
import baaahs.show.mutable.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PortRef {
    abstract fun dereference(mutableShow: MutableShow): MutablePort
}

@Serializable @SerialName("datasource")
data class FeedRef(val dataSourceId: String) : PortRef() {
    override fun dereference(mutableShow: MutableShow): MutablePort =
        mutableShow.dataSources.getBang(dataSourceId, "feed")
}

@Serializable @SerialName("stream")
data class StreamRef(val stream: Stream) : PortRef() {
    override fun dereference(mutableShow: MutableShow): MutablePort =
        MutableStream(stream.id)
}

@Serializable @SerialName("output")
data class OutputPortRef(val portId: String) : PortRef() {
    override fun dereference(mutableShow: MutableShow): MutablePort =
        MutableOutputPort(portId)
}

@Serializable @SerialName("const")
data class ConstPortRef(val glsl: String, val type: String) : PortRef() {
    override fun dereference(mutableShow: MutableShow): MutablePort =
        MutableConstPort(glsl, GlslType.from(type))
}
