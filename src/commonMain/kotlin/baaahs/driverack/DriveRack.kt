package baaahs.driverack

import baaahs.PubSub
import baaahs.getBang
import baaahs.ui.IObservable
import baaahs.ui.Observable
import baaahs.util.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlin.coroutines.CoroutineContext

class DriveRackManager(
    private val pubSub: PubSub.Endpoint,
    private val coroutineContext: CoroutineContext,
    private val isServer: Boolean = false,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private var nextDriveRackId = 0

    fun start() {}

    fun publish(rackMap: RackMap, initialBuses: Set<String> = emptySet()): DriveRack {
        return DriveRack("${nextDriveRackId++}", rackMap, initialBuses, pubSub, isServer = true, ioDispatcher)
    }

    fun subscribe(id: String, rackMap: RackMap, initialBuses: Set<String> = emptySet()): DriveRack {
        return DriveRack(id, rackMap, initialBuses, pubSub, isServer = false, ioDispatcher)
    }
}


class DriveRack(
    val id: String,
    private val rackMap: RackMap,
    initialBuses: Set<String>,
    private val pubSub: PubSub.Endpoint,
    private val isServer: Boolean,
    private val ioDispatcher: CoroutineDispatcher
) {

    val buses = initialBuses.associateWith { busId -> BusImpl(busId) }.toMutableMap()

//    val serverApi = ServerApiHandler(pubSub, object : ServerApi {
//        override suspend fun subscribe(driveRackId: String): String {
//            TODO("not implemented")
//        }
//
//        override suspend fun sync(sync: Sync) {
//            TODO("not implemented")
//        }
//
//        override suspend fun unsubscribe(driveRackId: String) {
//            TODO("not implemented")
//        }
//    })

    var nextClientId = 0
    private val subscribers = mutableListOf<Subscriber>()

    init {
        if (!isServer) {
            val client = ServerApiClientImpl(pubSub as PubSub.Client)
            CoroutineScope(ioDispatcher).launch {
                client.subscribe(id)
            }
            subscribers.add(Subscriber(pubSub.connectionToServer))
        }

        pubSub.listenOnCommandChannel(ServerApi.subscribePort) {
            subscribers.add(Subscriber(this))

            // TODO: This is dumb cuz there'll be duplicates, and probly not helpful anyway.
            "client${nextClientId++}"
        }

        pubSub.listenOnCommandChannel(ServerApi.syncPort) { sync ->
            println("sync = $sync")
            for (busChange in sync.busChanges) {
                val bus = buses.getBang(busChange.busId, "bus")
                for (change in busChange.changes) {
                    val channel = bus.findChannelById<Any?>(change.channelId)
                    channel.updateJsonValue(change.value, json)
                }
            }
        }

        pubSub.listenOnCommandChannel(ServerApi.unsubscribePort) {
            subscribers.removeAll { it.connection == this }
        }
    }

    fun getBus(id: String): Bus {
        return if (isServer) {
            buses.getBang(id, "bus")
        } else {
            // TODO: Not thread-safe because buses is accessed from the network dispatcher.
            buses.getOrPut(id) { BusImpl(id) }
        }
    }

    fun pushUpdate(channel: BusImpl.BusChannel<*>) {
        val value = channel.jsonValue(json)

        val sync = Sync(
            "myClientId", listOf(
                BusSync(channel.busId, listOf(
                    Change(-1, channel.channelId, value)
                ))
            )
        )
        logger.warn { "Send sync $sync to ${subscribers.size}" }

        val scope = CoroutineScope(ioDispatcher)
        subscribers.forEach { subscriber ->
            scope.launch {
                subscriber.pushUpdate(sync)
            }
        }
    }

    class Subscriber(val connection: PubSub.Connection) {
        suspend fun pushUpdate(sync: Sync) {
            connection.sendCommand(ServerApi.syncPort, sync, null)
        }
    }

    inner class BusImpl(override val id: String) : Bus {
        override val driveRack: DriveRack = this@DriveRack
        private val channels = rackMap.entries.associate { entry -> entry.id to BusChannel(entry) }

        override fun <T> channel(entry: RackMap.Entry<T>): Channel<T> {
            return channels.getBang(entry.id, "channel") as Channel<T>
        }

        fun <T> findChannelById(id: String): BusChannel<T> {
            return channels.getBang(id, "channel") as BusChannel<T>
        }

        inner class BusChannel<T>(
            private val mapEntry: RackMap.Entry<T>,
            initialValue: T = mapEntry.initialValue
        ) : Channel<T>, Observable() {
            val channelId: String get() = mapEntry.id
            val busId: String get() = id

            private var _value: T = initialValue

//            private val topic =
//                PubSub.Topic("/driverack/bus-$busId/${mapEntry.id}", mapEntry.serializer, mapEntry.serialModule)

            override var value: T
                get() = _value
                set(value) {
                    if (_value != value) {
                        println("bus $id: ${mapEntry.id} = $value")

                        _value = value

                        pushUpdate(this)
                        notifyChanged()
                    }
                }

            private fun doUpdate(newValue: T) {
                // TODO: this needs to happen on `coroutineContext`
//            if (_value != value) {
                _value = newValue
                notifyChanged()
//            }
            }

            fun subscribe() {
//            pubSubChannel = pubSub.openChannel(topic, _value, this::doUpdate)
            }

            fun unsubscribe() {
//            pubSubChannel!!.unsubscribe()
            }

            fun resubscribe(fromChannel: BusChannel<T>) {
//            pubSubChannel = fromChannel.pubSubChannel!!
//                .also {
//                    it.replaceOnUpdate(this::doUpdate)
//                }
            }

            fun jsonValue(json: Json): JsonElement {
                return json.encodeToJsonElement(mapEntry.serializer, value)
            }

            fun updateJsonValue(jsonElement: JsonElement, json: Json) {
                doUpdate(json.decodeFromJsonElement(mapEntry.serializer, jsonElement))
            }
        }
    }

//    fun createBus(id: String, fromBus: Bus? = null): Bus {
//        val oldLayout = layout
//        buildLayout(oldLayout.buses.keys + id, oldLayout.rackMap, fromBus?.id)
//        return layout.buses[id]!!
//    }

    interface ServerApi {
        /** @return A unique client ID. */
        suspend fun subscribe(driveRackId: String): String

        suspend fun sync(sync: Sync)

        suspend fun unsubscribe(driveRackId: String)

        companion object {
            val subscribePort = PubSub.CommandPort(
                "driverack/server/subscribe", String.serializer(), String.serializer()
            )

            val syncPort = PubSub.CommandPort(
                "driverack/server/sync", Sync.serializer(), Unit.serializer()
            )

            val unsubscribePort = PubSub.CommandPort(
                "driverack/server/unsubscribe", String.serializer(), Unit.serializer()
            )
        }
    }

    class ServerApiHandler(pubSub: PubSub.Endpoint, serverApiImpl: ServerApi) : ServerApi by serverApiImpl {
        private val subscribeCommand = pubSub.listenOnCommandChannel(ServerApi.subscribePort) { subscribe(it) }
        private val syncCommand = pubSub.listenOnCommandChannel(ServerApi.syncPort) { sync(it) }
        private val unsubscribeCommand = pubSub.listenOnCommandChannel(ServerApi.unsubscribePort) { unsubscribe(it) }
    }

    class ServerApiClientImpl(pubSub: PubSub.Client) : ServerApi {
        private val subscribeCommand = pubSub.commandSender(ServerApi.subscribePort)
        override suspend fun subscribe(driveRackId: String): String = subscribeCommand(driveRackId)

        private val syncCommand = pubSub.commandSender(ServerApi.syncPort)
        override suspend fun sync(sync: Sync) = syncCommand(sync)

        private val unsubscribeCommand = pubSub.commandSender(ServerApi.unsubscribePort)
        override suspend fun unsubscribe(driveRackId: String) = unsubscribeCommand(driveRackId)

    }

    interface ClientApi {
        suspend fun sync(sync: Sync)
    }

    @Serializable
    data class Sync(
        val originClientId: String,
        val busChanges: List<BusSync>
    )

    @Serializable
    data class BusSync(
        val busId: String,
        val changes: List<Change>
    )

    @Serializable
    data class Change(
        val baseChangeId: Int,
        val channelId: String,
        val value: JsonElement
    )

    companion object {
        val json = Json {}
        private val logger = Logger<DriveRack>()
    }
}

interface Bus {
    val id: String
    val driveRack: DriveRack

    fun <T> channel(entry: RackMap.Entry<T>): Channel<T>
}

data class RackMap(
    val entries: List<Entry<*>>
) {
    constructor(vararg entries: Entry<*>) : this(entries.toList())


    override fun toString(): String {
        return "RackMap(entries=${entries.map { it.id }})"
    }

    data class Entry<T>(
        val id: String,
        val initialValue: T,
        val serializer: KSerializer<T>,
        val serialModule: SerializersModule = SerializersModule { }
    ) {
        fun valueFrom(bus: Bus): T = bus.channel<T>(this).value
        fun setValue(bus: Bus, value: T) {
            bus.channel(this).value = value
        }
    }

    companion object {
        val Empty = RackMap(emptyList())
    }
}

class Harness

interface Channel<T> : IObservable {
    var value: T
}

private data class ChannelKey(
    val busId: String,
    val entry: RackMap.Entry<*>
) : Comparable<ChannelKey> {
    override fun compareTo(other: ChannelKey): Int {
        return busId.compareTo(other.busId).let {
            if (it == 0) {
                entry.id.compareTo(other.entry.id)
            } else it
        }
    }
}
