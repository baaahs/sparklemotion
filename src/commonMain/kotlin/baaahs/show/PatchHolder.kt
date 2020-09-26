package baaahs.show

import baaahs.app.ui.Editable

interface PatchHolder : Editable {
    override val title: String
    val patches: List<Patch>
    val eventBindings: List<EventBinding>
    val controlLayout: Map<String, List<String>>
}

class EmptyPatchHolder(override val title: String) : PatchHolder {
    override val patches: List<Patch>
        get() = emptyList()
    override val eventBindings: List<EventBinding>
        get() = emptyList()
    override val controlLayout: Map<String, List<String>>
        get() = emptyMap()
}