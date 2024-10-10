package baaahs.sm.webapi

import baaahs.PinkyState
import baaahs.PubSub
import baaahs.controller.ControllerId
import baaahs.controller.ControllerState
import baaahs.fixtures.FixtureInfo
import baaahs.io.RemoteFsSerializer
import baaahs.libraries.ShaderLibrary
import baaahs.model.MovingHead
import baaahs.plugin.Plugins
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

object Topics {
    fun createClientData(fsSerializer: RemoteFsSerializer) =
        PubSub.Topic("clientData", ClientData.serializer().nullable, fsSerializer.serialModule)

    val serverNotices =
        PubSub.Topic("serverNotices", ListSerializer(ServerNotice.serializer()))

    val pinkyState =
        PubSub.Topic("pinkyState", PinkyState.serializer())

    fun createControllerStates(plugins: Plugins) =
        PubSub.Topic(
            "controllers", MapSerializer(ControllerId.serializer(), ControllerState.serializer()),
            plugins.serialModule
        )

    fun createFixtures(plugins: Plugins) =
        PubSub.Topic("fixtures", ListSerializer(FixtureInfo.serializer()), plugins.serialModule)

    val showProblems =
        PubSub.Topic("showProblems", ListSerializer(Problem.serializer()))

    val movingHeadPresets =
        PubSub.Topic(
            "movingHeadPresets",
            MapSerializer(String.serializer(), MovingHead.MovingHeadPosition.serializer())
        )

    fun createShaderLibraries(fsSerializer: RemoteFsSerializer) =
        PubSub.Topic(
            "shaderLibraries",
            MapSerializer(String.serializer(), ShaderLibrary.serializer()),
            fsSerializer.serialModule
        )

    val shaderLibrariesCommands by lazy { ShaderLibraryCommands.getImpl("pinky/shaderLibraries") }
}