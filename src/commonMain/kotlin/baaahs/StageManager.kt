package baaahs

import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.render.ModelRenderer
import baaahs.io.Fs
import baaahs.io.FsServerSideSerializer
import baaahs.io.PubSubRemoteFsServerBackend
import baaahs.io.RemoteFsSerializer
import baaahs.mapper.Storage
import baaahs.model.ModelInfo
import baaahs.plugin.Plugins
import baaahs.show.DataSource
import baaahs.show.Show
import baaahs.show.Surfaces
import baaahs.show.buildEmptyShow
import baaahs.show.live.ActiveSet
import com.soywiz.klock.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlin.coroutines.CoroutineContext

class StageManager(
    plugins: Plugins,
    private val modelRenderer: ModelRenderer,
    private val pubSub: PubSub.Server,
    private val storage: Storage,
    private val surfaceManager: SurfaceManager,
    private val dmxUniverse: Dmx.Universe,
    private val movingHeadManager: MovingHeadManager,
    private val clock: Clock,
    modelInfo: ModelInfo,
    private val coroutineContext: CoroutineContext
) : BaseShowPlayer(plugins, modelInfo) {
    val facade = Facade()
    private val autoWirer = AutoWirer(plugins)
    override val glContext: GlContext
        get() = modelRenderer.gl
    private var showRunner: ShowRunner? = null
    private val gadgets: MutableMap<String, GadgetManager.GadgetInfo> = mutableMapOf()
    var lastUserInteraction = DateTime.now()

    private val fsSerializer = FsServerSideSerializer()

    init {
        PubSubRemoteFsServerBackend(pubSub, fsSerializer)
    }

    @Suppress("unused")
    private val clientData =
        pubSub.state(Topics.createClientData(fsSerializer), ClientData(storage.fs.rootFile))

    private val showEditSession = ShowEditSession(fsSerializer)
    private val showEditorStateChannel: PubSub.Channel<ShowEditorState?> =
        pubSub.publish(
            ShowEditorState.createTopic(plugins, fsSerializer),
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
        val topic =
            PubSub.Topic("/gadgets/$id", GadgetDataSerializer)
        val channel = pubSub.publish(topic, gadget.state) { updated ->
            CoroutineScope(coroutineContext).launch {
                gadget.state.putAll(updated)
                lastUserInteraction = DateTime.now()

                if (!gadgetsChangedJobEnqueued) {
                    onGadgetChange()
                    gadgetsChangedJobEnqueued = false
                }
            }
        }
        val gadgetChannelListener: (Gadget) -> Unit = { channel.onChange(it.state) }
        gadget.listen(gadgetChannelListener)
        val gadgetData = GadgetData(id, gadget, topic.name)
        gadgets[id] = GadgetManager.GadgetInfo(topic, channel, gadgetData, gadgetChannelListener)
        controlledDataSource?.let { dataSourceGadgets[controlledDataSource] = gadget }
    }

    fun onGadgetChange() {
        showRunner?.onSelectedPatchesChanged()

        // Start housekeeping early -- as soon as we see a change -- in hopes of avoiding jank.
        showRunner?.housekeeping()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Gadget> useGadget(id: String): T {
        return (gadgets[id]?.gadgetData?.gadget
            ?: error("no such gadget \"$id\" among [${gadgets.keys.sorted()}]")) as T
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
            ShowRunner(newShow, newShowState, openShow, clock, modelRenderer, surfaceManager, autoWirer)
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

    fun renderAndSendNextFrame(dontProcrastinate: Boolean = true) {
        showRunner?.let { showRunner ->
            // Unless otherwise instructed, = generate and send the next frame right away,
            // then perform any housekeeping tasks immediately afterward, to avoid frame lag.
            if (dontProcrastinate) housekeeping()

            if (showRunner.renderNextFrame()) {
                surfaceManager.sendFrame()
                dmxUniverse.sendFrame()
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
                include(plugins.serialModule)
            })
            pubSub.listenOnCommandChannel(commands.newShow) { command, reply -> handleNewShow(command) }
            pubSub.listenOnCommandChannel(commands.switchToShow) { command, reply -> handleSwitchToShow(command.file) }
            pubSub.listenOnCommandChannel(commands.saveShow) { command, reply -> handleSaveShow() }
            pubSub.listenOnCommandChannel(commands.saveAsShow) { command, reply ->
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

        val currentGlsl: Map<Surfaces, String>?
            get() = this@StageManager.showRunner?.currentRenderPlan
                ?.programs?.map { (patch, program) ->
                    patch.surfaces to program.fragShader.source
                }?.associate { it }
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

class RenderPlan(val programs: List<Pair<LinkedPatch, GlslProgram>>, val activeSet: ActiveSet) {
}

@Serializable
data class ClientData(
    val fsRoot: Fs.File
)

@Serializable
class NewShowCommand(
    val template: Show? = null
) {
    @Serializable
    class Response
}

@Serializable
class SwitchToShowCommand(
    val file: Fs.File?
) {
    @Serializable
    class Response
}

@Serializable
class SaveShowCommand {
    @Serializable
    class Response
}

@Serializable
class SaveAsShowCommand(val file: Fs.File) {
    @Serializable
    class Response
}
