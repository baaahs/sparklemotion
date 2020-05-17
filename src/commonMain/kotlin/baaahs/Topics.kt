package baaahs

import baaahs.model.MovingHead
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer

object Topics {
    val availableShows = PubSub.Topic("availableShows", String.serializer().list)
    val selectedShow = PubSub.Topic("selectedShow", String.serializer())
    val activeGadgets = PubSub.Topic("activeGadgets", GadgetData.serializer().list)
    val movingHeads = PubSub.Topic("movingHeads", MovingHead.serializer().list)

    val movingHeadPresets = PubSub.Topic(
        "movingHeadPresets",
        MapSerializer(String.serializer(), MovingHead.MovingHeadPosition.serializer())
    )
}