package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.jvm.Synchronized

class Central(val network: Network, val display: CentralDisplay) : Network.Listener {
    private lateinit var link: Network.Link
    private val controllers: MutableMap<Network.Address, RemoteController> = mutableMapOf()

    fun run() {
        link = network.link()
        link.listen(Ports.CENTRAL, this)
    }

    fun start() {
        GlobalScope.launch { run() }
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        when (parse(bytes)) {
            is ControllerHelloMessage -> {
                foundController(RemoteController(fromAddress))
            }

            is MapperHelloMessage -> {
                link.send(
                    fromAddress,
                    Ports.MAPPER,
                    CentralPongMessage(controllers.values.map { it.fromAddress.toString() }).toBytes()
                )
            }
        }

    }

    @Synchronized
    private fun foundController(remoteController: RemoteController) {
        controllers.put(remoteController.fromAddress, remoteController)
        display.controllerCount = controllers.size
    }
}

class RemoteController(val fromAddress: Network.Address) {
}