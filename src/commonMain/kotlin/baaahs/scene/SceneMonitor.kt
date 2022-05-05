package baaahs.scene

import baaahs.ui.IObservable
import baaahs.ui.Observable

class SceneMonitor(
    openScene: OpenScene? = null,
    private val observable: Observable = Observable()
) : SceneProvider, IObservable by observable {
    private val beforeChangeListeners = arrayListOf<SceneChangeListener>()

    override var openScene: OpenScene? = openScene
        private set

    override fun addBeforeChangeListener(callback: SceneChangeListener) {
        beforeChangeListeners.add(callback)
    }

    fun onChange(newOpenScene: OpenScene?) {
        beforeChangeListeners.forEach { it.invoke(newOpenScene) }

        openScene = newOpenScene
        observable.notifyChanged()
    }
}