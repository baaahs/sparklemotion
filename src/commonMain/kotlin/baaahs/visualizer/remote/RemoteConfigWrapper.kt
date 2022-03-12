package baaahs.visualizer.remote

import baaahs.fixtures.RemoteConfig
import kotlinx.serialization.Serializable

@Serializable
data class RemoteConfigWrapper(val remoteConfig: RemoteConfig)