package baaahs

import baaahs.model.MovingHead
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

object Topics {
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
}