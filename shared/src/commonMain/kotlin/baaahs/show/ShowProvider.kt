package baaahs.show

import baaahs.show.live.OpenShow
import baaahs.ui.IObservable

interface ShowProvider : IObservable {
    val openShow: OpenShow?

    fun addBeforeChangeListener(callback: ShowChangeListener)
}

typealias ShowChangeListener = (OpenShow?) -> Unit