package baaahs.ui.components

import baaahs.*
import baaahs.net.Network
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class RVisualizerDisplay: VisualizerDisplay, Observable() {
    override var renderMs: Int = 0
        set(value) { field = value; onChange() }
}

class ViewObserver<T>(
    val name: String,
    val initialValue: T,
    private val serializer: KSerializer<T>,
    val data: MutableMap<String, JsonElement>,
    val onChange: () -> Unit
) : ReadWriteProperty<Gadget, T> {
    override fun getValue(thisRef: Gadget, property: KProperty<*>): T {
        val value = data[name]
        return if (value == null) initialValue else {
            jsonParser.fromJson(serializer, value)
        }
    }

    override fun setValue(thisRef: Gadget, property: KProperty<*>, value: T) {
        if (getValue(thisRef, property) != value) {
            data[name] = jsonParser.toJson(serializer, value)
            onChange()
        }
    }
}

class XNetworkDisplay(val map: Map<String, Any?>): NetworkDisplay by ViewObserver

class RNetworkDisplay: NetworkDisplay, Observable() {
    override var packetLossRate: Float = 0f
        set(value) { field = value; onChange() }
    override var packetsReceived: Int = 0
        set(value) { field = value; onChange() }
    override var packetsDropped: Int = 0
        set(value) { field = value; onChange() }
}

class RPinkyDisplay: PinkyDisplay, Observable() {
    override var brainCount: Int = 0
        set(value) { field = value; onChange() }
    override var beat: Int = 0
        set(value) { field = value; onChange() }
    override var bpm: Float = 0f
        set(value) { field = value; onChange() }
    override var beatConfidence: Float = 0f
        set(value) { field = value; onChange() }
    override var selectShow: (Show) -> Unit = {}
        set(value) { field = value; onChange() }
    override var selectedShow: Show? = null
        set(value) { field = value; onChange() }
    override var availableShows: List<Show> = emptyList()
        set(value) { field = value; onChange() }
    override var showFrameMs: Int = 0
        set(value) { field = value; onChange() }
    override var stats: Pinky.NetworkStats? = null
        set(value) { field = value; onChange() }
    override val brains = ObservableMap<BrainId, BrainUiModel>(mutableMapOf())
}

class RBrainDisplay: BrainDisplay, Observable() {
    override var id: String? = null
        set(value) { field = value; onChange() }
    override var surface: Surface? = null
        set(value) { field = value; onChange() }
    override var onReset: suspend () -> Unit = {}
        set(value) { field = value; onChange() }

    override fun haveLink(link: Network.Link) {
    }
}

class ObservableList<T>(private val l: MutableList<T>) : MutableList<T> by l, Observable() {
    override fun add(element: T): Boolean = l.add(element).also { onChange() }
    override fun add(index: Int, element: T) = l.add(index, element).also { onChange() }
    override fun addAll(index: Int, elements: Collection<T>): Boolean =
        l.addAll(index, elements).also { onChange() }
    override fun addAll(elements: Collection<T>): Boolean = l.addAll(elements).also { onChange() }
    override fun clear() = l.clear().also { onChange() }
    override fun remove(element: T): Boolean = remove(element).also { onChange() }
    override fun removeAll(elements: Collection<T>): Boolean = removeAll(elements).also { onChange() }
    override fun removeAt(index: Int): T = removeAt(index).also { onChange() }
    override fun retainAll(elements: Collection<T>): Boolean = retainAll(elements).also { onChange() }
    override fun set(index: Int, element: T): T = set(index, element).also { onChange() }
}

class ObservableMap<K,V>(private val m: MutableMap<K,V>) : MutableMap<K,V> by m, Observable() {
    override fun clear() = m.clear().also { onChange() }
    override fun put(key: K, value: V): V? = m.put(key, value).also { onChange() }
    override fun putAll(from: Map<out K, V>) = m.putAll(from).also { onChange() }
    override fun remove(key: K): V? = m.remove(key).also { onChange() }
}

