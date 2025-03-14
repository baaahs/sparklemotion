package baaahs.libraries

import baaahs.PubSub
import baaahs.io.RemoteFsSerializer
import baaahs.show.Tag
import baaahs.sm.webapi.Topics

interface ShaderLibraries {
    suspend fun searchFor(terms: String): List<ShaderLibrary.Entry>
    suspend fun tagList(): Set<Tag>
}

class ShaderLibrariesClient(pubSub: PubSub.Client, remoteFsSerializer: RemoteFsSerializer) {
    val facade = Facade()

    private val shaderLibraries = mutableMapOf<String, ShaderLibrary>()

    private val channel = pubSub.subscribe(Topics.createShaderLibraries(remoteFsSerializer)) { updated ->
        shaderLibraries.clear()
        shaderLibraries.putAll(updated)
    }

    private val searchShaderLibraries = Topics.shaderLibrariesCommands.createSender(pubSub)

    inner class Facade : baaahs.ui.Facade(), ShaderLibraries {
        override suspend fun searchFor(terms: String): List<ShaderLibrary.Entry> =
            searchShaderLibraries.search(terms)

        override suspend fun tagList(): Set<Tag> =
            searchShaderLibraries.tagList()
    }
}