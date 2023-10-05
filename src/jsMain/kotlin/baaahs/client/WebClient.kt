package baaahs.client

import baaahs.PinkyState
import baaahs.PubSub
import baaahs.app.settings.UiSettings
import baaahs.app.ui.AppIndex
import baaahs.app.ui.dialog.FileDialog
import baaahs.client.document.SceneManager
import baaahs.client.document.ShowManager
import baaahs.dmx.DmxManagerImpl
import baaahs.dmx.ListDmxUniverses
import baaahs.gl.Displays
import baaahs.gl.Toolchain
import baaahs.io.Fs
import baaahs.io.RemoteFsSerializer
import baaahs.libraries.ShaderLibraries
import baaahs.mapper.JsMapper
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.scene.SceneProvider
import baaahs.sim.HostedWebApp
import baaahs.sm.webapi.Topics
import baaahs.util.globalLaunch
import js.core.jso
import react.ReactElement
import react.createElement

class WebClient(
    private val webClientLink: Network.Link,
    private val pubSub: PubSub.Client,
    private val toolchain: Toolchain,
    private val sceneProvider: SceneProvider,
    private val storage: ClientStorage,
    private val sceneEditorClient: SceneEditorClient,
    private val mapper: JsMapper,
    remoteFsSerializer: RemoteFsSerializer,
    private val notifier: Notifier,
    private val fileDialog: FileDialog,
    private val showManager: ShowManager,
    private val sceneManager: SceneManager,
    private val stageManager: ClientStageManager
) : HostedWebApp {
    private val facade = Facade()

    private val pubSubListener = { facade.notifyChanged() }.also {
        pubSub.addStateChangeListener(it)
    }

    private val clientData by pubSub.state(Topics.createClientData(remoteFsSerializer), null) {
        facade.notifyChanged()
    }

    private var pinkyState: PinkyState? = null
    init {
        pubSub.subscribe(Topics.pinkyState) { newState ->
            pinkyState = newState
            facade.notifyChanged()
        }
    }

    private val displays = Displays().also { it.Channel(pubSub) }

    private val shaderLibraries = ShaderLibraries(pubSub, remoteFsSerializer)

    private val listDmxUniverses = pubSub.commandSender(
        DmxManagerImpl.createCommandPort(toolchain.plugins.serialModule))

    private var uiSettings = UiSettings()

    init {
        globalLaunch {
            storage.loadSettings()?.let { updateUiSettings(it, saveToStorage = false) }
        }
    }

    override fun render(): ReactElement<*> {
        println("WebClient: my link is ${webClientLink.myAddress}")

        return createElement(AppIndex, jso {
            this.id = "Client Window"
            this.webClient = facade
            this.stageManager = this@WebClient.stageManager
            this.showManager = this@WebClient.showManager.facade
            this.sceneManager = this@WebClient.sceneManager.facade

            this.sceneEditorClient = this@WebClient.sceneEditorClient.facade
            this.mapper = this@WebClient.mapper
        })
    }

    override fun onClose() {
        showManager.release()
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

    inner class Facade : baaahs.ui.Facade() {
        val fileDialog: FileDialog
            get() = this@WebClient.fileDialog

        val plugins: Plugins
            get() = this@WebClient.toolchain.plugins

        val toolchain: Toolchain
            get() = this@WebClient.toolchain

        val isConnected: Boolean
            get() = pubSub.isConnected

        val serverIsOnline: Boolean
            get() = this@WebClient.pinkyState != null
                    && clientData != null

        val showManagerIsReady: Boolean
            get() = showManager.everSynced

        val fsRoot: Fs.File?
            get() = this@WebClient.clientData?.fsRoot

        val isMapping: Boolean
            get() = this@WebClient.pinkyState == PinkyState.Mapping

        val sceneProvider: SceneProvider
            get() = this@WebClient.sceneProvider

        val notifier: Notifier.Facade
            get() = this@WebClient.notifier.facade

        val shaderLibraries : ShaderLibraries.Facade
            get() = this@WebClient.shaderLibraries.facade

        val uiSettings: UiSettings
            get() = this@WebClient.uiSettings

        val displays: Displays
            get() = this@WebClient.displays

        fun updateUiSettings(newSettings: UiSettings, saveToStorage: Boolean) {
            this@WebClient.updateUiSettings(newSettings, saveToStorage)
        }

        suspend fun listDmxUniverses() =
            listDmxUniverses.invoke(ListDmxUniverses())

    }
}