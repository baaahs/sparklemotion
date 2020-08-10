package baaahs.client

import baaahs.*
import baaahs.app.ui.AppIndex
import baaahs.app.ui.AppIndexProps
import baaahs.gl.GlBase
import baaahs.io.Fs
import baaahs.io.PubSubRemoteFsClientBackend
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.proto.Ports
import baaahs.show.Show
import baaahs.show.live.OpenShow
import baaahs.util.UndoStack
import kotlinext.js.jsObject
import kotlinx.serialization.modules.SerializersModule
import react.ReactElement
import react.createElement

class WebClient(
    network: Network,
    pinkyAddress: Network.Address,
    private val plugins: Plugins = Plugins.findAll()
) : HostedWebApp {
    private val facade = Facade()

    private val webClientLink = network.link("app")
    private val pubSub = PubSub.Client(webClientLink, pinkyAddress, Ports.PINKY_UI_TCP)
    private val pubSubListener = { facade.notifyChanged() }

    init {
        pubSub.addStateChangeListener(pubSubListener)
    }

    private val glslContext = GlBase.jsManager.createContext()
    private val model = Pluggables.getModel()

    private var show: Show? = null
    private var openShow: OpenShow? = null
    private var showState: ShowState? = null

    private var savedShow: Show? = null
    private var showIsUnsaved: Boolean = false
    private var showFile: Fs.File? = null

    @Suppress("UNCHECKED_CAST")
    private val remoteFsSerializer = PubSubRemoteFsClientBackend(pubSub)
    private val clientData by pubSub.state(Topics.createClientData(remoteFsSerializer), null) {
        facade.notifyChanged()
    }

    private val showPlayer = ClientShowPlayer(plugins, glslContext, pubSub, model)
    private var showStateSynced = false
    private val showEditStateChannel =
        pubSub.subscribe(
            ShowEditorState.createTopic(plugins, remoteFsSerializer)
        ) { incoming ->
            switchTo(incoming)
            undoStack.reset(incoming)
            showStateSynced = true
            facade.notifyChanged()
        }
    private val showStateChannel =
        pubSub.subscribe(Topics.showState) {
            showState = it
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
        val newOpenShow = newShow?.let { showPlayer.openShow(newShow) }
        openShow?.release()
        openShow = newOpenShow
        this.show = newShow
        this.showState = newShowState
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
        showStateChannel.unsubscribe()
        showEditStateChannel.unsubscribe()
        pubSub.removeStateChangeListener(pubSubListener)
    }

    inner class Facade : baaahs.ui.Facade() {
        val plugins: Plugins
            get() = this@WebClient.plugins

        val isConnected: Boolean
            get() = pubSub.isConnected

        val fsRoot: Fs.File?
            get() = this@WebClient.clientData?.fsRoot

        val isLoaded: Boolean
            get() = this@WebClient.showStateSynced && clientData != null

        val show: Show?
            get() = this@WebClient.show

        val showFile: Fs.File?
            get() = this@WebClient.showFile

        val showIsModified: Boolean
            get() = this@WebClient.showIsUnsaved

        val showState: ShowState?
            get() = this@WebClient.showState

        val openShow: OpenShow?
            get() = this@WebClient.openShow

        fun onShowEdit(show: Show, showState: ShowState): ShowEditorState {
            val isUnsaved = savedShow?.equals(show) != true
            val showEditState = show.withState(showState, isUnsaved, showFile)
            showEditStateChannel.onChange(showEditState)
            switchTo(showEditState)
            facade.notifyChanged()
            return showEditState
        }

        fun onShowStateChange(showState: ShowState) {
            showStateChannel.onChange(showState)
            this@WebClient.showState = showState
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
    }
}
