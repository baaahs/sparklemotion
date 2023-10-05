package baaahs.show.mutable

import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.PatchEditorPanel
import baaahs.control.MutableButtonControl
import baaahs.control.MutableButtonGroupControl
import baaahs.control.OpenButtonControl
import baaahs.control.OpenButtonGroupControl
import baaahs.device.PixelArrayDevice
import baaahs.getBang
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.LinkedProgram
import baaahs.gl.patch.ProgramResolver
import baaahs.gl.preview.ProjectionPreviewDevice
import baaahs.gl.shader.OpenShader
import baaahs.randomId
import baaahs.show.*
import baaahs.show.live.OpenPatchHolder
import baaahs.show.live.OpenShow
import baaahs.show.live.PatchResolver
import baaahs.unknown
import baaahs.util.CacheBuilder

class MutableShow(
    baseShow: Show
) : MutablePatchHolder(baseShow), MutableDocument<Show> {
    override val mutableShow: MutableShow get() = this

    private val implicitControls: Map<String, Control> = baseShow.findImplicitControls()
    internal val controls = CacheBuilder<String, MutableControl> { id ->
        (baseShow.controls[id]
            ?: implicitControls[id]
            ?: error(unknown("control", id, baseShow.controls.keys + implicitControls.keys))
        ).createMutable(this).also { it.asBuiltId = id }
    }

    internal val feeds = baseShow.feeds
        .mapValues { (_, shader) -> MutableFeedPort(shader) }
        .toMutableMap()

    internal val shaders = baseShow.shaders
        .mapValues { (_, shader) -> MutableShader(shader) }
        .toMutableMap()

    private val streams = CacheBuilder<Stream, MutableStream> {
        MutableStream(it.id)
    }

    private val allPatches = baseShow.patches
        .mapValues { (_, patch) ->
            MutablePatch(
                findShader(patch.shaderId),
                hashMapOf(),
                streams[patch.stream],
                patch.priority
            )
        }

    internal val layouts = MutableLayouts(baseShow.layouts, this)

    init {
        // Second pass required here since they might refer to each other.
        baseShow.patches.forEach { (id, patch) ->
            val editor = findPatch(id)
            val resolvedIncomingLinks = patch.incomingLinks.mapValues { (_, fromPortRef) ->
                fromPortRef.dereference(this)
            }
            editor.incomingLinks.putAll(resolvedIncomingLinks)
        }
    }

    constructor(title: String, block: MutableShow.() -> Unit = {}) : this(Show(title)) {
        this.block()
    }

    fun invoke(block: MutableShow.() -> Unit) = this.block()

    fun editLayouts(block: MutableLayouts.() -> Unit): MutableShow {
        layouts.apply(block)
        return this
    }

    fun build(showBuilder: ShowBuilder): Show {
        return Show(
            title,
            patchIds = patches.map { showBuilder.idFor(it.build(showBuilder)) },
            eventBindings = eventBindings,
            controlLayout = buildControlLayout(showBuilder),
            layouts = layouts.build(showBuilder),
            shaders = showBuilder.getShaders(),
            controls = showBuilder.getControls(),
            patches = showBuilder.getPatches(),
            feeds = run {
                showBuilder.includeDependencyFeeds()
                showBuilder.getFeeds()
            }
        )
    }

    fun getShow() = build(ShowBuilder())

    override fun build(): Show = getShow()

    fun findControl(controlId: String): MutableControl =
        controls.getBang(controlId, "control")

    fun edit(buttonGroupControl: OpenButtonGroupControl, block: MutableButtonGroupControl.() -> Unit = {}) {
        (findControl(buttonGroupControl.id) as MutableButtonGroupControl).block()
    }

    fun findFeed(feedId: String): MutableFeedPort =
        feeds.getBang(feedId, "feed")

    fun findPatchHolder(openPatchHolder: OpenPatchHolder): MutablePatchHolder {
        return when (openPatchHolder) {
            is OpenShow -> this

            // Yuck.
            is OpenButtonControl -> return findControl(openPatchHolder.id) as MutableButtonControl

            else -> error("huh? $openPatchHolder isn't a show or a button?")
        }
    }

    fun findPanel(panelId: String): MutablePanel =
        layouts.panels.getBang(panelId, "panel")

    fun findPanel(panel: Panel): MutablePanel =
        layouts.panels.values.find { it.isFor(panel) } ?: error("mutable panel ${panel.title} not found")

    fun findShader(shaderId: String): MutableShader =
        shaders.getBang(shaderId, "shader")

    fun findPatch(id: String): MutablePatch =
        allPatches.getBang(id, "patch")

    fun findGridItem(
        layoutId: String,
        tabTitle: String,
        vararg controlTitles: String
    ): MutableGridItem {
        var layout = layouts
            .findLayout(layoutId)
            .findTab(tabTitle) as MutableIGridLayout?
        var item: MutableGridItem? = null
        for (title in controlTitles) {
            if (layout == null) error("layout not found")
            item = layout.find(title)
            layout = item.layout
        }
        return item!!
    }

    fun commit(editHandler: EditHandler<Show, ShowState>) {
        editHandler.onEdit(this)
    }

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        super.accept(visitor, log)

        layouts.formats.values.forEach { layout ->
            layout.accept(visitor, log)
        }
    }

    companion object {
        fun create(title: String): Show {
            return Show(title = title)
        }
    }
}

