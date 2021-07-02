package baaahs

import baaahs.controller.WledDevice
import baaahs.dmx.DmxInfo
import baaahs.io.RemoteFsSerializer
import baaahs.libraries.ShaderLibrary
import baaahs.model.MovingHead
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule

object Topics {
    fun createClientData(fsSerializer: RemoteFsSerializer) =
        PubSub.Topic("clientData", ClientData.serializer().nullable, fsSerializer.serialModule)

    val serverNotices =
        PubSub.Topic("serverNotices", ListSerializer(Pinky.ServerNotice.serializer()))

    val pinkyState =
        PubSub.Topic("pinkyState", PinkyState.serializer())

    val brains =
        PubSub.Topic("brains", MapSerializer(String.serializer(), BrainInfo.serializer()))

    val dmxDevices =
        PubSub.Topic("dmx/devices", MapSerializer(String.serializer(), DmxInfo.serializer()))

    val sacnDevices =
        PubSub.Topic("sacn/devices", MapSerializer(String.serializer(), WledDevice.serializer()))

    val showProblems =
        PubSub.Topic("showProblems", ListSerializer(ShowProblem.serializer()))

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

    class Commands(serialModule: SerializersModule) {
        val newShow = PubSub.CommandPort(
            "pinky/newShow", NewShowCommand.serializer(), Unit.serializer(), serialModule
        )
        val switchToShow = PubSub.CommandPort(
            "pinky/switchToShow", SwitchToShowCommand.serializer(), Unit.serializer(), serialModule
        )
        val saveShow = PubSub.CommandPort(
            "pinky/saveShow", SaveShowCommand.serializer(), Unit.serializer(), serialModule
        )
        val saveAsShow = PubSub.CommandPort(
            "pinky/saveAsShow", SaveAsShowCommand.serializer(), Unit.serializer(), serialModule
        )
        val searchShaderLibraries = PubSub.CommandPort(
            "pinky/shaderLibraries/search",
            SearchShaderLibraries.serializer(), SearchShaderLibraries.Response.serializer(), serialModule
        )
    }
}