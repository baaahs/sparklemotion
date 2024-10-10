package baaahs.show

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.io.RemoteFsSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class ShowState(
    val controls: Map<String, Map<String, JsonElement>>
) {
    companion object {
        fun createTopic(
            serializersModule: SerializersModule,
            fsSerializer: RemoteFsSerializer
        ): PubSub.Topic<DocumentState<Show, ShowState>?> {
            return PubSub.Topic(
                "showEditState",
                DocumentState.serializer(Show.serializer(), serializer()).nullable,
                SerializersModule {
                    include(serializersModule)
                    include(fsSerializer.serialModule)
                }
            )
        }
    }
}