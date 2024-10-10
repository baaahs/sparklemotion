package baaahs.sm.webapi

import baaahs.libraries.ShaderLibrary
import baaahs.rpc.Service

@Service
interface ShaderLibraryCommands {
    suspend fun search(terms: String): List<ShaderLibrary.Entry>

    companion object
}