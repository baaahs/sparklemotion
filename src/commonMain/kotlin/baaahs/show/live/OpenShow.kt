package baaahs.show.live

import baaahs.ShowPlayer
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.Editor
import baaahs.app.ui.editor.GridLayoutEditorPanel
import baaahs.client.document.OpenDocument
import baaahs.control.OpenButtonControl
import baaahs.getBang
import baaahs.randomId
import baaahs.show.*
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutableILayout
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.sm.webapi.Problem
import baaahs.sm.webapi.Severity
import baaahs.ui.Observable
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter

interface OpenContext {
    val allControls: List<OpenControl>
    val allPatchModDataSources: List<DataSource>

    fun findControl(id: String): OpenControl?
    fun getControl(id: String): OpenControl
    fun getDataSource(id: String): DataSource
    fun getPanel(id: String): Panel
    fun getPatch(it: String): OpenPatch
    fun release()
}

object EmptyOpenContext : OpenContext {
    override val allControls: List<OpenControl> get() = emptyList()

    override val allPatchModDataSources: List<DataSource> get() = emptyList()

    override fun findControl(id: String): OpenControl? = null

    override fun getControl(id: String): OpenControl = error("not really an open context")

    override fun getDataSource(id: String): DataSource = error("not really an open context")

    override fun getPanel(id: String): Panel = error("not really an open context")

    override fun getPatch(it: String): OpenPatch = error("not really an open context")

    override fun release() {
    }
}

class OpenShow(
    internal val show: Show,
    private val showPlayer: ShowPlayer,
    private val openContext: OpenContext,
    internal val implicitControls: List<OpenControl>,
) : OpenPatchHolder(show, openContext), RefCounted by RefCounter(), OpenDocument {
    val id = randomId("show")
    val layouts get() = show.layouts
    val openLayouts = show.layouts.open(openContext)
    val allDataSources = run {
        ShowBuilder().apply {
            val map = show.dataSources.values.associateBy { idFor(it) }.toMutableMap()
            openContext.allPatchModDataSources.forEach { dataSource ->
                map[idFor(dataSource)] = dataSource
            }
        }.getDataSources()
    }
    val allControls: List<OpenControl> get() = openContext.allControls
    val feeds = allDataSources.entries.associate { (id, dataSource) ->
        val feed = showPlayer.openFeed(id, dataSource)
        feed.use()
        dataSource to feed
    }

    val missingPlugins: Map<PluginDesc, List<String>>
        get() = run {
            val unknownDataSources = allDataSources.values
                .filterIsInstance<UnknownDataSource>()
            unknownDataSources
                .asSequence()
                .map { ds -> ds.pluginRef }
                .groupBy { it.pluginId }
                .map { (pluginId, pluginRefs) ->
                    PluginDesc(pluginId, pluginId, null, null, null) to
                            pluginRefs.map { it.resourceName }.sorted()
                }
                .sortedBy { (desc, _) -> desc.title }
                .associate { (desc, resources) -> desc to resources.distinct().sorted() }
        }

    val allProblems: List<Problem>
        get() = buildList {
            addAll(missingPlugins.map { (desc, resources) ->
                Problem(
                    "Missing plugin \"${desc.title}\".",
                    "Some things may not work properly. (${resources.joinToString()})",
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

    fun buildActivePatchSet(): ActivePatchSet {
        val items = arrayListOf<ActivePatchSet.Companion.Item>()
        var nextSerial = 0

        val builder = object : ActivePatchSet.Builder {
            override val show: OpenShow
                get() = this@OpenShow

            override fun add(patchHolder: OpenPatchHolder, depth: Int, layoutContainerId: String) {
                items.add(ActivePatchSet.Companion.Item(patchHolder, depth, layoutContainerId, nextSerial++))
            }
        }
        addTo(builder, 0)

        return ActivePatchSet(
            ActivePatchSet.sort(items),
            allDataSources,
            feeds
        )
    }

    override fun onRelease() {
        openContext.release()
        feeds.values.forEach { it.disuse() }
    }

    fun applyConstraints() {
        allControls.forEach { it.applyConstraints() }
    }

    override fun addTo(builder: ActivePatchSet.Builder, depth: Int) {
        super.addTo(builder, depth)

        openLayouts.currentFormat?.addTo(builder, depth + 1)
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

data class OpenLayouts(
    val panels: Map<String, Panel>,
    val formats: Map<String, OpenLayout>
) {
    val currentFormatId = formats.keys.firstOrNull()
    val currentFormat = formats[currentFormatId]
}

class OpenLayout(
    val mediaQuery: String?,
    val tabs: List<OpenTab>
): Observable() {
    var currentTabIndex = if (tabs.isEmpty()) null else 0
        set(value) {
            field = value
            notifyChanged()
        }

    val currentTab get() = currentTabIndex?.let { tabs[it] }

    fun addTo(builder: ActivePatchSet.Builder, depth: Int) {
        currentTab?.addTo(builder, depth + 1)
    }
}

interface OpenTab {
    val title: String

    fun addTo(builder: ActivePatchSet.Builder, depth: Int)
}

class OpenGridTab(
    override val title: String,
    override var columns: Int,
    override var rows: Int,
    override val items: List<OpenGridItem>
) : OpenTab, OpenIGridLayout {
    override fun addTo(builder: ActivePatchSet.Builder, depth: Int) {
        items.forEach { item ->
            item.control.addTo(builder, depth + 1, item.layout)
        }
    }
}

class OpenGridLayout(
    override val columns: Int,
    override val rows: Int,
    val matchParent: Boolean,
    override val items: List<OpenGridItem>
) : OpenIGridLayout

interface OpenILayout {
    fun getEditorPanel(
        editableManager: EditableManager<*>,
        layoutEditor: Editor<MutableILayout>
    ): DialogPanel?
}

interface OpenIGridLayout : OpenILayout {
    val columns: Int
    val rows: Int
    val items: List<OpenGridItem>

    override fun getEditorPanel(editableManager: EditableManager<*>, layoutEditor: Editor<MutableILayout>) =
        GridLayoutEditorPanel(editableManager, this, layoutEditor as Editor<MutableIGridLayout>)
}

class OpenGridItem(
    val control: OpenControl,
    val column: Int,
    val row: Int,
    val width: Int,
    val height: Int,
    val layout: OpenGridLayout?
)