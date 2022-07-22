package baaahs.mapper

import baaahs.net.Network

abstract class MappingStrategy {
    abstract val title: String

    abstract suspend fun capturePixelData(
        mapper: Mapper,
        stats: MapperStats,
        ui: MapperUi,
        session: Mapper.Session,
        brainsToMap: MutableMap<Network.Address, MappableBrain>,
        mapperBackend: MapperBackend
    )

    companion object {
        val options = listOf(
            OneAtATimeMappingStrategy,
            TwoLogNMappingStrategy
        )
    }
}