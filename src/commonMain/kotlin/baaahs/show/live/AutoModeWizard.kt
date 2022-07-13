package baaahs.show.live

import baaahs.PubSub
import baaahs.sm.webapi.AutoModeCommand
import baaahs.sm.webapi.Topics
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule

class AutoModeWizard (
    val pubSub: PubSub.Server,
    val autoMode: AutoMode) {

    var autoModeState: AutoModeState = AutoModeState.Off

    fun setShow(newShow: OpenShow) {
        autoMode.setShow(newShow)
    }

    fun setState(newState: AutoModeState) {
        autoModeState = newState
        autoMode.setState(newState)
    }
}

@Serializable
enum class AutoModeState {
    On,
    Off
}