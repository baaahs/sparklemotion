package baaahs

import baaahs.model.MovingHead
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer

object Topics {
    val currentShow = PubSub.Topic("currentShow", baaahs.show.Show.serializer())
    val showState = PubSub.Topic("showState", ShowState.serializer())

    val availableShows = PubSub.Topic("availableShows", String.serializer().list)
    val activeGadgets = PubSub.Topic("activeGadgets", GadgetData.serializer().list)
    val movingHeads = PubSub.Topic("movingHeads", MovingHead.serializer().list)

    val movingHeadPresets = PubSub.Topic(
        "movingHeadPresets",
        MapSerializer(String.serializer(), MovingHead.MovingHeadPosition.serializer())
    )
}