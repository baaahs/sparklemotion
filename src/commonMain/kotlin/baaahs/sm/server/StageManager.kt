package baaahs.sm.server

import baaahs.*
import baaahs.controller.ControllersManager
import baaahs.fixtures.FixtureManager
import baaahs.fixtures.RenderPlan
import baaahs.gl.Toolchain
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.io.PubSubRemoteFsServerBackend
import baaahs.io.RemoteFsSerializer
import baaahs.mapper.Storage
import baaahs.model.ModelInfo
import baaahs.show.DataSource
import baaahs.show.Show
import baaahs.show.buildEmptyShow
import baaahs.show.live.OpenShow
import baaahs.sm.webapi.ClientData
import baaahs.sm.webapi.NewShowCommand
import baaahs.sm.webapi.Topics
import baaahs.ui.addObserver
import baaahs.util.Clock
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule

class StageManager(
    toolchain: Toolchain,
    private val renderManager: RenderManager,
    private val pubSub: PubSub.Server,
    private val storage: Storage,
    private val fixtureManager: FixtureManager,
    private val clock: Clock,
    modelInfo: ModelInfo,
    private val gadgetManager: GadgetManager,
    private val controllersManager: ControllersManager,
    private val serverNotices: ServerNotices
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

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?) {
        gadgetManager.registerGadget(id, gadget)
        super.registerGadget(id, gadget, controlledDataSource)
    }

    private fun onGadgetChange() {
        showRunner?.onSelectedPatchesChanged()

        // Start housekeeping early -- as soon as we see a change -- in hopes of avoiding jank.
        if (showRunner?.housekeeping() == true) facade.notifyChanged()
    }

    override fun <T : Gadget> useGadget(id: String): T {
        return gadgetManager.useGadget(id)
    }

    override fun openShow(show: Show, showState: ShowState?): OpenShow {
        return super.openShow(show, showState).also {
            val missingPlugins = it.missingPlugins
            if (missingPlugins.isNotEmpty()) {
                serverNotices.add(
                    "Missing Plugins",
                    "The following plugin(s) are unavailable, " +
                            "so some features may not work as expected:\n" +
                            "* " + missingPlugins.joinToString("\n* ") { desc -> desc.title })
            }
        }
    }

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
        showEditSession.show = newShowRunner?.show
        showEditSession.showFile = file
        showEditSession.showIsUnsaved = isUnsaved

        updateRunningShowPath(file)

        notifyOfShowChanges(fromClientUpdate)
    }

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

    suspend fun renderAndSendNextFrame(dontProcrastinate: Boolean = true) {
        showRunner?.let { showRunner ->
            // Unless otherwise instructed, = generate and send the next frame right away,
            // then perform any housekeeping tasks immediately afterward, to avoid frame lag.
            if (dontProcrastinate) housekeeping()

            controllersManager.beforeFrame()
            if (showRunner.renderNextFrame()) {
                fixtureManager.sendFrame()
            }
            controllersManager.afterFrame()

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

    fun logStatus() {
        renderManager.logStatus()
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