data class MutablePatch(
    val mutableShader: MutableShader,
    val incomingLinks: MutableMap<String, MutablePort> = hashMapOf(),
    var stream: MutableStream = Stream.Main.editor(),
    var priority: Float = 0f
) {
    constructor(basePatch: Patch, show: MutableShow) : this(
        show.findShader(basePatch.shaderId),
        basePatch.incomingLinks.mapValues { (_, v) -> v.dereference(show) }.toMutableMap(),
        basePatch.stream.toMutable(),
        basePatch.priority
    )

    constructor(shader: Shader, block: MutablePatch.() -> Unit = {}) : this(MutableShader(shader)) {
        block()
    }

    val id = randomId("MutablePatch")
    val title: String
        get() = mutableShader.title
    var surfaces: Surfaces = Surfaces.AllSurfaces

    fun findFeeds(): List<Feed> {
        return incomingLinks.mapNotNull { (_, from) ->
            (from as? MutableFeedPort)?.feed
        }
    }

    fun findStreams(): List<MutableStream> {
        return (incomingLinks.values.map { link ->
            link as? MutableStream
        } + stream).filterNotNull()
    }

    fun link(portId: String, toPort: Feed) {
        incomingLinks[portId] = toPort.editor()
    }

    fun link(portId: String, toPort: MutablePort) {
        incomingLinks[portId] = toPort
    }

    fun build(showBuilder: ShowBuilder): Patch {
        return Patch(
            showBuilder.idFor(mutableShader.build()),
            incomingLinks.mapValues { (_, portRef) ->
                portRef.toRef(showBuilder)
            },
            stream.build(),
            priority
        )
    }

    fun accept(visitor: MutableShowVisitor, log: VisitationLog = VisitationLog()) {
        if (log.patches.add(this)) visitor.visit(this)
        mutableShader.accept(visitor, log)
        stream.accept(visitor, log)
        incomingLinks.forEach { (_, port) -> port.accept(visitor, log) }
        surfaces.accept(visitor, log)
    }

    fun isFilter(openShader: OpenShader): Boolean = with(openShader) {
        inputPorts.any {
            it.contentType == outputPort.contentType && incomingLinks[it.id]?.let { link ->
                link is MutableStream && link.id == stream.id
            } == true
        }
    }

    fun getEditorPanel(editableManager: EditableManager<*>) =
        PatchEditorPanel(editableManager, this)
}

class MutablePatchSet(val mutablePatches: MutableList<MutablePatch> = mutableListOf()) {
    constructor(vararg mutablePatches: MutablePatch) : this(mutablePatches.toMutableList())

    fun addPatch(shader: Shader, block: MutablePatch.() -> Unit = {}): MutablePatchSet =
        addPatch(MutableShader(shader), block)

    fun addPatch(shader: MutableShader, block: MutablePatch.() -> Unit = {}): MutablePatchSet =
        addPatch(MutablePatch(shader), block)

    fun addPatch(patch: MutablePatch, block: MutablePatch.() -> Unit = {}): MutablePatchSet {
        block.invoke(patch)
        mutablePatches.add(patch)
        return this
    }

    /** Build a [LinkedProgram] independent of an [baaahs.show.live.OpenShow]. */
    fun openForPreview(toolchain: Toolchain, resultContentType: ContentType): LinkedProgram? {
        val showBuilder = ShowBuilder()
        mutablePatches.forEach {
            showBuilder.idFor(it.build(showBuilder))
        }
        showBuilder.includeDependencyFeeds()

        val openShaders = CacheBuilder<String, OpenShader> { shaderId ->
            toolchain.openShader(showBuilder.getShaders().getBang(shaderId, "shader"))
        }

        val resolvedPatches =
            PatchResolver(openShaders, showBuilder.getPatches(), showBuilder.getFeeds(), toolchain)
                .getResolvedPatches()
        val openPatches = resolvedPatches.values.toTypedArray()
        val portDiagram = ProgramResolver.buildPortDiagram(PixelArrayDevice, *openPatches)
        return portDiagram.resolvePatch(Stream.Main, resultContentType, showBuilder.getFeeds())
    }

}

data class MutableShader(
    var title: String,
    /**language=glsl*/
    var src: String
) {
    constructor(shader: Shader) : this(shader.title, shader.src)

    fun build(): Shader {
        return Shader(title, src)
    }

    fun accept(visitor: MutableShowVisitor, log: VisitationLog = VisitationLog()) {
        if (log.shaders.add(this)) visitor.visit(this)
    }

    override fun toString(): String {
        return "MutableShader(title='$title', src='[${src.length} chars]')"
    }
}