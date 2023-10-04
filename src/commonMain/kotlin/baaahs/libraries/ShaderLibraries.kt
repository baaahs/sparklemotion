package baaahs.libraries

import baaahs.PubSub
import baaahs.io.RemoteFsSerializer
import baaahs.sm.webapi.SearchShaderLibraries
import baaahs.sm.webapi.Topics
import kotlinx.serialization.modules.SerializersModule

interface ShaderLibraries {
    suspend fun searchFor(terms: String): List<ShaderLibrary.Entry>
}

class ShaderLibrariesClient(pubSub: PubSub.Client, remoteFsSerializer: RemoteFsSerializer) {
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

    inner class Facade : baaahs.ui.Facade(), ShaderLibraries {
        override suspend fun searchFor(terms: String): List<ShaderLibrary.Entry> {
            return searchShaderLibraries(SearchShaderLibraries(terms)).matches
        }
    }
}