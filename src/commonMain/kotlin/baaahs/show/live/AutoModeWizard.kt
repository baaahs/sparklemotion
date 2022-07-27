package baaahs.show.live

import baaahs.PubSub
import kotlinx.serialization.Serializable

class AutoModeWizard (
    val pubSub: PubSub.Server,
    val autoMode: AutoMode) {

    var autoModeState: AutoModeState = AutoModeState.Off

    fun setShow(newShow: OpenShow) {
        autoMode.setShow(newShow)
    }

    fun setState(newState: AutoModeState) {
        if (autoModeState == newState) return
        autoModeState = newState
        autoMode.setState(newState)
    }
}

@Serializable
enum class AutoModeState {
    On,
    Off
}