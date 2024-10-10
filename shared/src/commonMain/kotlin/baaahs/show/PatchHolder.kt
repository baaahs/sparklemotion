package baaahs.show

import baaahs.SparkleMotion
import baaahs.app.ui.editor.Editable

interface PatchHolder : Editable {
    override val title: String
    val patchIds: List<String>
    val eventBindings: List<EventBinding>
    @Deprecated("Only used for legacy layout.")
    val controlLayout: Map<String, List<String>>

    fun validatePatchHolder() {
        if (SparkleMotion.EXTRA_ASSERTIONS) patchIds.assertNoDuplicates()
    }
}

class EmptyPatchHolder(override val title: String) : PatchHolder {
    override val patchIds: List<String>
        get() = emptyList()
    override val eventBindings: List<EventBinding>
        get() = emptyList()
    @Deprecated("Only used for legacy layout.")
    override val controlLayout: Map<String, List<String>>
        get() = emptyMap()

    init { validatePatchHolder() }
}