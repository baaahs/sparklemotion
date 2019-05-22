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
    fun listShows(showMetas: List<Show.MetaData>)

    var brainCount: Int
    var beat: Int
    var onShowChange: (() -> Unit)
    var selectedShow: Show.MetaData?
    var nextFrameMs: Int
    var stats: ShowRunner.Stats?
}

open class StubPinkyDisplay : PinkyDisplay {
    override fun listShows(showMetas: List<Show.MetaData>) {
    }

    override var brainCount = 0
    override var beat = 0
    override var onShowChange: () -> Unit = { }
    override var selectedShow: Show.MetaData? = null
    override var nextFrameMs: Int = 0
    override var stats: ShowRunner.Stats? = null
}

interface BrainDisplay {
    fun haveLink(link: Network.Link)
}
