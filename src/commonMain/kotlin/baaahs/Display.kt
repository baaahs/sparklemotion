package baaahs

import baaahs.net.Network

interface Display {
    fun forNetwork(): NetworkDisplay
    fun forPinky(): PinkyDisplay
    fun forBrain(): BrainDisplay
}

interface NetworkDisplay {
    var packetLossRate: Float
    fun receivedPacket()
    fun droppedPacket()
}

interface PinkyDisplay {
    fun listShows(showMetas: List<Show>)

    var brainCount: Int
    var beat: Int
    var onShowChange: (() -> Unit)
    var selectedShow: Show?
    var nextFrameMs: Int
    var stats: Pinky.NetworkStats?
}

open class StubPinkyDisplay : PinkyDisplay {
    override fun listShows(showMetas: List<Show>) {
    }

    override var brainCount = 0
    override var beat = 0
    override var onShowChange: () -> Unit = { }
    override var selectedShow: Show? = null
    override var nextFrameMs: Int = 0
    override var stats: Pinky.NetworkStats? = null
}

interface BrainDisplay {
    var id: String?
    var surface: Surface?
    var onReset: suspend () -> Unit
    fun haveLink(link: Network.Link)
}
