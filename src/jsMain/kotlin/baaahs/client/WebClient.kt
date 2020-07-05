package baaahs.client

import baaahs.*
import baaahs.app.ui.AppIndex
import baaahs.app.ui.AppIndexProps
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslBase
import baaahs.net.Network
import baaahs.proto.Ports
import baaahs.show.Show
import baaahs.ui.SaveAsFs
import baaahs.util.UndoStack
import kotlinext.js.jsObject
import react.ReactElement
import react.createElement

class WebClient(
    network: Network,
    pinkyAddress: Network.Address,
    private val filesystems: List<SaveAsFs>,
    plugins: Plugins = Plugins.findAll()
) : HostedWebApp {
    private val facade = Facade()

    private val webClientLink = network.link("app")
    private val pubSub = PubSub.Client(webClientLink, pinkyAddress, Ports.PINKY_UI_TCP)
    private val pubSubListener = { facade.notifyChanged() }
    init { pubSub.addStateChangeListener(pubSubListener) }

    private val glslContext = GlslBase.jsManager.createContext()

    private var show: Show? = null
    private var openShow: OpenShow? = null
    private var showState: ShowState? = null

    private val showResources = ClientShowResources(plugins, glslContext, pubSub)
    private val showWithStateChannel = pubSub.subscribe(showResources.showWithStateTopic) {
        switchTo(it.show, it.showState)
        undoStack.reset(it)
        facade.notifyChanged()
    }
    private val showStateChannel = pubSub.subscribe(Topics.showState) {
        showState = it
        facade.notifyChanged()
    }

    private val undoStack = UndoStack<ShowWithState>()

    private fun switchTo(show: Show, showState: ShowState) {
        openShow = showResources.swapAndRelease(openShow, show)
        this.show = show
        this.showState = showState
    }

    override fun render(): ReactElement {
        println("WebClient: my link is ${webClientLink.myAddress}")

        return createElement(AppIndex, jsObject<AppIndexProps> {
            this.id = "Client Window"
            this.webClient = facade
            this.undoStack = this@WebClient.undoStack
            this.filesystems = this@WebClient.filesystems
            this.showResources = this@WebClient.showResources
        })
    }

    override fun onClose() {
        showStateChannel.unsubscribe()
        showWithStateChannel.unsubscribe()
        pubSub.removeStateChangeListener(pubSubListener)
    }

    inner class Facade : baaahs.ui.Facade() {
        val isConnected: Boolean
            get() = pubSub.isConnected

        val show: Show?
            get() = this@WebClient.show

        val showState: ShowState?
            get() = this@WebClient.showState

        val openShow: OpenShow?
            get() = this@WebClient.openShow

        fun onShowEdit(showWithState: ShowWithState) {
            showWithStateChannel.onChange(showWithState)
            switchTo(showWithState.show, showWithState.showState)
            facade.notifyChanged()
        }

        fun onShowStateChange(showState: ShowState) {
            showStateChannel.onChange(showState)
            this@WebClient.showState = showState
            facade.notifyChanged()
        }
    }
}
