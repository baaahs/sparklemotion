package baaahs.ui.components

import baaahs.*
import baaahs.net.Network
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import react.RState
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class RVisualizerDisplay: VisualizerDisplay, Observable<RVisualizerDisplay>() {
    override var renderMs: Int = 0
        set(value) { field = value; onChange() }
}

class RNetworkDisplay: NetworkDisplay, Observable<RNetworkDisplay>(), RState {
    override var packetLossRate: Float by observer(0f)
    override var packetsReceived: Int by observer(0)
    override var packetsDropped: Int by observer(0)
}

class XNetworkDisplay: NetworkDisplay, Observable<XNetworkDisplay>() {
    override var packetLossRate: Float = 0f
        set(value) { field = value; onChange() }
    override var packetsReceived: Int = 0
        set(value) { field = value; onChange() }
    override var packetsDropped: Int = 0
        set(value) { field = value; onChange() }
}

class RPinkyDisplay: PinkyDisplay, Observable<RPinkyDisplay>() {
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

class RBrainDisplay: BrainDisplay, Observable<RBrainDisplay>() {
    override var id: String? = null
        set(value) { field = value; onChange() }
    override var surface: Surface? = null
        set(value) { field = value; onChange() }
    override var onReset: suspend () -> Unit = {}
        set(value) { field = value; onChange() }

    override fun haveLink(link: Network.Link) {
    }
}

class ObservableList<T>(private val l: MutableList<T>) : MutableList<T> by l, Observable<ObservableList<T>>() {
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

class ObservableMap<K,V>(private val m: MutableMap<K,V>) : MutableMap<K,V> by m, Observable<ObservableMap<K,V>>() {
    override fun clear() = m.clear().also { onChange() }
    override fun put(key: K, value: V): V? = m.put(key, value).also { onChange() }
    override fun putAll(from: Map<out K, V>) = m.putAll(from).also { onChange() }
    override fun remove(key: K): V? = m.remove(key).also { onChange() }
}

private val jsonParser = Json(JsonConfiguration.Stable)
