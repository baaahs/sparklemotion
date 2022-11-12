package baaahs.plugin

import baaahs.PubSub
import baaahs.app.ui.dialog.DialogPanel
import baaahs.controller.ControllerManager
import baaahs.device.FixtureType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.dialect.ShaderDialect
import baaahs.gl.shader.type.ShaderType
import baaahs.net.Network
import baaahs.show.Control
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.sim.BridgeClient
import baaahs.sm.server.PinkyArgs
import baaahs.ui.Icon
import baaahs.util.Clock
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlin.reflect.KClass

interface OpenPlugin {
    val packageName: String
    val title: String

    val addControlMenuItems: List<AddControlMenuItem>
        get() = emptyList()

    val contentTypes: List<ContentType>
        get() = emptyList()

    val controlSerializers: List<SerializerRegistrar<out Control>>
        get() = emptyList()

    val controllerManagers: List<ControllerManager.Meta>
        get() = emptyList()

    val feedBuilders: List<FeedBuilder<out Feed>>
        get() = emptyList()

    val fixtureTypes: List<FixtureType>
        get() = emptyList()

    val shaderDialects: List<ShaderDialect>
        get() = emptyList()

    val shaderTypes: List<ShaderType>
        get() = emptyList()
}

interface OpenServerPlugin : OpenPlugin

interface OpenClientPlugin : OpenPlugin {
    fun getSettingsPanel(): DialogPanel? = null
}

interface OpenBridgePlugin {
    fun onConnectionOpen(tcpConnection: PubSub.Connection) = Unit
    fun onConnectionClose(tcpConnection: PubSub.Connection) = Unit

    companion object {
        internal val json = Json
    }
}

fun Network.TcpConnection.sendToClient(command: String, json: JsonElement) {
    val frame = toWsMessage(command, json)
    send(frame.encodeToByteArray())
}

internal fun toWsMessage(command: String, json: JsonElement): String {
    return OpenBridgePlugin.json.encodeToString(
        JsonElement.serializer(),
        buildJsonArray {
            add(command)
            add(json)
        })
}


interface OpenSimulatorPlugin {
    /** This plugin is used in the Simulator Bridge running on the JVM. */
    fun getBridgePlugin(pluginContext: PluginContext): OpenBridgePlugin?

    /** This plugin is used by Pinky running in the Simulator. */
    fun getServerPlugin(pluginContext: PluginContext, bridgeClient: BridgeClient): OpenServerPlugin

    /** This plugin is used on the client when running in the Simulator. */
    fun getClientPlugin(pluginContext: PluginContext): OpenClientPlugin
}

class SerializerRegistrar<T : Any>(val klass: KClass<T>, val serializer: KSerializer<T>) {
    fun register(polymorphicModuleBuilder: PolymorphicModuleBuilder<T>) {
        polymorphicModuleBuilder.subclass(klass, serializer)
    }

    override fun toString(): String = "SerializerRegistrar[${klass.simpleName} -> $serializer]"
}

@OptIn(InternalSerializationApi::class)
class ObjectSerializer<T : Any>(serialName: String, private val objectInstance: T) : KSerializer<T> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor(serialName, StructureKind.OBJECT)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.beginStructure(descriptor).endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): T {
        decoder.beginStructure(descriptor).endStructure(descriptor)
        return objectInstance
    }

    override fun toString(): String =
        "[ObjectSerializer ${descriptor.serialName} -> ${objectInstance::class.simpleName}]"
}

inline fun <reified T : Any> classSerializer(serializer: KSerializer<T>) =
    SerializerRegistrar(T::class, serializer)

inline fun <reified T : Any> objectSerializer(serialName: String, t: T) =
    classSerializer(ObjectSerializer(serialName, t))

interface Plugin {
    val id: String

    /** This plugin is used by Pinky running on JVM. */
    fun openForServer(pluginContext: PluginContext): OpenServerPlugin

    /** This plugin is used on the client when connected to Pinky running on JVM. */
    fun openForClient(pluginContext: PluginContext): OpenClientPlugin
}

/**
 * If a plugin implements [SimulatorPlugin], then the Simulator will create server and
 * client plugins via [#openForSimulator].
 */
interface SimulatorPlugin {
    fun openForSimulator(): OpenSimulatorPlugin
}

class PluginContext(
    val clock: Clock,
    val pubSub: PubSub.Endpoint
)

data class AddControlMenuItem(
    val label: String,
    val icon: Icon,
    val validForButtonGroup: Boolean = false,
    val createControlFn: (mutableShow: MutableShow) -> MutableControl
)