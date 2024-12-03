package baaahs.controller

import baaahs.dmx.DmxTransportType
import baaahs.fixtures.*
import baaahs.model.Model
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/** A Controller represents a physical device directly connected to one or more fixtures. */
interface Controller {
    val controllerId: ControllerId
    val defaultFixtureOptions: FixtureOptions?
    val transportType: TransportType
    val defaultTransportConfig: TransportConfig?

    /**
     * Retrieves a list of fixture mappings that do not have associated names.
     * This could be useful for fixtures that are automatically discovered or 
     * do not need explicit naming.
     *
     * @return A list of `FixtureMapping` instances without names.
     */
    fun getAnonymousFixtureMappings(): List<FixtureMapping>

    
    /** Called before each frame is rendered. */
    fun beforeFrame() {}

    /** Called after each frame has been rendered and [baaahs.gl.render.RenderTarget.sendFrame] has been called. */
    fun afterFrame() {}
    
    /**
     * Creates a [FixtureResolver] that is responsible for constructing transport instances
     * necessary to communicate with all the fixtures associated with this controller.
     *
     * A single fixture resolver will be used to resolve all fixtures associated with this
     * controller. That might be useful if, e.g., fixtures are allocated to sequential DMX
     * channels.
     *
     * @return A new instance of `FixtureResolver`.
     */
    fun createFixtureResolver(): FixtureResolver

    
    /**
     * Releases any resources associated with this controller
     * and performs any necessary cleanup operations.
     * 
     * Called by [ControllersManager] when [ControllerManager.onChange] for this controller returns null.
     */
    fun release() {}
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
    override val transportType: TransportType
        get() = DmxTransportType

    @Serializable
    class NullState(
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