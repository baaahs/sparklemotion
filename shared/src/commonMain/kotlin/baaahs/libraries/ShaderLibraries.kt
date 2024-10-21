package baaahs.libraries

import baaahs.PubSub
import baaahs.io.RemoteFsSerializer
import baaahs.sm.webapi.Topics

class ShaderLibraries(pubSub: PubSub.Client, remoteFsSerializer: RemoteFsSerializer) {
    val facade = Facade()

    private val shaderLibraries = mutableMapOf<String, ShaderLibrary>()

    private val channel = pubSub.subscribe(Topics.createShaderLibraries(remoteFsSerializer)) { updated ->
        shaderLibraries.clear()
        shaderLibraries.putAll(updated)
    }

    private val searchShaderLibraries = Topics.shaderLibrariesCommands.createSender(pubSub)

    inner class Facade : baaahs.ui.Facade() {
        suspend fun searchFor(terms: String): List<ShaderLibrary.Entry> {
            return searchShaderLibraries.search(terms)
        }
    }
}