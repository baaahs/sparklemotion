package baaahs

expect fun getDisplay(): Display

interface Display {
    fun forNetwork(): NetworkDisplay
    fun forCentral(): CentralDisplay
    fun forController(): ControllerDisplay
    fun forMapper(): MapperDisplay
}

interface NetworkDisplay {
    fun receivedPacket()
    fun droppedPacket()
}

interface CentralDisplay {
    var controllerCount: Int
}

interface ControllerDisplay {
    fun haveLink(link: Network.Link)
}

interface MapperDisplay
