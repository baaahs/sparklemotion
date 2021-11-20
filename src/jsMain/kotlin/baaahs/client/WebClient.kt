package baaahs.client

import baaahs.*
import baaahs.app.settings.UiSettings
import baaahs.app.ui.AppIndex
import baaahs.app.ui.AppIndexProps
import baaahs.gl.Toolchain
import baaahs.io.Fs
import baaahs.io.PubSubRemoteFsClientBackend
import baaahs.libraries.ShaderLibraries
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.show.Show
import baaahs.show.live.OpenShow
import baaahs.show.mutable.EditHandler
import baaahs.show.mutable.MutableShow
import baaahs.sim.HostedWebApp
import baaahs.sm.webapi.*
import baaahs.util.UndoStack
import kotlinext.js.jsObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import react.ReactElement
import react.createElement

class WebClient(
    private val webClientLink: Network.Link,
    private val pubSub: PubSub.Client,
    private val toolchain: Toolchain,
    private val model: Model,
    private val storage: ClientStorage
) : HostedWebApp {
    private val facade = Facade()

    private val pubSubListener = { facade.notifyChanged() }.also {
        pubSub.addStateChangeListener(it)
    }

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

    private val stageManager = ClientStageManager(toolchain, pubSub, model)
    private val showEditStateChannel =
        pubSub.subscribe(
            ShowEditorState.createTopic(toolchain.plugins, remoteFsSerializer)
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

    private val serverNotices = arrayListOf<ServerNotice>()
    private val serverNoticesChannel =
        pubSub.subscribe(Topics.serverNotices) {
            serverNotices.clear()
            serverNotices.addAll(it)
            facade.notifyChanged()
        }

    var clientError: ServerNotice? = null

    private val showProblems = arrayListOf<ShowProblem>()
    init {
        pubSub.subscribe(Topics.showProblems) {
            showProblems.clear()
            showProblems.addAll(it)
            facade.notifyChanged()
        }
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
            include(toolchain.plugins.serialModule)
        })
        val newShow = pubSub.commandSender(commands.newShow)
        val switchToShow = pubSub.commandSender(commands.switchToShow)
        val saveShow = pubSub.commandSender(commands.saveShow)
        val saveAsShow = pubSub.commandSender(commands.saveAsShow)
    }

    private val shaderLibraries = ShaderLibraries(pubSub, remoteFsSerializer)

    private var uiSettings = UiSettings()

    init {
        launch {
            storage.loadSettings()?.let { updateUiSettings(it, saveToStorage = false) }
        }
    }

    private fun switchTo(showEditorState: ShowEditorState?) {
        val newShow = showEditorState?.show
        val newShowState = showEditorState?.showState
        val newIsUnsaved = showEditorState?.isUnsaved ?: false
        val newFile = showEditorState?.file
        val newOpenShow = newShow?.let { stageManager.openShow(newShow, newShowState) }
        openShow?.disuse()
        openShow = newOpenShow
        openShow?.use()

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
            this.stageManager = this@WebClient.stageManager
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

    private fun updateUiSettings(newSettings: UiSettings, saveToStorage: Boolean) {
        if (uiSettings != newSettings) {
            uiSettings = newSettings
            facade.notifyChanged()

            if (saveToStorage) {
                launch { storage.saveSettings(newSettings) }
            }
        }
    }

    inner class Facade : baaahs.ui.Facade(), EditHandler {
        val plugins: Plugins
            get() = this@WebClient.toolchain.plugins

        val toolchain: Toolchain
            get() = this@WebClient.toolchain

        val isConnected: Boolean
            get() = pubSub.isConnected

        val fsRoot: Fs.File?
            get() = this@WebClient.clientData?.fsRoot

        val isLoaded: Boolean
            get() = this@WebClient.pinkyState == PinkyState.Running && clientData != null

        val isMapping: Boolean
            get() = this@WebClient.pinkyState == PinkyState.Mapping

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

        val serverNotices : List<ServerNotice>
            get() = this@WebClient.serverNotices + listOfNotNull(this@WebClient.clientError)

        val showProblems : List<ShowProblem>
            get() = this@WebClient.showProblems

        val shaderLibraries : ShaderLibraries.Facade
            get() = this@WebClient.shaderLibraries.facade

        val uiSettings: UiSettings
            get() = this@WebClient.uiSettings

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

        private fun launchAndReportErrors(block: suspend () -> Unit) {
            launch {
                try {
                    block()
                } catch(e: Exception) {
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

        fun onNewShow(newShow: Show? = null) =
            launchAndReportErrors { serverCommands.newShow(NewShowCommand(newShow)) }

        fun onOpenShow(file: Fs.File?) =
            launchAndReportErrors { serverCommands.switchToShow(SwitchToShowCommand(file)) }

        fun onSaveShow() =
            launchAndReportErrors { serverCommands.saveShow(SaveShowCommand()) }

        fun onSaveAsShow(file: Fs.File) =
            launchAndReportErrors { serverCommands.saveAsShow(SaveAsShowCommand(file)) }

        fun onCloseShow() =
            launchAndReportErrors { serverCommands.switchToShow(SwitchToShowCommand(null)) }

        fun confirmServerNotice(id: String) {
            if (id == "_clientError_") {
                this@WebClient.clientError = null
                notifyChanged()
            } else {
                this@WebClient.confirmServerNotice(id)
            }
        }

        fun updateUiSettings(newSettings: UiSettings, saveToStorage: Boolean) {
            this@WebClient.updateUiSettings(newSettings, saveToStorage)
        }
    }

    companion object {
        private fun launch(block: suspend CoroutineScope.() -> Unit) =
            GlobalScope.launch(block = block)
    }
}