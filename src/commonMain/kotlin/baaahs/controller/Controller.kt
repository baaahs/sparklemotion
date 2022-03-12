package baaahs.controller

import baaahs.dmx.DmxTransport
import baaahs.fixtures.*
import baaahs.model.Model
import baaahs.util.Time
import kotlinx.serialization.Serializable

/** A Controller represents a physical device directly connected to one or more fixtures. */
interface Controller {
    val controllerId: ControllerId
    val state: ControllerState
    val defaultFixtureConfig: FixtureConfig?
    val transportType: TransportType
    val defaultTransportConfig: TransportConfig?

    fun createTransport(
        entity: Model.Entity?,
        fixtureConfig: FixtureConfig,
        transportConfig: TransportConfig?,
        componentCount: Int,
        bytesPerComponent: Int
    ): Transport

    fun getAnonymousFixtureMappings(): List<FixtureMapping>

    fun beforeFrame() {}
    fun afterFrame() {}

    fun beforeFixtureResolution() {}
    fun afterFixtureResolution() {}
}

open class NullController(
    override val controllerId: ControllerId,
    override val defaultFixtureConfig: FixtureConfig? = null,
    override val defaultTransportConfig: TransportConfig? = null
) : Controller {
    override val state: ControllerState =
        State("Null Controller", "N/A", 0.0)
    override val transportType: TransportType
        get() = DmxTransport

    @Serializable
    class State(
        override val title: String,
        override val address: String?,
        override val onlineSince: Time?
    ) : ControllerState()

    override fun createTransport(
        entity: Model.Entity?,
        fixtureConfig: FixtureConfig,
        transportConfig: TransportConfig?,
        componentCount: Int,
        bytesPerComponent: Int
    ): Transport = NullTransport

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> =
        emptyList()

    companion object : NullController(ControllerId("Null", "Null"))
}