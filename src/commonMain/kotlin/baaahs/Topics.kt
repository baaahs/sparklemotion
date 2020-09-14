package baaahs

import baaahs.io.RemoteFsSerializer
import baaahs.model.MovingHead
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerialModule

object Topics {
    fun createClientData(fsSerializer: RemoteFsSerializer) =
        PubSub.Topic("clientData", ClientData.serializer().nullable, fsSerializer.serialModule)

    val serverNotices =
        PubSub.Topic("serverNotices", Pinky.ServerNotice.serializer().list)

    val pinkyState =
        PubSub.Topic("pinkyState", PinkyState.serializer())

    val activeGadgets =
        PubSub.Topic("activeGadgets", GadgetData.serializer().list, Gadget.serialModule)

    val movingHeads =
        PubSub.Topic("movingHeads", MovingHead.serializer().list)

    val movingHeadPresets =
        PubSub.Topic(
            "movingHeadPresets",
            MapSerializer(String.serializer(), MovingHead.MovingHeadPosition.serializer())
        )

    class Commands(serialModule: SerialModule) {
        val newShow =
            PubSub.CommandPort("pinky/newShow",
                NewShowCommand.serializer(), NewShowCommand.Response.serializer(), serialModule)
        val switchToShow =
            PubSub.CommandPort("pinky/switchToShow",
                SwitchToShowCommand.serializer(), SwitchToShowCommand.Response.serializer(), serialModule)
        val saveShow =
            PubSub.CommandPort("pinky/saveShow",
                SaveShowCommand.serializer(), SaveShowCommand.Response.serializer(), serialModule)
        val saveAsShow =
            PubSub.CommandPort("pinky/saveAsShow",
                SaveAsShowCommand.serializer(), SaveAsShowCommand.Response.serializer(), serialModule)
    }
}