package baaahs.scene

import baaahs.ui.IObservable
import baaahs.ui.Observable

class SceneMonitor(
    openScene: OpenScene? = null,
    private val observable: Observable = Observable()
) : SceneProvider, IObservable by observable {
    override var openScene: OpenScene? = openScene
        private set

    fun onChange(newOpenScene: OpenScene?) {
        openScene = newOpenScene
        observable.notifyChanged()
    }
}