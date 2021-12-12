package baaahs.show.live

import baaahs.ShowPlayer
import baaahs.control.OpenButtonControl
import baaahs.getBang
import baaahs.randomId
import baaahs.show.*
import baaahs.show.mutable.MutableShow
import baaahs.sm.webapi.Severity
import baaahs.sm.webapi.ShowProblem
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter

interface OpenContext {
    val allControls: List<OpenControl>

    fun findControl(id: String): OpenControl?
    fun getControl(id: String): OpenControl
    fun getDataSource(id: String): DataSource
    fun getPanel(id: String): Panel
    fun getShaderInstance(it: String): LiveShaderInstance
    fun release()
}

object EmptyOpenContext : OpenContext {
    override val allControls: List<OpenControl> get() = emptyList()

    override fun findControl(id: String): OpenControl? = null

    override fun getControl(id: String): OpenControl = error("not really an open context")

    override fun getDataSource(id: String): DataSource = error("not really an open context")

    override fun getPanel(id: String): Panel = error("not really an open context")

    override fun getShaderInstance(it: String): LiveShaderInstance = error("not really an open context")

    override fun release() {
    }
}

class OpenShow(
    internal val show: Show,
    private val showPlayer: ShowPlayer,
    private val openContext: OpenContext,
    internal val implicitControls: List<OpenControl>,
) : OpenPatchHolder(show, openContext), RefCounted by RefCounter() {
    val id = randomId("show")
    val layouts get() = show.layouts
    val allDataSources = show.dataSources
    val allControls: List<OpenControl> get() = openContext.allControls
    val feeds = allDataSources.entries.associate { (id, dataSource) ->
        val feed = showPlayer.openFeed(id, dataSource)
        feed.use()
        dataSource to feed
    }

    val missingPlugins: List<PluginDesc>
        get() = run {
            val unknownDataSources = allDataSources.values
                .filterIsInstance<UnknownDataSource>()
            unknownDataSources
                .map { ds -> ds.pluginRef.pluginId }
                .distinct()
                .map { pluginId ->
                    PluginDesc(pluginId, pluginId, null, null, null)
                }
                .sortedBy { it.title }
        }

    val allProblems: List<ShowProblem>
        get() = buildList {
            addAll(missingPlugins.map { desc ->
                ShowProblem(
                    "Missing plugin \"${desc.title}\".",
                    "Some things may not work properly.",
                    severity = Severity.WARN
                )
            })

            object : OpenShowVisitor() {
                override fun visitPatchHolder(openPatchHolder: OpenPatchHolder) {
                    addAll(openPatchHolder.problems)
                    super.visitPatchHolder(openPatchHolder)
                }
            }.apply { visitShow(this@OpenShow) }
        }

    fun getPanel(id: String) = show.layouts.panels.getBang(id, "panel")

    /**
     * Don't hold on to MutableShows; create them, apply changes, and commit or abandon them promptly!
     */
    fun edit(block: MutableShow.() -> Unit = {}): MutableShow =
        MutableShow(show).apply(block)

    fun buildActivePatchSet(): ActivePatchSet =
        ActivePatchSet.build(this, allDataSources, feeds)

    override fun onRelease() {
        openContext.release()
        feeds.values.forEach { it.disuse() }
    }

    fun applyConstraints() {
        allControls.forEach { it.applyConstraints() }
    }

    fun getEnabledSwitchState(): Set<String> {
        return allControls
            .filterIsInstance<OpenButtonControl>()
            .mapNotNull { if (it.isPressed) it.id else null }
            .toSet()
    }

    fun getShowState(): ShowState {
        return ShowState(
            allControls.mapNotNull { control ->
                control.getState()?.let { control.id to it }
            }.associate { it }
        )
    }

    fun applyState(showState: ShowState) {
        showState.controls.forEach { (id, state) ->
            val control = openContext.findControl(id)
            if (control != null) {
                control.applyState(state)
            } else {
                logger.debug { "Can't apply state to unknown control \"$id\"" }
            }
        }
    }

    companion object {
        private val logger = Logger("OpenShow")
    }
}

abstract class OpenShowVisitor {
    open fun visitShow(openShow: OpenShow) {
        visitPatchHolder(openShow)

        openShow.implicitControls.forEach { visitUnplacedControl(it) }
    }

    open fun visitPatchHolder(openPatchHolder: OpenPatchHolder) {
        openPatchHolder.patches.forEach {
            visitPatch(it)
        }

        openPatchHolder.controlLayout.forEach { (panel, openControls) ->
            openControls.forEach { openControl ->
                visitPlacedControl(panel, openControl)
            }
        }
    }

    open fun visitPlacedControl(panel: Panel, openControl: OpenControl) {
        visitControl(openControl)
    }

    open fun visitUnplacedControl(openControl: OpenControl) {
        visitControl(openControl)
    }

    open fun visitButtonGroupButton(controlContainer: ControlContainer, openControl: OpenControl) {
        visitControl(openControl)
    }

    open fun visitControl(openControl: OpenControl) {
        if (openControl is OpenPatchHolder) {
            visitPatchHolder(openControl)
        }

        if (openControl is ControlContainer) {
            visitControlContainer(openControl)
        }
    }

    open fun visitControlContainer(controlContainer: ControlContainer) {
        controlContainer.containedControls().forEach { containedControl ->
            visitButtonGroupButton(controlContainer, containedControl)
        }
    }

    open fun visitPatch(openPatch: OpenPatch) {
    }
}