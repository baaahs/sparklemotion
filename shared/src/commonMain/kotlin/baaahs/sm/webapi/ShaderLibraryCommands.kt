package baaahs.sm.webapi

import baaahs.libraries.ShaderLibrary
import baaahs.rpc.Service
import baaahs.show.Tag

@Service
interface ShaderLibraryCommands {
    suspend fun search(terms: String): List<ShaderLibrary.Entry>
    suspend fun tagList(): Set<Tag>

    companion object
}