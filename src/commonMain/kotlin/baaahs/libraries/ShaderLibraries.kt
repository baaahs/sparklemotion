package baaahs.libraries

import baaahs.PubSub
import baaahs.SearchShaderLibraries
import baaahs.Topics
import baaahs.io.RemoteFsSerializer
import kotlinx.serialization.modules.SerializersModule

class ShaderLibraries(pubSub: PubSub.Client, remoteFsSerializer: RemoteFsSerializer) {
    val facade = Facade()

    private val shaderLibraries = mutableMapOf<String, ShaderLibrary>()

    private val channel = pubSub.subscribe(Topics.createShaderLibraries(remoteFsSerializer)) { updated ->
        shaderLibraries.clear()
        shaderLibraries.putAll(updated)
    }

    private val searchShaderLibraries = run {
        val commands = Topics.Commands(SerializersModule {
            include(remoteFsSerializer.serialModule)
        })
        pubSub.commandSender(commands.searchShaderLibraries)
    }

    inner class Facade : baaahs.ui.Facade() {
        suspend fun searchFor(terms: String): List<ShaderLibrary.Entry> {
            return searchShaderLibraries(SearchShaderLibraries(terms)).matches
        }
    }
}