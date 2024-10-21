package baaahs.sm.server

import baaahs.PubSub
import baaahs.sm.webapi.ServerNotice
import baaahs.sm.webapi.Topics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ServerNotices(
    pubSub: PubSub.Server,
    override val coroutineContext: CoroutineContext,
) : CoroutineScope {
    private val serverNotices = arrayListOf<ServerNotice>()
    private val serverNoticesChannel = pubSub.publish(Topics.serverNotices, serverNotices) {
        launch {
            serverNotices.clear()
            serverNotices.addAll(it)
        }
    }

    fun add(title: String, message: String? = null, stackTraceToString: String? = null) {
        serverNotices.add(ServerNotice(title, message, stackTraceToString))
        serverNoticesChannel.onChange(serverNotices)
    }
}