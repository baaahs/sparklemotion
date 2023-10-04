package baaahs.show

import baaahs.app.ui.editor.Editable
import baaahs.camelize
import baaahs.show.live.OpenContext
import baaahs.show.live.OpenControl
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow

interface Control : Editable {
    override val title: String

    val controlledFeedId: String? get() = null

    fun suggestId(): String = title.camelize()

    /** Don't call this directly; use [MutableShow.findControl]. */
    fun createMutable(mutableShow: MutableShow): MutableControl

    fun open(id: String, openContext: OpenContext): OpenControl
}