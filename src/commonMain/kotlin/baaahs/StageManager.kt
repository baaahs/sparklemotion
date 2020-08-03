package baaahs

import baaahs.glshaders.GlslProgram
import baaahs.glshaders.OpenPatch
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslRenderer
import baaahs.io.Fs
import baaahs.io.FsServerSideSerializer
import baaahs.io.PubSubRemoteFsServerBackend
import baaahs.io.RemoteFsSerializer
import baaahs.mapper.Storage
import baaahs.model.ModelInfo
import baaahs.show.*
import com.soywiz.klock.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class StageManager(
    plugins: Plugins,
    private val glslRenderer: GlslRenderer,
    private val pubSub: PubSub.Server,
    private val storage: Storage,
    private val surfaceManager: SurfaceManager,
    private val dmxUniverse: Dmx.Universe,
    private val movingHeadManager: MovingHeadManager,
    private val clock: Clock,
    modelInfo: ModelInfo
) : BaseShowPlayer(plugins, modelInfo) {
    val facade = Facade()
    override val glslContext: GlslContext
        get() = glslRenderer.gl
    private val showStateChannel = pubSub.publish(Topics.showState, null) { showState ->
        if (showState != null) showRunner?.switchTo(showState)
    }
    private var showRunner: ShowRunner? = null
    private val gadgets: MutableMap<String, GadgetManager.GadgetInfo> = mutableMapOf()
    var lastUserInteraction = DateTime.now()

    private val fsSerializer = FsServerSideSerializer()
    init { PubSubRemoteFsServerBackend(pubSub, fsSerializer) }
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
            switchTo(newShow, newShowState, showEditSession.showFile, newIsUnsaved)
        }

    override fun <T : Gadget> createdGadget(id: String, gadget: T) {
        val topic =
            PubSub.Topic("/gadgets/$id", GadgetDataSerializer)
        val channel = pubSub.publish(topic, gadget.state) { updated ->
            gadget.state.putAll(updated)
            lastUserInteraction = DateTime.now()
        }
        val gadgetChannelListener: (Gadget) -> Unit = { gadget1 ->
            channel.onChange(gadget1.state)
        }
        gadget.listen(gadgetChannelListener)
        val gadgetData = GadgetData(id, gadget, topic.name)
        gadgets[id] = GadgetManager.GadgetInfo(topic, channel, gadgetData, gadgetChannelListener)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Gadget> useGadget(id: String): T {
        return (gadgets[id]?.gadgetData?.gadget
            ?: error("no such gadget \"$id\" among [${gadgets.keys.sorted()}]")) as T
    }

    fun switchTo(
        newShow: Show?,
        newShowState: ShowState? = newShow?.defaultShowState(),
        file: Fs.File? = null,
        isUnsaved: Boolean = file == null
    ) {
        val newShowRunner = newShow?.let {
            ShowRunner(newShow, newShowState, openShow(newShow), clock, glslRenderer, surfaceManager)
        }

        showRunner?.release()
        releaseUnused()

        showRunner = newShowRunner
        showEditSession.show = newShowRunner?.show
        showEditSession.showFile = file
        showEditSession.showIsUnsaved = isUnsaved

        CoroutineScope(Dispatchers.Main).launch {
            storage.updateConfig {
                copy(runningShowPath = file?.fullPath)
            }
        }

        notifyOfShowChanges()
    }

    internal fun notifyOfShowChanges() {
        val showEditState = showEditSession.getShowEditState()
        showEditorStateChannel.onChange(showEditState)
        showStateChannel.onChange(showEditState?.showState)
        facade.notifyChanged()
    }

    fun renderAndSendNextFrame(dontProcrastinate: Boolean = true) {
        showRunner?.let { showRunner ->
            // Unless otherwise instructed, = generate and send the next frame right away,
            // then perform any housekeeping tasks immediately afterward, to avoid frame lag.
            if (dontProcrastinate) showRunner.housekeeping()

            if (showRunner.renderNextFrame()) {
                surfaceManager.sendFrame()
                dmxUniverse.sendFrame()
            }

            if (!dontProcrastinate) showRunner.housekeeping()
        }
    }

    fun shutDown() {
        showRunner?.release()
        showStateChannel.unsubscribe()
        showEditorStateChannel.unsubscribe()
    }

    inner class ShowEditSession(remoteFsSerializer: RemoteFsSerializer) {
        var show: Show? = null
        var showFile: Fs.File? = null
        var showIsUnsaved: Boolean = false

        init {
            val commands = Topics.Commands(remoteFsSerializer)
            pubSub.listenOnCommandChannel(commands.newShow) { command, reply -> handleNewShow() }
            pubSub.listenOnCommandChannel(commands.switchToShow) { command, reply -> handleSwitchToShow(command.file) }
            pubSub.listenOnCommandChannel(commands.saveShow) { command, reply -> handleSaveShow() }
            pubSub.listenOnCommandChannel(commands.saveAsShow) { command, reply ->
                handleSaveAsShow(storage.resolve(command.file.fullPath))
            }
        }

        private fun handleNewShow() {
            val newShow = ShowEditor("Untitled").apply {
                addScene("Scene 1") {
                    addPatchSet("All Dark") {
                    }
                }
                addControl("Scenes", SpecialControl("baaahs.Core:Scenes"))
                addControl("Patches", SpecialControl("baaahs.Core:Patches"))

                editLayouts {
                    copyFrom(
                        Layouts(
                            listOf("Scenes", "Patches", "More Controls", "Preview", "Controls"),
                            mapOf("default" to SampleData.defaultLayout)
                        )
                    )
                }
            }
            switchTo(newShow.getShow(), newShow.getShowState())
        }

        private fun handleSwitchToShow(file: Fs.File?) {
            CoroutineScope(Dispatchers.Main).launch {
                if (file != null) {
                    switchTo(storage.loadShow(file), file = file, isUnsaved = false)
                } else {
                    switchTo(null, null, null)
                }
                notifyOfShowChanges()
            }
        }

        private fun handleSaveShow() {
            showFile?.let { showFile ->
                show?.let { show -> saveShow(showFile, show) }
            }
        }

        private fun handleSaveAsShow(showAsFile: Fs.File) {
            show?.let { show -> saveShow(showAsFile, show) }
        }

        private fun saveShow(file: Fs.File, show: Show) {
            CoroutineScope(Dispatchers.Main).launch {
                storage.saveShow(file, show)
                showFile = file
                showIsUnsaved = false
                notifyOfShowChanges()
            }
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

class RenderPlan(val programs: List<Pair<OpenPatch, GlslProgram>>) {
    fun render(glslRenderer: GlslRenderer) {
        glslRenderer.draw()
    }
}

@Serializable
data class ClientData(
    val fsRoot: Fs.File
)

@Serializable
class NewShowCommand {
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
