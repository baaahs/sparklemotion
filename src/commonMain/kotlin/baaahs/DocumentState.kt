package baaahs

import baaahs.io.Fs
import baaahs.show.Show
import baaahs.show.ShowState
import kotlinx.serialization.Serializable

@Serializable
data class DocumentState<T, TState>(
    val document: T,
    val state: TState,
    val isUnsaved: Boolean,
    val file: Fs.File?
)

fun Show.withState(showState: ShowState, isUnsaved: Boolean, file: Fs.File?) =
    DocumentState(this, showState, isUnsaved, file)