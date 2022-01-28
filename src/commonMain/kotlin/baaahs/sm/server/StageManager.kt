package baaahs.sm.server

import baaahs.*
import baaahs.controller.ControllersManager
import baaahs.doc.SceneDocumentType
import baaahs.doc.ShowDocumentType
import baaahs.fixtures.FixtureManager
import baaahs.fixtures.RenderPlan
import baaahs.gl.Toolchain
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.io.PubSubRemoteFsServerBackend
import baaahs.mapper.Storage
import baaahs.scene.Scene
import baaahs.show.DataSource
import baaahs.show.Show
import baaahs.show.ShowState
import baaahs.show.buildEmptyShow
import baaahs.show.live.OpenShow
import baaahs.sm.webapi.ClientData
import baaahs.sm.webapi.Topics
import baaahs.ui.addObserver
import baaahs.util.Clock
import baaahs.util.globalLaunch

class StageManager(
    toolchain: Toolchain,
    private val renderManager: RenderManager,
    private val pubSub: PubSub.Server,
    private val storage: Storage,
    private val fixtureManager: FixtureManager,
    private val clock: Clock,
    modelProvider: ModelProvider,
    private val gadgetManager: GadgetManager,
    private val controllersManager: ControllersManager,
    private val serverNotices: ServerNotices
) : BaseShowPlayer(toolchain, modelProvider) {
    val facade = Facade()
    private var showRunner: ShowRunner? = null

    private val fsSerializer = storage.fsSerializer
    private var gadgetsChanged: Boolean = false

    init {
        PubSubRemoteFsServerBackend(pubSub, fsSerializer)

        gadgetManager.addObserver {
            gadgetsChanged = true
        }
    }

    @Suppress("unused")
    private val clientData =
        pubSub.state(Topics.createClientData(fsSerializer), ClientData(storage.fs.rootFile))

    internal val showDocumentService = ShowDocumentService()
    internal val sceneDocumentService = SceneDocumentService()

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?) {
        gadgetManager.registerGadget(id, gadget)
        super.registerGadget(id, gadget, controlledDataSource)
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
        file: Fs.File? = null
    ) {
        showDocumentService.switchTo(newShow, newShowState, file)
    }

    private fun updateRunningScenePath(file: Fs.File?) {
        globalLaunch {
            storage.updateConfig {
                copy(runningScenePath = file?.fullPath)
            }
        }
    }

    private fun updateRunningShowPath(file: Fs.File?) {
        globalLaunch {
            storage.updateConfig {
                copy(runningShowPath = file?.fullPath)
            }
        }
    }

    suspend fun renderAndSendNextFrame(doHousekeepingFirst: Boolean = false) {
        showRunner?.let { showRunner ->
            // Unless otherwise instructed, = generate and send the next frame right away,
            // then perform any housekeeping tasks immediately afterward, to avoid frame lag.
            if (doHousekeepingFirst) housekeeping()

            controllersManager.beforeFrame()
            if (showRunner.renderNextFrame()) {
                fixtureManager.sendFrame()
            }
            controllersManager.afterFrame()

            if (!doHousekeepingFirst) housekeeping()
        }
    }

    private fun housekeeping() {
        if (gadgetsChanged) {
            showRunner?.onSelectedPatchesChanged()
            gadgetsChanged = false
        }

        // Start housekeeping early -- as soon as we see a change -- in hopes of avoiding jank.
        if (showRunner?.housekeeping() == true) facade.notifyChanged()
    }

    fun shutDown() {
        showRunner?.release()
        showDocumentService.release()
    }

    fun logStatus() {
        renderManager.logStatus()
    }

    inner class ShowDocumentService : DocumentService<Show, ShowState>(
        pubSub, storage,
        ShowState.createTopic(
            toolchain.plugins.serialModule,
            fsSerializer
        ),
        Show.serializer(),
        fsSerializer,
        toolchain.plugins.serialModule,
        ShowDocumentType
    ) {
        private val showProblems = pubSub.publish(Topics.showProblems, emptyList()) {}

        override fun createDocument(): Show = buildEmptyShow()

        override suspend fun load(file: Fs.File): Show? {
            return storage.loadShow(file)
        }

        override suspend fun save(file: Fs.File, document: Show) {
            storage.saveShow(file, document)
        }

        override fun onFileChanged(saveAsFile: Fs.File) {
            updateRunningShowPath(saveAsFile)
        }

        override fun getDocumentState(): DocumentState<Show, ShowState>? {
            return showRunner?.let { showRunner ->
                document?.withState(showRunner.getShowState(), isUnsaved, file)
            }
        }

        override fun notifyOfDocumentChanges(fromClientUpdate: Boolean) {
            super.notifyOfDocumentChanges(fromClientUpdate)
            facade.notifyChanged()
        }

        override fun switchTo(
            newDocument: Show?,
            newState: ShowState?,
            file: Fs.File?,
            isUnsaved: Boolean,
            fromClientUpdate: Boolean
        ) {
            val newShowRunner = newDocument?.let {
                val openShow = openShow(newDocument, newState)
                ShowRunner(newDocument, newState, openShow, clock, renderManager, fixtureManager) { problems ->
                    showProblems.onChange(problems)
                }
            }

            showRunner?.release()
            releaseUnused()

            showRunner = newShowRunner
            super.switchTo(newDocument, newState, file, isUnsaved, fromClientUpdate)

            updateRunningShowPath(file)

            notifyOfDocumentChanges(fromClientUpdate)
        }
    }

    inner class SceneDocumentService : DocumentService<Scene, Unit>(
        pubSub, storage,
        Scene.createTopic(
            toolchain.plugins.serialModule,
            fsSerializer
        ),
        Scene.serializer(),
        fsSerializer,
        toolchain.plugins.serialModule,
        SceneDocumentType
    ) {
        override fun createDocument(): Scene = Scene.Empty

        override suspend fun load(file: Fs.File): Scene? {
            return storage.loadScene(file)
        }

        override suspend fun save(file: Fs.File, document: Scene) {
            storage.saveScene(file, document)
        }

        override fun onFileChanged(saveAsFile: Fs.File) {
            updateRunningScenePath(saveAsFile)
        }

        override fun getDocumentState(): DocumentState<Scene, Unit>? {
            return document?.let { DocumentState(it, Unit, true, null) }
        }

        override fun notifyOfDocumentChanges(fromClientUpdate: Boolean) {
            super.notifyOfDocumentChanges(fromClientUpdate)
            facade.notifyChanged()
        }

        override fun switchTo(
            newDocument: Scene?,
            newState: Unit?,
            file: Fs.File?,
            isUnsaved: Boolean,
            fromClientUpdate: Boolean
        ) {
//            val newShowRunner = newDocument?.let {
//                val openShow = openShow(newDocument, newState)
//                ShowRunner(newDocument, newState, openShow, clock, renderManager, fixtureManager) { problems ->
//                    showProblems.onChange(problems)
//                }
//            }

//            showRunner?.release()
//            releaseUnused()

//            showRunner = newShowRunner
            super.switchTo(newDocument, newState, file, isUnsaved, fromClientUpdate)

            updateRunningScenePath(file)

            controllersManager.onSceneChange(newDocument)

            notifyOfDocumentChanges(fromClientUpdate)
        }
    }

    inner class Facade : baaahs.ui.Facade() {
        val currentShow: Show?
            get() = this@StageManager.showRunner?.show

        val currentRenderPlan: RenderPlan?
            get() = this@StageManager.fixtureManager.facade.currentRenderPlan
    }
}