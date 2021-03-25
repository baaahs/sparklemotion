package baaahs.show

import baaahs.ShowPlayer
import baaahs.app.ui.Editable
import baaahs.show.live.OpenContext
import baaahs.show.live.OpenControl
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow

interface Control : Editable {
    override val title: String
    fun suggestId(): String = "control"

    /** Don't call this directly; use [MutableShow.findControl]. */
    fun createMutable(mutableShow: MutableShow): MutableControl

    fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenControl
}