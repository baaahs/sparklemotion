package baaahs

import baaahs.net.Network

interface Display {
    fun forNetwork(): NetworkDisplay
    fun forPinky(): PinkyDisplay
    fun forBrain(): BrainDisplay
    fun forVisualizer(): VisualizerDisplay
}

interface NetworkDisplay {
    var packetLossRate: Float
    fun receivedPacket()
    fun droppedPacket()
}

interface PinkyDisplay {
    var brainCount: Int
    var beat: Int
    var bpm: Float
    var beatConfidence: Float
    var onShowChange: (() -> Unit)
    var selectedShow: Show?
    var availableShows: List<Show>
    var showFrameMs: Int
    var stats: Pinky.NetworkStats?
}

open class StubPinkyDisplay : PinkyDisplay {
    override var brainCount = 0
    override var beat = 0
    override var bpm = 0.0f
    override var beatConfidence = 0.0f
    override var onShowChange: () -> Unit = { }
    override var availableShows: List<Show> = emptyList()
    override var selectedShow: Show? = null
    override var showFrameMs: Int = 0
    override var stats: Pinky.NetworkStats? = null
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