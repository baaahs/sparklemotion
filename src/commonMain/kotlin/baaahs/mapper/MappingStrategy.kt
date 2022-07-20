package baaahs.mapper

import baaahs.net.Network

abstract class MappingStrategy {
    abstract suspend fun capturePixelData(
        mapper: Mapper,
        stats: MapperStats,
        ui: MapperUi,
        session: Mapper.Session,
        brainsToMap: MutableMap<Network.Address, Mapper.MappableBrain>
    )
}