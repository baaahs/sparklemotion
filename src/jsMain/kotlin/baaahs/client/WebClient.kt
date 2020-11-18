package baaahs.client

import baaahs.*
import baaahs.app.ui.AppIndex
import baaahs.app.ui.AppIndexProps
import baaahs.gl.GlBase
import baaahs.io.Fs
import baaahs.io.PubSubRemoteFsClientBackend
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.ArgsProvider
import baaahs.plugin.PluginContext
import baaahs.plugin.PluginMode
import baaahs.plugin.Plugins
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.beatlink.BeatSource
import baaahs.proto.Ports
import baaahs.show.Show
import baaahs.show.live.OpenShow
import baaahs.show.mutable.EditHandler
import baaahs.show.mutable.MutableShow
import baaahs.sim.FakeNetwork
import baaahs.util.JsClock
import baaahs.util.UndoStack
import kotlinext.js.jsObject
import kotlinx.serialization.modules.SerializersModule
import react.ReactElement
import react.createElement

class WebClient private constructor(
    network: Network,
    pinkyAddress: Network.Address,
    private val plugins: Plugins = createPlugins()
) : HostedWebApp {
    private val facade = Facade()

    private val webClientLink = network.link("app")
    private val pubSub = PubSub.Client(webClientLink, pinkyAddress, Ports.PINKY_UI_TCP)
    private val pubSubListener = { facade.notifyChanged() }

    init {
        pubSub.addStateChangeListener(pubSubListener)
    }

    private val model = Pluggables.getModel()

    private var show: Show? = null
    private var openShow: OpenShow? = null

    private var savedShow: Show? = null
    private var showIsUnsaved: Boolean = false
    private var showFile: Fs.File? = null

    @Suppress("UNCHECKED_CAST")
    private val remoteFsSerializer = PubSubRemoteFsClientBackend(pubSub)
    private val clientData by pubSub.state(Topics.createClientData(remoteFsSerializer), null) {
        facade.notifyChanged()
    }

    private val showPlayer = ClientShowPlayer(plugins, pubSub, model)
    private val showEditStateChannel =
        pubSub.subscribe(
            ShowEditorState.createTopic(plugins, remoteFsSerializer)
        ) { incoming ->
            switchTo(incoming)
            undoStack.reset(incoming)
            facade.notifyChanged()
        }

    private var pinkyState: PinkyState? = null
    init {
        pubSub.subscribe(Topics.pinkyState) { newState ->
            pinkyState = newState
            facade.notifyChanged()
        }
    }

    private val serverNotices = arrayListOf<Pinky.ServerNotice>()
    private val serverNoticesChannel =
        pubSub.subscribe(Topics.serverNotices) {
            serverNotices.clear()
            serverNotices.addAll(it)
            facade.notifyChanged()
        }


    private val undoStack = UndoStack<ShowEditorState>()

    private val serverCommands = object {
        private var nextRequestId = 0
        private val requestCallbacks = mutableMapOf<Int, Function<*>>()

        private fun <T> getCallback(requestId: Int): (T) -> Unit =
            requestCallbacks[requestId].unsafeCast<(T) -> Unit>()

        private fun saveCallback(callback: Function<*>): Int {
            val requestId = nextRequestId++
            requestCallbacks[requestId] = callback
            return requestId
        }

        private val commands = Topics.Commands(SerializersModule {
            include(remoteFsSerializer.serialModule)
            include(plugins.serialModule)
        })
        val newShow = pubSub.commandSender(commands.newShow) {}
        val switchToShow = pubSub.commandSender(commands.switchToShow) {}
        val saveShow = pubSub.commandSender(commands.saveShow) {}
        val saveAsShow = pubSub.commandSender(commands.saveAsShow) {}
    }

    private fun switchTo(showEditorState: ShowEditorState?) {
        val newShow = showEditorState?.show
        val newShowState = showEditorState?.showState
        val newIsUnsaved = showEditorState?.isUnsaved ?: false
        val newFile = showEditorState?.file
        val newOpenShow = newShow?.let { showPlayer.openShow(newShow, newShowState) }
        openShow?.release()
        openShow = newOpenShow
        this.show = newShow
        this.showIsUnsaved = newIsUnsaved
        this.showFile = newFile
        if (!newIsUnsaved) this.savedShow = show
    }

    override fun render(): ReactElement {
        println("WebClient: my link is ${webClientLink.myAddress}")

        return createElement(AppIndex, jsObject<AppIndexProps> {
            this.id = "Client Window"
            this.webClient = facade
            this.undoStack = this@WebClient.undoStack
            this.showPlayer = this@WebClient.showPlayer
        })
    }

    override fun onClose() {
        showEditStateChannel.unsubscribe()
        pubSub.removeStateChangeListener(pubSubListener)
    }

    private fun confirmServerNotice(id: String) {
        serverNotices.removeAll { it.id == id }
        serverNoticesChannel.onChange(this@WebClient.serverNotices)
        facade.notifyChanged()
    }

    inner class Facade : baaahs.ui.Facade(), EditHandler {
        val plugins: Plugins
            get() = this@WebClient.plugins

        val isConnected: Boolean
            get() = pubSub.isConnected

        val fsRoot: Fs.File?
            get() = this@WebClient.clientData?.fsRoot

        val isLoaded: Boolean
            get() = this@WebClient.pinkyState == PinkyState.Running && clientData != null

        val model: Model
            get() = this@WebClient.model

        val show: Show?
            get() = this@WebClient.show

        val showFile: Fs.File?
            get() = this@WebClient.showFile

        val showIsModified: Boolean
            get() = this@WebClient.showIsUnsaved

        val openShow: OpenShow?
            get() = this@WebClient.openShow

        val serverNotices : List<Pinky.ServerNotice>
            get() = this@WebClient.serverNotices

        override fun onShowEdit(mutableShow: MutableShow, pushToUndoStack: Boolean) {
            onShowEdit(mutableShow.getShow(), openShow!!.getShowState(), pushToUndoStack)
        }

        override fun onShowEdit(show: Show, pushToUndoStack: Boolean) {
            onShowEdit(show, openShow!!.getShowState(), pushToUndoStack)
        }

        override fun onShowEdit(show: Show, showState: ShowState, pushToUndoStack: Boolean) {
            val isUnsaved = savedShow?.equals(show) != true
            val showEditState = show.withState(showState, isUnsaved, showFile)
            showEditStateChannel.onChange(showEditState)
            switchTo(showEditState)

            if (pushToUndoStack) {
                undoStack.changed(showEditState)
            }

            facade.notifyChanged()
        }

        fun onShowStateChange() {
            facade.notifyChanged()
        }

        fun onNewShow(newShow: Show? = null) {
            serverCommands.newShow(NewShowCommand(newShow))
        }

        fun onOpenShow(file: Fs.File?) {
            serverCommands.switchToShow(SwitchToShowCommand(file))
        }

        fun onSaveShow() {
            serverCommands.saveShow(SaveShowCommand())
        }

        fun onSaveAsShow(file: Fs.File) {
            serverCommands.saveAsShow(SaveAsShowCommand(file))
        }

        fun onCloseShow() {
            serverCommands.switchToShow(SwitchToShowCommand(null))
        }

        fun confirmServerNotice(id: String) {
            this@WebClient.confirmServerNotice(id)
        }
    }

    companion object {
        fun createPlugins(): Plugins {
            val argsProvider = ArgsProvider()
            val pluginContext = PluginContext(JsClock, PluginMode.Client)
            val plugins = Plugins.findAllBuilders()
                .map { it.createArgs(argsProvider) }
                .map { it.createPlugin(pluginContext) }
            return Plugins(pluginContext, plugins)
        }
    }
}