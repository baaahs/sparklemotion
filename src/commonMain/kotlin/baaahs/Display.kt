package baaahs

import baaahs.net.Network
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Display {
    fun forNetwork(): NetworkDisplay
    fun forPinky(): PinkyDisplay
    fun forBrain(): BrainDisplay
    fun forVisualizer(): VisualizerDisplay
}

interface NetworkDisplay {
    var packetLossRate: Float
    var packetsReceived: Int
    var packetsDropped: Int
}

interface PinkyDisplay {
    var brainCount: Int
    var beat: Int
    var bpm: Float
    var beatConfidence: Float
    var selectShow: ((Show) -> Unit)
    var selectedShow: Show?
    var availableShows: List<Show>
    var showFrameMs: Int
    var stats: Pinky.NetworkStats?
    val brains: MutableMap<BrainId, BrainUiModel>
}

interface BrainUiModel {
    val brainId: String
    val surface: Surface?
    val firmwareVersion: String?

    fun reset()
}

open class StubPinkyDisplay : PinkyDisplay {
    override var brainCount = 0
    override var beat = 0
    override var bpm = 0.0f
    override var beatConfidence = 0.0f
    override var selectShow: (Show) -> Unit = { }
    override var availableShows: List<Show> = emptyList()
    override var selectedShow: Show? = null
    override var showFrameMs: Int = 0
    override var stats: Pinky.NetworkStats? = null
    override val brains: MutableMap<BrainId, BrainUiModel> = mutableMapOf()
}

interface BrainDisplay {
    var id: String?
    var surface: Surface?
    var onReset: suspend () -> Unit
    fun haveLink(link: Network.Link)
}

interface VisualizerDisplay {
    var renderMs: Int
}

open class Observable<S> {
    val map: Map<String, Any> = hashMapOf()

    private val listeners: MutableSet<() -> Unit> = hashSetOf()
    private val stateListeners: MutableSet<(Map<String, Any>) -> Unit> = hashSetOf()

    fun addListener(listener: () -> Unit) = listeners.add(listener)
    fun removeListener(listener: () -> Unit) = listeners.remove(listener)

    fun addStateListener(listener: (Map<String, Any>) -> Unit) = stateListeners.add(listener)
    fun removeStateListener(listener: (Map<String, Any>) -> Unit) = stateListeners.remove(listener)

    protected fun onChange(changes: Map<String, Any> = map) {
        listeners.forEach { it() }
        stateListeners.forEach { it(changes) }
    }

    fun <T> observer(default: T): ViewObserver<S, T> = ViewObserver(default)

    val data: MutableMap<String, Any> = hashMapOf()

    class ViewObserver<S, T>(val default: T) : ReadWriteProperty<Observable<S>, T> {
        override fun getValue(thisRef: Observable<S>, property: KProperty<*>): T {
            @Suppress("UNCHECKED_CAST")
            return thisRef.data[property.name] as T ?: default
        }

        override fun setValue(thisRef: Observable<S>, property: KProperty<*>, value: T) {
            val name = property.name
            if (thisRef.data[name] != value) {
                @Suppress("UNCHECKED_CAST")
                (thisRef.data as MutableMap<String, T>)[name] = value
                thisRef.onChange(mapOf(name to value as Any))
            }
        }
    }
}
