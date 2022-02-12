package baaahs.controller

import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.NullTransport
import baaahs.fixtures.Transport
import baaahs.mapper.FixtureMapping
import baaahs.mapper.TransportConfig
import baaahs.model.Model

/** A Controller represents a physical device directly connected to one or more fixtures. */
interface Controller {
    val controllerId: ControllerId
    val fixtureMapping: FixtureMapping?

    fun createTransport(entity: Model.Entity?, fixtureConfig: FixtureConfig, transportConfig: TransportConfig?, pixelCount: Int): Transport
    fun getAnonymousFixtureMappings(): List<FixtureMapping>

    fun beforeFrame() {}
    fun afterFrame() {}
}

open class NullController(
    override val controllerId: ControllerId,
    override val fixtureMapping: FixtureMapping?
) : Controller {
    override fun createTransport(
        entity: Model.Entity?,
        fixtureConfig: FixtureConfig,
        transportConfig: TransportConfig?,
        pixelCount: Int
    ): Transport = NullTransport

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> =
        emptyList()

    companion object : NullController(ControllerId("Null", "Null"), null)
}