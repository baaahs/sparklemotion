package baaahs

import baaahs.app.ui.CommonIcons
import baaahs.driverack.BusAlias
import baaahs.driverack.Channel
import baaahs.driverack.DriveRack
import baaahs.driverack.RackMap
import baaahs.fixtures.FixtureManager
import baaahs.fixtures.RenderPlan
import baaahs.gadgets.Slider
import baaahs.gl.Toolchain
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.io.PubSubRemoteFsServerBackend
import baaahs.io.RemoteFsSerializer
import baaahs.libraries.ShaderLibrary
import baaahs.mapper.Storage
import baaahs.model.ModelInfo
import baaahs.show.DataSource
import baaahs.show.Show
import baaahs.show.buildEmptyShow
import baaahs.ui.Icon
import baaahs.ui.Observable
import baaahs.ui.RemovableObserver
import baaahs.ui.addObserver
import baaahs.util.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlin.coroutines.CoroutineContext

class GadgetManager(
    private val pubSub: PubSub.Server,
    private val clock: Clock,
    private val coroutineContext: CoroutineContext
) : Observable() {
    private val gadgets: MutableMap<String, Gadget> = mutableMapOf()
    var lastUserInteraction = clock.now()

    fun <T : Gadget> registerGadget(id: String, gadget: T) {
        val topic =
            PubSub.Topic("/gadgets/$id", GadgetDataSerializer)
        val channel = pubSub.publish(topic, gadget.state) { updated ->
            lastUserInteraction = clock.now()

            CoroutineScope(coroutineContext).launch {
                gadget.state.putAll(updated)
                notifyChanged()
            }
        }
        val gadgetChannelListener: (Gadget) -> Unit = { channel.onChange(it.state) }
        gadget.listen(gadgetChannelListener)
        gadgets[id] = gadget
    }

    fun <T : Gadget> useGadget(id: String): T {
        @Suppress("UNCHECKED_CAST")
        return (gadgets[id]
            ?: error("no such gadget \"$id\" among [${gadgets.keys.sorted()}]")) as T
    }
}

