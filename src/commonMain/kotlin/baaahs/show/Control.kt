package baaahs.show

import baaahs.getBang
import baaahs.glshaders.PluginRef
import baaahs.show.mutable.ShowBuilder
import kotlinx.serialization.Serializable

interface Control {
    fun toControlRef(showBuilder: ShowBuilder): ControlRef
}

@Serializable
data class ControlRef(val type: Type, val id: String) {
    enum class Type {
        Special,
        DataSource;
    }

    fun dereference(dataSources: Map<String, DataSource>): Control {
        return when (type) {
            Type.Special -> SpecialControl(id)
            Type.DataSource -> dataSources.getBang(id, "data source")
        }
    }

    fun toShortString(): String = "$type:$id"
}

class SpecialControl(val pluginRef: PluginRef) : Control {
    constructor(id: String) : this(PluginRef.from(id))

    override fun toControlRef(showBuilder: ShowBuilder): ControlRef =
        ControlRef(ControlRef.Type.Special, pluginRef.toRef())
}

val scenesControl = SpecialControl("baaahs.Core:Scenes")
val patchesControl = SpecialControl("baaahs.Core:Patches")

