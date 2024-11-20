package baaahs.fixtures

import baaahs.PubSub
import baaahs.controller.ControllerId
import baaahs.plugin.Plugins
import baaahs.sm.webapi.Topics
import kotlinx.serialization.Serializable

class FixturePublisher(
    pubSub: PubSub.Server,
    plugins: Plugins
) : FixtureListener {
    private val fixturesChannel = pubSub.openChannel(Topics.createFixtures(plugins), emptyList()) {}
    private val fixtures = mutableMapOf<Fixture, FixtureInfo>()

    override fun fixturesChanged(addedFixtures: Collection<Fixture>, removedFixtures: Collection<Fixture>) {
        removedFixtures.forEach { fixtures.remove(it) }
        addedFixtures.forEach {
            fixtures[it] = FixtureInfo(
                it.name,
                it.modelEntity?.name, // TODO: Name isn't a unique selector for entities, fix.
                it.transport.controller.controllerId,
                it.transport.config
            )
        }
        fixturesChannel.onChange(fixtures.values.toList())
    }
}

@Serializable
data class FixtureInfo(
    val name: String,
    val entityName: String?,
    val controllerId: ControllerId,
    val transportConfig: TransportConfig?
) {
    fun matches(search: String): Boolean {
        val s = search.lowercase()
        return name.lowercase().contains(s) ||
                entityName?.lowercase()?.contains(s) == true
    }
}