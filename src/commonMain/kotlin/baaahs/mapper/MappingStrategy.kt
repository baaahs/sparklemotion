package baaahs.mapper

import baaahs.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.KSerializer

abstract class MappingStrategy {
    abstract val title: String

    abstract val sessionMetadataSerializer: KSerializer<out SessionMetadata>
//    abstract val entityMetadataSerializer: KSerializer<out EntityMetadata>
    abstract val pixelMetadataSerializer: KSerializer<out PixelMetadata>

    abstract fun beginSession(
        scope: CoroutineScope,
        mapper: Mapper,
        session: Mapper.Session,
        stats: MapperStats,
        ui: MapperUi,
        brainsToMap: MutableMap<Network.Address, MappableBrain>,
        mapperBackend: MapperBackend
    ): Session

    interface Session {
        suspend fun captureControllerData(mappableBrain: MappableBrain)
        suspend fun capturePixelData()
    }

    interface SessionMetadata
//    interface EntityMetadata
    interface PixelMetadata

    companion object {
        val options = listOf(
            OneAtATimeMappingStrategy,
            TwoLogNMappingStrategy
        )
    }
}