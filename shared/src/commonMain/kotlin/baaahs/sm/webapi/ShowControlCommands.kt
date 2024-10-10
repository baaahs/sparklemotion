package baaahs.sm.webapi

import baaahs.rpc.Service
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule

@Service
interface ShowControlCommands {
    suspend fun setGadgetState(id: String, state: Map<String, JsonElement>)

    companion object {
        val IMPL by lazy { ShowControlCommands.getImpl("pinky/showControl", SerializersModule {}) }
    }
}