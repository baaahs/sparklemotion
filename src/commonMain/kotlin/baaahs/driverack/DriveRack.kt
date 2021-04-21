package baaahs.driverack

import baaahs.PubSub
import baaahs.getBang
import baaahs.ui.IObservable
import baaahs.ui.Observable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlin.coroutines.CoroutineContext

class DriveRack(
    private val pubSub: PubSub.Endpoint,
    private val coroutineContext: CoroutineContext,
    initialBuses: Set<String> = emptySet(),
    initialRackMap: RackMap = RackMap.Empty,
    private val isServer: Boolean = false
) {
    private val commandChannel = pubSub.listenOnCommandChannel()

    private var layout = Layout()

    private fun buildLayout(buses: Set<String>, rackMap: RackMap, initializeFromBus: String? = null) {
        val destKeys = buses.flatMap { busId ->
            rackMap.entries.map { channel -> ChannelKey(busId, channel) }
        }.sorted()

        val removeKeys = (layout.channelKeys - destKeys).sorted()

        removeKeys.forEach { key ->
            println("Remove $key")
            layout.findChannel<Any?>(key).unsubscribe()
        }

        val newChannels = destKeys.associateWith { key ->
            val initialValue = if (initializeFromBus != null) {
                layout.findChannel<Any?>(key).value
            } else {
                key.entry.initialValue
            }

            val oldChannel = layout.findChannelOrNull<Any?>(key)
            if (oldChannel == null) {
                println("Add $key")
                BusChannel(key.busId, key.entry as RackMap.Entry<Any?>, initialValue).also { newChannel ->
                    newChannel.subscribe()
                }
            } else {
                println("Reuse $key")
                oldChannel
            }
        }

        layout = Layout(buses, rackMap, newChannels)
    }

    init {
        buildLayout(initialBuses, initialRackMap)
    }

    var rackMap: RackMap = initialRackMap
        set(value) {
            buildLayout(layout.buses.keys, value)
            field = value
        }

    fun start() {}

    fun createBus(id: String, fromBus: Bus? = null): Bus {
        val oldLayout = layout
        buildLayout(oldLayout.buses.keys + id, oldLayout.rackMap, fromBus?.id)
        return layout.buses[id]!!
    }

    fun getBus(id: String): Bus {
        return layout.buses.getBang(id, "bus")
    }

    private inner class Layout(
        buses: Set<String> = emptySet(),
        val rackMap: RackMap = RackMap.Empty,
        private val channels: Map<ChannelKey, BusChannel<*>> = emptyMap()
    ) {
        val channelKeys get() = channels.keys
        val buses = buses.associateWith { LayoutBus(it) }

        val channelEntries: Map<String, RackMap.Entry<*>> = channels.keys.associate { key ->
            key.entry.id to key.entry
        }

        fun <T> findChannel(key: ChannelKey): BusChannel<T> {
            return channels.getBang(key, "channel") as BusChannel<T>
        }

        fun <T> findChannelOrNull(key: ChannelKey): BusChannel<T>? {
            return channels[key] as BusChannel<T>?
        }

        override fun toString(): String {
            return "Layout(buses=$buses, rackMap=$rackMap)"
        }

        inner class LayoutBus(override val id: String) : Bus {
            override val driveRack: DriveRack = this@DriveRack

            override fun <T> channel(id: String): Channel<T> {
                val entry = layout.channelEntries[id]!!
                val channelKey = ChannelKey(this.id, entry)
                return layout.findChannel(channelKey)
            }
        }
    }

    inner class BusChannel<T>(
        busId: String,
        mapEntry: RackMap.Entry<T>,
        initialValue: T = mapEntry.initialValue
    ) : Channel<T>, Observable() {
        private var _value: T = initialValue

        private val topic =
            PubSub.Topic("/driverack/bus-$busId/${mapEntry.id}", mapEntry.serializer, mapEntry.serialModule)

        private var pubSubChannel: PubSub.Channel<T>? = null

        override var value: T
            get() = _value
            set(value) {
                if (_value != value) {
                    _value = value
                    pubSubChannel!!.onChange(value)
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
            pubSubChannel = pubSub.openChannel(topic, _value, this::doUpdate)
        }

        fun unsubscribe() {
            pubSubChannel!!.unsubscribe()
        }

        fun resubscribe(fromChannel: BusChannel<T>) {
            pubSubChannel = fromChannel.pubSubChannel!!
                .also {
                    it.replaceOnUpdate(this::doUpdate)
                }
        }
    }

    interface ServerApi {
        /** @return A unique client ID. */
        suspend fun subscribe(): String

        suspend fun sync(sync: Sync)

        suspend fun unsubscribe()

        companion object {
            val subscribePort = PubSub.CommandPort(
                "driverack/server/subscribe", Unit.serializer(), String.serializer()
            )

            val syncPort = PubSub.CommandPort(
                "driverack/server/sync", Sync.serializer(), Unit.serializer()
            )

            val unsubscribePort = PubSub.CommandPort(
                "driverack/server/unsubscribe", Unit.serializer(), Unit.serializer()
            )
        }
    }

    class ServerApiServerImpl(pubSub: PubSub.Server, serverApiImpl: ServerApi) : ServerApi by serverApiImpl {
        private val subscribeCommand = pubSub.listenOnCommandChannel(ServerApi.subscribePort) { subscribe() }
        private val syncCommand = pubSub.listenOnCommandChannel(ServerApi.syncPort) { sync(it) }
        private val unsubscribeCommand = pubSub.listenOnCommandChannel(ServerApi.unsubscribePort) { unsubscribe() }
    }

    class ServerApiClientImpl(pubSub: PubSub.Client) : ServerApi {
        private val subscribeCommand = pubSub.commandSender(ServerApi.subscribePort)
        override suspend fun subscribe(): String = subscribeCommand(Unit)

        private val syncCommand = pubSub.commandSender(ServerApi.syncPort)
        override suspend fun sync(sync: Sync) = syncCommand(sync)

        private val unsubscribeCommand = pubSub.commandSender(ServerApi.unsubscribePort)
        override suspend fun unsubscribe() = unsubscribeCommand(Unit)

    }

    interface ClientApi {
        suspend fun sync(sync: Sync)
    }

    @Serializable
    data class Sync(
        val originClientId: String,
        val baseChangeId: Int,
        val busChanges: List<BusSync>
    )

    @Serializable
    data class BusSync(
        val busId: String,
        val changes: Map<String, JsonElement>
    )
}

interface Bus {
    val id: String
    val driveRack: DriveRack

    fun <T> channel(id: String): Channel<T>
}

class RackMap(
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
        fun valueFrom(bus: Bus): T = bus.channel<T>(id).value
        fun setValue(bus: Bus, value: T) {
            bus.channel<T>(id).value = value
        }
    }

    companion object {
        val Empty = RackMap(emptyList())
    }
}

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
