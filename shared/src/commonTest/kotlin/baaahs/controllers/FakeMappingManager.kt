package baaahs.controllers

import baaahs.controller.ControllerId
import baaahs.fixtures.FixtureMapping
import baaahs.mapping.MappingManager
import baaahs.ui.Observable

class FakeMappingManager(
    data: Map<ControllerId, List<FixtureMapping>> = mutableMapOf(),
    dataHasLoaded: Boolean = true
) : Observable(), MappingManager {
    val data = data.toMutableMap()
    override var dataHasLoaded: Boolean = dataHasLoaded
        set(value) {
            field = value
            notifyChanged()
        }

    override suspend fun start(): Unit = TODO("not implemented")

    override fun findMappings(controllerId: ControllerId): List<FixtureMapping> {
        return data[controllerId] ?: emptyList()
    }

    override fun getAllControllerMappings(): Map<ControllerId, List<FixtureMapping>> {
        TODO("not implemented")
    }
}