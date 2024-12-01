package baaahs.controller

import baaahs.dmx.DmxTransportType
import baaahs.fixtures.*
import baaahs.model.Model
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/** A Controller represents a physical device directly connected to one or more fixtures. */
interface Controller {
    val controllerId: ControllerId
    val state: ControllerState
    val defaultFixtureOptions: FixtureOptions?
    val transportType: TransportType
    val defaultTransportConfig: TransportConfig?

    fun getAnonymousFixtureMappings(): List<FixtureMapping>

    fun beforeFrame() {}
    fun afterFrame() {}

    fun createFixtureResolver(): FixtureResolver
}

interface FixtureResolver {
    fun createTransport(
        entity: Model.Entity?,
        fixtureConfig: FixtureConfig,
        transportConfig: TransportConfig?
    ): Transport
}

open class NullController(
    override val controllerId: ControllerId,
    override val defaultFixtureOptions: FixtureOptions? = null,
    override val defaultTransportConfig: TransportConfig? = null
) : Controller {
    override val state: ControllerState =
        State("Null Controller", "N/A", null)
    override val transportType: TransportType
        get() = DmxTransportType

    @Serializable
    class State(
        override val title: String,
        override val address: String?,
        override val onlineSince: Instant?,
        override val firmwareVersion: String? = null,
        override val lastErrorMessage: String? = null,
        override val lastErrorAt: Instant? = null
    ) : ControllerState()

    override fun createFixtureResolver(): FixtureResolver = object : FixtureResolver {
        override fun createTransport(
            entity: Model.Entity?,
            fixtureConfig: FixtureConfig,
            transportConfig: TransportConfig?
        ): Transport = NullTransport
    }

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> =
        emptyList()

    companion object : NullController(ControllerId("Null", "Null"))
}