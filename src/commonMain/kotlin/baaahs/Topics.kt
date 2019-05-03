package baaahs

import kotlinx.serialization.list
import kotlinx.serialization.serializer

object Topics {
    val availableShows = PubSub.Topic("availableShows", String.serializer().list)
    val selectedShow = PubSub.Topic("selectedShow", String.serializer())
    val primaryColor = PubSub.Topic("primaryColor", Color.serializer())
}