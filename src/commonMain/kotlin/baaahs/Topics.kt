package baaahs

import baaahs.io.RemoteFsSerializer
import baaahs.model.MovingHead
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

object Topics {
    fun createClientData(fsSerializer: RemoteFsSerializer) =
        PubSub.Topic("clientData", ClientData.serializer().nullable, fsSerializer.serialModule)

    val showState = PubSub.Topic("showState", ShowState.serializer().nullable)

    val activeGadgets =
        PubSub.Topic("activeGadgets", GadgetData.serializer().list, gadgetModule)

    val movingHeads =
        PubSub.Topic("movingHeads", MovingHead.serializer().list)

    val movingHeadPresets =
        PubSub.Topic(
            "movingHeadPresets",
            MapSerializer(String.serializer(), MovingHead.MovingHeadPosition.serializer())
        )

    class Commands(fsSerializer: RemoteFsSerializer) {
        val newShow =
            PubSub.CommandPort("pinky/newShow",
                NewShowCommand.serializer(), NewShowCommand.Response.serializer(), fsSerializer.serialModule)
        val switchToShow =
            PubSub.CommandPort("pinky/switchToShow",
                SwitchToShowCommand.serializer(), SwitchToShowCommand.Response.serializer(), fsSerializer.serialModule)
        val saveShow =
            PubSub.CommandPort("pinky/saveShow",
                SaveShowCommand.serializer(), SaveShowCommand.Response.serializer(), fsSerializer.serialModule)
        val saveAsShow =
            PubSub.CommandPort("pinky/saveAsShow",
                SaveAsShowCommand.serializer(), SaveAsShowCommand.Response.serializer(), fsSerializer.serialModule)
    }
}