package baaahs.scene

import baaahs.ui.IObservable
import baaahs.ui.Observable

class SceneMonitor(
    openScene: OpenScene? = null,
    private val observable: Observable = Observable()
) : SceneProvider, IObservable by observable {
    private val beforeChangeListeners = arrayListOf<BeforeChangeListener>()

    override var openScene: OpenScene? = openScene
        private set

    override fun addBeforeChangeListener(callback: BeforeChangeListener) {
        beforeChangeListeners.add(callback)
    }

    fun onChange(newOpenScene: OpenScene?) {
        beforeChangeListeners.forEach { it.invoke(newOpenScene) }

        openScene = newOpenScene
        observable.notifyChanged()
    }
}

typealias BeforeChangeListener = (OpenScene?) -> Unit