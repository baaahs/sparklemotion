package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.jvm.Synchronized

public class Central(val network: Network) : Network.Listener {
    private lateinit var link: Network.Link
    private val controllers: MutableMap<Network.Address, RemoteController> = mutableMapOf()

    fun run() {
        link = network.link(this)
    }

    fun start() {
        GlobalScope.launch { run() }
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        when (parse(bytes)) {
            is HelloMessage -> {
                println("central: hello from ${fromAddress}!")
                foundController(RemoteController(fromAddress))
            }

        }

    }

    @Synchronized private fun foundController(remoteController: RemoteController) {
        controllers.put(remoteController.fromAddress, remoteController)
    }
}

class RemoteController(val fromAddress: Network.Address) {
}