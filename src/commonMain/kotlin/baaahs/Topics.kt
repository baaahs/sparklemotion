package baaahs

import kotlinx.serialization.serializer

object Topics {
    val availableShows = PubSub.Topic("availableShows", String.serializer())
    val selectedShow = PubSub.Topic("selectedShow", String.serializer())
    val primaryColor = PubSub.Topic("primaryColor", Color.serializer())
}