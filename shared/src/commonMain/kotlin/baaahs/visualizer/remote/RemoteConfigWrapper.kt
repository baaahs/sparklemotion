package baaahs.visualizer.remote

import baaahs.fixtures.FixtureConfig
import kotlinx.serialization.Serializable

@Serializable
data class RemoteConfigWrapper(val fixtureConfig: FixtureConfig)