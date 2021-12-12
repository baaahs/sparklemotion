package baaahs

import baaahs.io.Fs
import baaahs.io.RemoteFsSerializer
import baaahs.show.Show
import baaahs.show.ShowState
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class DocumentState<T, TState>(
    val document: T,
    val state: TState,
    val isUnsaved: Boolean,
    val file: Fs.File?
) {
    companion object {
        fun <T, TState> createTopic(
            serializersModule: SerializersModule,
            fsSerializer: RemoteFsSerializer,
            tSerializer: KSerializer<T>,
            stateSerializer: KSerializer<TState>
        ): PubSub.Topic<DocumentState<T, TState>?> {
            return PubSub.Topic(
                "showEditState",
                serializer(tSerializer, stateSerializer).nullable,
                SerializersModule {
                    include(serializersModule)
                    include(fsSerializer.serialModule)
                }
            )
        }
    }
}

fun Show.withState(showState: ShowState, isUnsaved: Boolean, file: Fs.File?) =
    DocumentState(this, showState, isUnsaved, file)