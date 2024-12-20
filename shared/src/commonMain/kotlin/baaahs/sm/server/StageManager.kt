package baaahs.sm.server

import baaahs.*
import baaahs.app.settings.FeatureFlags
import baaahs.app.settings.Provider
import baaahs.client.document.sceneStore
import baaahs.client.document.showStore
import baaahs.control.OpenSliderControl
import baaahs.doc.SceneDocumentType
import baaahs.doc.ShowDocumentType
import baaahs.fixtures.FixtureManager
import baaahs.fixtures.RenderPlan
import baaahs.gl.Toolchain
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.io.FsServerSideSerializer
import baaahs.io.PubSubRemoteFsServerBackend
import baaahs.scene.OpenScene
import baaahs.scene.Scene
import baaahs.scene.SceneChangeListener
import baaahs.scene.SceneMonitor
import baaahs.show.*
import baaahs.show.live.OpenShow
import baaahs.sm.webapi.ClientData
import baaahs.sm.webapi.Topics
import baaahs.util.Clock
import baaahs.util.globalLaunch

class StageManager(
    toolchain: Toolchain,
    private val renderManager: RenderManager,
    private val pubSub: PubSub.Server,
    private val dataDir: Fs.File,
    private val fixtureManager: FixtureManager,
    override val clock: Clock,
    private val gadgetManager: GadgetManager,
    private val serverNotices: ServerNotices,
    private val sceneMonitor: SceneMonitor,
    private val fsSerializer: FsServerSideSerializer,
    private val pinkyConfigStore: PinkyConfigStore,
    private val showMonitor: ShowMonitor,
    private val featureFlagsProvider: Provider<FeatureFlags>
) : BaseShowPlayer(toolchain, sceneMonitor) {
    val facade = Facade()
    private var showRunner: ShowRunner? = null

    private var checkActivePatchSet: Boolean = false
    private var onScreenSliders: List<OpenSliderControl>? = null

    init {
        PubSubRemoteFsServerBackend(pubSub, fsSerializer)
    }

    @Suppress("unused")
    private val clientData =
        pubSub.state(Topics.createClientData(fsSerializer), ClientData(dataDir))

    internal val showDocumentService = ShowDocumentService()
    internal val sceneDocumentService = SceneDocumentService()

    private var openScene: OpenScene? = null

    private val frameListeners = mutableListOf<FrameListener>()

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledFeed: Feed?) {
        gadgetManager.registerGadget(id, gadget)
        super.registerGadget(id, gadget, controlledFeed)
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
                            "* " + missingPlugins.entries.joinToString("\n* ") { (desc, resources) ->
                        "${desc.title} (${resources.joinToString()})"
                    })
            }
        }
    }

    fun switchTo(
        newShow: Show?, newShowState: ShowState? = null, file: Fs.File? = null
    ) {
        showDocumentService.switchTo(newShow, newShowState, file)
    }

    fun switchToScene(newScene: Scene?, file: Fs.File? = null) {
        sceneDocumentService.switchTo(newScene, null, file)
    }

    private fun updateRunningScenePath(file: Fs.File?) {
        globalLaunch {
            pinkyConfigStore.update {
                copy(runningScenePath = file?.fullPath)
            }
        }
    }

    private fun updateRunningShowPath(file: Fs.File?) {
        globalLaunch {
            pinkyConfigStore.update {
                copy(runningShowPath = file?.fullPath)
            }
        }
    }

    suspend fun renderAndSendNextFrame(doHousekeepingFirst: Boolean = false) {
        showRunner?.let { showRunner ->
            // Unless otherwise instructed, = generate and send the next frame right away,
            // then perform any housekeeping tasks immediately afterward, to avoid frame lag.
            if (doHousekeepingFirst) housekeeping()

            frameListeners.forEach { it.beforeFrame() }
            if (showRunner.renderNextFrame()) {
                fixtureManager.sendFrame()
            }
            frameListeners.forEach { it.afterFrame() }

            if (!doHousekeepingFirst) housekeeping()
        }
    }

    override fun onActivePatchSetMayHaveChanged() {
        checkActivePatchSet = true
    }

    private fun housekeeping() {
        if (checkActivePatchSet) {
            showRunner?.onSelectedPatchesChanged()
            checkActivePatchSet = false
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
        pubSub, plugins.showStore, dataDir,
        ShowState.createTopic(
            toolchain.plugins.serialModule,
            fsSerializer
        ),
        Show.serializer(),
        fsSerializer,
        toolchain.plugins.serialModule,
        ShowDocumentType
    ) {
        override val featureFlagsProvider: Provider<FeatureFlags>
            get() = this@StageManager.featureFlagsProvider

        private val showProblems = pubSub.publish(Topics.showProblems, emptyList()) {}

        override fun createDocument(): Show = buildEmptyShow()

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
            showMonitor.onChange(newShowRunner?.openShow)
        }
    }

    inner class SceneDocumentService : DocumentService<Scene, Unit>(
        pubSub, plugins.sceneStore, dataDir,
        Scene.createTopic(
            toolchain.plugins.serialModule,
            fsSerializer
        ),
        Scene.serializer(),
        fsSerializer,
        toolchain.plugins.serialModule,
        SceneDocumentType
    ) {
        override val featureFlagsProvider: Provider<FeatureFlags>
            get() = this@StageManager.featureFlagsProvider

        override fun createDocument(): Scene = Scene.Empty

        override fun onFileChanged(saveAsFile: Fs.File) {
            updateRunningScenePath(saveAsFile)
        }

        override fun getDocumentState(): DocumentState<Scene, Unit>? {
            return document?.let { DocumentState(it, Unit, isUnsaved, file) }
        }

        override fun notifyOfDocumentChanges(fromClientUpdate: Boolean) {
            super.notifyOfDocumentChanges(fromClientUpdate)
            facade.notifyChanged()
        }

        private fun List<SceneChangeListener>.notify() {
            forEach { listener -> listener(this@StageManager.openScene) }
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

            val newOpenScene = newDocument?.open()
            this@StageManager.openScene = newOpenScene
            sceneMonitor.onChange(newOpenScene)

            notifyOfDocumentChanges(fromClientUpdate)
        }
    }

    fun addFrameListener(listener: FrameListener): FrameListener {
        frameListeners.add(listener)
        return listener
    }

    fun removeFrameListener(listener: FrameListener) {
        frameListeners.remove(listener)
    }

    inner class Facade : baaahs.ui.Facade() {
        val currentShow: Show?
            get() = this@StageManager.showRunner?.show

        val currentRenderPlan: RenderPlan?
            get() = this@StageManager.fixtureManager.facade.currentRenderPlan

        val openScene: OpenScene?
            get() = this@StageManager.openScene

        fun addFrameListener(listener: FrameListener) =
            this@StageManager.addFrameListener(listener)

        fun removeFrameListener(listener: FrameListener) =
            this@StageManager.removeFrameListener(listener)
    }
}

interface FrameListener {
    /** Called before each frame is rendered. */
    fun beforeFrame()

    /** Called after each frame has been rendered and [baaahs.gl.render.RenderTarget.sendFrame] has been called. */
    fun afterFrame()
}
