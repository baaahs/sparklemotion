package baaahs.show

import baaahs.show.live.OpenShow
import baaahs.ui.IObservable
import baaahs.ui.Observable

class ShowMonitor(
    openShow: OpenShow? = null,
    private val observable: Observable = Observable()
) : ShowProvider, IObservable by observable {
    private val beforeChangeListeners = arrayListOf<ShowChangeListener>()

    override var openShow: OpenShow? = openShow
        private set

    override fun addBeforeChangeListener(callback: ShowChangeListener) {
        beforeChangeListeners.add(callback)
    }

    fun onChange(newOpenShow: OpenShow?) {
        beforeChangeListeners.forEach { it.invoke(newOpenShow) }

        openShow = newOpenShow
        observable.notifyChanged()
    }
}