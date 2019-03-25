package baaahs

class Ui(val network: Network, val display: UiDisplay) {
    val link = network.link()

    private lateinit var tcpConnection: Network.TcpConnection

    fun connectTo(pinkyAddress: Network.Address) {
        tcpConnection = link.connectTcp(pinkyAddress, Ports.PINKY_UI_TCP, object: Network.TcpListener {
            override fun connected(tcpConnection: Network.TcpConnection) {
                tcpConnection.send(Message(Type.UI_CLIENT_HELLO))
            }

            override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
                val message = parse(bytes)
                when (message) {
                    is PinkyState -> {
                        println("UI: primary color is ${message.primaryColor}")
                    }
                }
            }

            override fun reset(tcpConnection: Network.TcpConnection) {
                TODO("Ui.reset not implemented")
            }
        })
    }
}
