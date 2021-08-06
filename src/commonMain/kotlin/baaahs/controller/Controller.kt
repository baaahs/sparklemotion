package baaahs.controller

import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.Transport
import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.mapper.TransportConfig
import baaahs.model.Model

interface Controller {
    val controllerId: ControllerId
    val fixtureMapping: FixtureMapping?

    fun createTransport(entity: Model.Entity?, fixtureConfig: FixtureConfig, transportConfig: TransportConfig?, pixelCount: Int): Transport
    fun getAnonymousFixtureMappings(): List<FixtureMapping>

    fun beforeFrame() {}
    fun afterFrame() {}
}