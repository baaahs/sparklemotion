package baaahs

import baaahs.glshaders.Plugins
import baaahs.io.Fs
import baaahs.io.RemoteFsSerializer
import baaahs.show.Show
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class ShowEditorState(
    val show: Show,
    val showState: ShowState,
    val isUnsaved: Boolean,
    val file: Fs.File?
) {
    companion object {
        fun createTopic(plugins: Plugins, fsSerializer: RemoteFsSerializer): PubSub.Topic<ShowEditorState?> {
            return PubSub.Topic(
                "showEditState",
                serializer().nullable,
                SerializersModule {
                    include(plugins.serialModule)
                    include(fsSerializer.serialModule)
                }
            )
        }
    }
}

fun Show.withState(showState: ShowState, isUnsaved: Boolean, file: Fs.File?): ShowEditorState {
    return ShowEditorState(this, showState.boundedBy(this), isUnsaved, file)
}