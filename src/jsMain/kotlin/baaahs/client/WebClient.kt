package baaahs.client

import baaahs.*
import baaahs.app.settings.UiSettings
import baaahs.app.ui.AppIndex
import baaahs.app.ui.dialog.FileDialog
import baaahs.client.document.SceneManager
import baaahs.client.document.ShowManager
import baaahs.gl.Toolchain
import baaahs.io.Fs
import baaahs.io.RemoteFsSerializer
import baaahs.libraries.ShaderLibraries
import baaahs.mapper.JsMapperUi
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.show.Show
import baaahs.show.live.OpenShow
import baaahs.show.mutable.EditHandler
import baaahs.show.mutable.MutableShow
import baaahs.sim.HostedWebApp
import baaahs.sm.webapi.ShowProblem
import baaahs.sm.webapi.Topics
import baaahs.util.UndoStack
import baaahs.util.globalLaunch
import kotlinext.js.jsObject
import react.ReactElement
import react.createElement

class WebClient(
    private val webClientLink: Network.Link,
    private val pubSub: PubSub.Client,
    private val toolchain: Toolchain,
    private val modelProvider: ModelProvider,
    private val storage: ClientStorage,
    private val sceneEditorClient: SceneEditorClient,
    private val mapperUi: JsMapperUi,
    remoteFsSerializer: RemoteFsSerializer,
    private val notifier: Notifier,
    private val fileDialog: FileDialog,
    private val showManager: ShowManager,
    private val sceneManager: SceneManager
) : HostedWebApp {
    private val facade = Facade()

    private val pubSubListener = { facade.notifyChanged() }.also {
        pubSub.addStateChangeListener(it)
    }

    private var openShow: OpenShow? = null

    private val clientData by pubSub.state(Topics.createClientData(remoteFsSerializer), null) {
        facade.notifyChanged()
    }

    private val stageManager = ClientStageManager(toolchain, pubSub, modelProvider)
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

    private val showProblems = arrayListOf<ShowProblem>()
    init {
        pubSub.subscribe(Topics.showProblems) {
            showProblems.clear()
            showProblems.addAll(it)
            facade.notifyChanged()
        }
    }

    private val undoStack = UndoStack<ShowEditorState>()

    private val shaderLibraries = ShaderLibraries(pubSub, remoteFsSerializer)

    private var uiSettings = UiSettings()

    init {
        globalLaunch {
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

        showManager.update(newShow, newFile, newIsUnsaved)
    }

    override fun render(): ReactElement {
        println("WebClient: my link is ${webClientLink.myAddress}")

        return createElement(AppIndex, jsObject {
            this.id = "Client Window"
            this.webClient = facade
            this.undoStack = this@WebClient.undoStack
            this.stageManager = this@WebClient.stageManager
            this.showManager = this@WebClient.showManager
            this.sceneManager = this@WebClient.sceneManager

            this.sceneEditorClient = this@WebClient.sceneEditorClient.facade
            this.mapperUi = this@WebClient.mapperUi
        })
    }

    override fun onClose() {
        showEditStateChannel.unsubscribe()
        pubSub.removeStateChangeListener(pubSubListener)
    }

    private fun updateUiSettings(newSettings: UiSettings, saveToStorage: Boolean) {
        if (uiSettings != newSettings) {
            uiSettings = newSettings
            facade.notifyChanged()

            if (saveToStorage) {
                globalLaunch { storage.saveSettings(newSettings) }
            }
        }
    }

    inner class Facade : baaahs.ui.Facade(), EditHandler {
        val fileDialog: FileDialog
            get() = this@WebClient.fileDialog

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

        val modelProvider: ModelProvider
            get() = this@WebClient.modelProvider

        val show: Show?
            get() = this@WebClient.showManager.document

        val showFile: Fs.File?
            get() = this@WebClient.showManager.file

        val showIsModified: Boolean
            get() = this@WebClient.showManager.isUnsaved

        val openShow: OpenShow?
            get() = this@WebClient.openShow

        val notifier: Notifier.Facade
            get() = this@WebClient.notifier.facade

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
            val isUnsaved = this@WebClient.showManager.isModified(show)
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

        fun updateUiSettings(newSettings: UiSettings, saveToStorage: Boolean) {
            this@WebClient.updateUiSettings(newSettings, saveToStorage)
        }
    }
}