class StageManager(
    toolchain: Toolchain,
    private val renderManager: RenderManager,
    private val pubSub: PubSub.Server,
    private val storage: Storage,
    private val fixtureManager: FixtureManager,
    private val clock: Clock,
    modelInfo: ModelInfo,
    private val gadgetManager: GadgetManager,
    override val driveRack: DriveRack
) : BaseShowPlayer(toolchain, modelInfo) {
    val facade = Facade()
    private var showRunner: ShowRunner? = null

    private val fsSerializer = storage.fsSerializer

    init {
        PubSubRemoteFsServerBackend(pubSub, fsSerializer)

        gadgetManager.addObserver {
            if (!gadgetsChangedJobEnqueued) {
                onGadgetChange()
                gadgetsChangedJobEnqueued = false
            }
        }
    }

    private val busA = driveRack.createBus("A")
    private val busB = driveRack.createBus("B")
    private val buses = listOf(busA, busB)
    private val primary = BusAlias(busA)
    private val secondary = BusAlias(busB)

    @Suppress("unused")
    private val clientData =
        pubSub.state(Topics.createClientData(fsSerializer), ClientData(storage.fs.rootFile))

    private val showProblems = pubSub.publish(Topics.showProblems, emptyList()) {}

    private val showEditSession = ShowEditSession(fsSerializer)
    private val showEditorStateChannel: PubSub.Channel<ShowEditorState?> =
        pubSub.publish(
            ShowEditorState.createTopic(toolchain.plugins, fsSerializer),
            showEditSession.getShowEditState()
        ) { incoming ->
            val newShow = incoming?.show
            val newShowState = incoming?.showState
            val newIsUnsaved = incoming?.isUnsaved ?: false
            switchTo(
                newShow, newShowState, showEditSession.showFile,
                newIsUnsaved, fromClientUpdate = true
            )
        }

    private var gadgetsChangedJobEnqueued: Boolean = false

    private var driveRackObservers = mutableListOf<RemovableObserver<*>>()
    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?) {
        var registerWithGadgetManager = true

        if (gadget is Slider) {
            registerWithGadgetManager = false

//            primary.channel<T>(id).addObserver {
//                gadget.position = it.value as Float
//            }

//            buses.map { bus ->
//                bus.channel<Any?>(id).addObserver {}
//            }.also { driveRackObservers.addAll(it) }
        }

        if (registerWithGadgetManager) {
            gadgetManager.registerGadget(id, gadget)
        }

        super.registerGadget(id, gadget, controlledDataSource)
    }

    fun onGadgetChange() {
        showRunner?.onSelectedPatchesChanged()

        // Start housekeeping early -- as soon as we see a change -- in hopes of avoiding jank.
        if (showRunner?.housekeeping() == true) facade.notifyChanged()
    }

    override fun <T : Gadget> useGadget(id: String): T {
        return gadgetManager.useGadget(id)
    }

    override fun <T> useChannel(id: String): Channel<T> = primary.channel(id)

    fun switchTo(
        newShow: Show?,
        newShowState: ShowState? = null,
        file: Fs.File? = null,
        isUnsaved: Boolean = file == null,
        fromClientUpdate: Boolean = false
    ) {
        val newShowRunner = newShow?.let {
            val openShow = openShow(newShow, newShowState)
            ShowRunner(newShow, newShowState, openShow, clock, renderManager, fixtureManager) { problems ->
                this.showProblems.onChange(problems)
            }
        }

        showRunner?.release()
        releaseUnused()

        showRunner = newShowRunner

        wireDriveRack(newShowRunner?.rackMap ?: RackMap.Empty)

        showEditSession.show = newShowRunner?.show
        showEditSession.showFile = file
        showEditSession.showIsUnsaved = isUnsaved

        updateRunningShowPath(file)

        notifyOfShowChanges(fromClientUpdate)
    }

    private fun wireDriveRack(rackMap: RackMap) {
        driveRack.rackMap = rackMap

//        rackMap.entries.map { entry ->
//            Updatable(entry.initialValue).also { updatable ->
//                primary.channel<Any?>(entry.id).addObserver { channel ->
//                    updatable.value = channel.value
//                }
//            }
//        }
    }

    class Updatable(
        var value: Any? = null
    )

    private fun updateRunningShowPath(file: Fs.File?) {
        GlobalScope.launch {
            storage.updateConfig {
                copy(runningShowPath = file?.fullPath)
            }
        }
    }

    internal fun notifyOfShowChanges(fromClientUpdate: Boolean = false) {
        if (!fromClientUpdate) {
            showEditorStateChannel.onChange(showEditSession.getShowEditState())
        }

        facade.notifyChanged()
    }

    fun renderAndSendNextFrame(dontProcrastinate: Boolean = true) {
        showRunner?.let { showRunner ->
            // Unless otherwise instructed, = generate and send the next frame right away,
            // then perform any housekeeping tasks immediately afterward, to avoid frame lag.
            if (dontProcrastinate) housekeeping()

            if (showRunner.renderNextFrame()) {
                fixtureManager.sendFrame()
            }

            if (!dontProcrastinate) housekeeping()
        }
    }

    private fun housekeeping() {
        if (showRunner!!.housekeeping()) facade.notifyChanged()
    }

    fun shutDown() {
        showRunner?.release()
        showEditorStateChannel.unsubscribe()
    }

    inner class ShowEditSession(remoteFsSerializer: RemoteFsSerializer) {
        var show: Show? = null
        var showFile: Fs.File? = null
        var showIsUnsaved: Boolean = false

        init {
            val commands = Topics.Commands(SerializersModule {
                include(remoteFsSerializer.serialModule)
                include(toolchain.plugins.serialModule)
            })
            pubSub.listenOnCommandChannel(commands.newShow) { command -> handleNewShow(command) }
            pubSub.listenOnCommandChannel(commands.switchToShow) { command -> handleSwitchToShow(command.file) }
            pubSub.listenOnCommandChannel(commands.saveShow) { command -> handleSaveShow() }
            pubSub.listenOnCommandChannel(commands.saveAsShow) { command ->
                val saveAsFile = storage.resolve(command.file.fullPath)
                handleSaveAsShow(saveAsFile)
                updateRunningShowPath(saveAsFile)
            }
        }

        private suspend fun handleNewShow(command: NewShowCommand) {
            switchTo(command.template ?: buildEmptyShow())
        }

        private suspend fun handleSwitchToShow(file: Fs.File?) {
            if (file != null) {
                switchTo(storage.loadShow(file), file = file, isUnsaved = false)
            } else {
                switchTo(null, null, null)
            }
        }

        private suspend fun handleSaveShow() {
            showFile?.let { showFile ->
                show?.let { show -> saveShow(showFile, show) }
            }
        }

        private suspend fun handleSaveAsShow(showAsFile: Fs.File) {
            show?.let { show -> saveShow(showAsFile, show) }
        }

        private suspend fun saveShow(file: Fs.File, show: Show) {
            storage.saveShow(file, show)
            showFile = file
            showIsUnsaved = false
            notifyOfShowChanges()
        }

        fun getShowEditState(): ShowEditorState? {
            return showRunner?.let { showRunner ->
                show?.withState(showRunner.getShowState(), showIsUnsaved, showFile)
            }
        }
    }

    inner class Facade : baaahs.ui.Facade() {
        val currentShow: Show?
            get() = this@StageManager.showRunner?.show

        val currentRenderPlan: RenderPlan?
            get() = this@StageManager.fixtureManager.currentRenderPlan
    }
}

interface RefCounted {
    fun inUse(): Boolean
    fun use()
    fun release()
    fun onFullRelease()
}

class RefCounter : RefCounted {
    var refCount: Int = 0

    override fun inUse(): Boolean = refCount == 0

    override fun use() {
        refCount++
    }

    override fun release() {
        refCount--

        if (!inUse()) onFullRelease()
    }

    override fun onFullRelease() {
    }
}

@Serializable
data class ClientData(
    val fsRoot: Fs.File
)

@Serializable
class NewShowCommand(val template: Show? = null)

@Serializable
class SwitchToShowCommand(val file: Fs.File?)

@Serializable
class SaveShowCommand

@Serializable
class SaveAsShowCommand(val file: Fs.File)

@Serializable
class SearchShaderLibraries(val terms: String) {
    @Serializable
    class Response(val matches: List<ShaderLibrary.Entry>)
}

@Serializable
data class ShowProblem(
    val title: String,
    val message: String? = null,
    val severity: Severity = Severity.ERROR,
    val id: String = randomId("error")
)

fun Collection<ShowProblem>.severity() = maxOfOrNull { it.severity }

enum class Severity(val icon: Icon) {
    INFO(CommonIcons.Info),
    WARN(CommonIcons.Warning),
    ERROR(CommonIcons.Error)
}