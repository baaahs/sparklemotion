package baaahs.sm.webapi

import baaahs.PinkyState
import baaahs.PubSub
import baaahs.controller.ControllerId
import baaahs.controller.ControllerState
import baaahs.fixtures.FixtureInfo
import baaahs.gl.Display
import baaahs.io.RemoteFsSerializer
import baaahs.libraries.ShaderLibrary
import baaahs.model.MovingHead
import baaahs.plugin.Plugins
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule

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

    val displays =
        PubSub.Topic("displays", ListSerializer(Display.serializer()))

    fun createShaderLibraries(fsSerializer: RemoteFsSerializer) =
        PubSub.Topic(
            "shaderLibraries",
            MapSerializer(String.serializer(), ShaderLibrary.serializer()),
            fsSerializer.serialModule
        )

    class Commands(serialModule: SerializersModule) {
        val searchShaderLibraries = PubSub.CommandPort(
            "pinky/shaderLibraries/search",
            SearchShaderLibraries.serializer(), SearchShaderLibraries.Response.serializer(), serialModule
        )
    }

    class DocumentCommands<T>(documentType: String, tSerializer: KSerializer<T>, serialModule: SerializersModule) {
        val newCommand = PubSub.CommandPort(
            "pinky/$documentType/new", NewCommand.serializer(tSerializer), Unit.serializer(), serialModule
        )
        val switchToCommand = PubSub.CommandPort(
            "pinky/$documentType/switchTo", SwitchToCommand.serializer(), Unit.serializer(), serialModule
        )
        val saveCommand = PubSub.CommandPort(
            "pinky/$documentType/save", SaveCommand.serializer(), Unit.serializer(), serialModule
        )
        val saveAsCommand = PubSub.CommandPort(
            "pinky/$documentType/saveAs", SaveAsCommand.serializer(), Unit.serializer(), serialModule
        )
    }
}