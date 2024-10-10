package baaahs.dmx

import baaahs.rpc.Service
import kotlinx.serialization.modules.SerializersModule

@Service
interface DmxCommands {
    suspend fun listDmxUniverses(): Map<String, DmxUniverseListener.LastFrame>

    companion object {
        val IMPL by lazy { DmxCommands.getImpl("pinky/dmx/universes", SerializersModule {}) }
    }
}