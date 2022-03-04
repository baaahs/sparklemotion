package baaahs.controller

import baaahs.fixtures.*
import baaahs.model.Model
import baaahs.util.Time
import kotlinx.serialization.Serializable

/** A Controller represents a physical device directly connected to one or more fixtures. */
interface Controller {
    val controllerId: ControllerId
    val state: ControllerState
    val defaultFixtureConfig: FixtureConfig?

    fun createTransport(entity: Model.Entity?, fixtureConfig: FixtureConfig, transportConfig: TransportConfig?, pixelCount: Int): Transport
    fun getAnonymousFixtureMappings(): List<FixtureMapping>

    fun beforeFrame() {}
    fun afterFrame() {}
}

open class NullController(
    override val controllerId: ControllerId,
    override val defaultFixtureConfig: FixtureConfig?
) : Controller {
    override val state: ControllerState =
        State("Null Controller", "N/A", 0.0)

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
        pixelCount: Int
    ): Transport = NullTransport

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> =
        emptyList()

    companion object : NullController(ControllerId("Null", "Null"), null)
}