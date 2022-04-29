package baaahs.scene

import baaahs.ui.IObservable

interface SceneProvider : IObservable {
    val openScene: OpenScene?

    fun addBeforeChangeListener(callback: SceneChangeListener)
}

typealias SceneChangeListener = (OpenScene?) -> Unit