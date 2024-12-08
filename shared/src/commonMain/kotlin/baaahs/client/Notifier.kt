package baaahs.client

import baaahs.PubSub
import baaahs.sm.webapi.ServerNotice
import baaahs.sm.webapi.Topics
import baaahs.util.Logger
import baaahs.util.globalLaunch

class Notifier(
    pubSub: PubSub.Client
) {
    val facade = Facade()

    private val serverNotices = arrayListOf<ServerNotice>()
    private val serverNoticesChannel =
        pubSub.subscribe(Topics.serverNotices) {
            serverNotices.clear()
            serverNotices.addAll(it)
            facade.notifyChanged()
        }

    var clientError: ServerNotice? = null

    private fun confirmServerNotice(id: String) {
        serverNotices.removeAll { it.id == id }
        serverNoticesChannel.onChange(serverNotices)
        facade.notifyChanged()
    }

    inner class Facade : baaahs.ui.Facade() {
        val serverNotices: List<ServerNotice>
            get() = this@Notifier.serverNotices + listOfNotNull(this@Notifier.clientError)

        fun confirmServerNotice(id: String) {
            if (id == "_clientError_") {
                this@Notifier.clientError = null
                notifyChanged()
            } else {
                this@Notifier.confirmServerNotice(id)
            }
        }

        fun launchAndReportErrors(block: suspend () -> Unit) {
            globalLaunch {
                try {
                    block()
                } catch (e: Exception) {
                    logger.error(e) { "Error in launchAndReportErrors." }
                    clientError = ServerNotice(
                        "Command Failed",
                        e.message,
                        e.stackTraceToString(),
                        "_clientError_"
                    )
                    notifyChanged()
                }
            }
        }
    }

    companion object {
        private val logger = Logger<Notifier>()
    }
}