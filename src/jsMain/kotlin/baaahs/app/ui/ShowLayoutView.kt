package baaahs.app.ui

import baaahs.app.ui.editor.Editor
import baaahs.app.ui.editor.layout.legacyLayoutWarning
import baaahs.app.ui.layout.DragNDropContext
import baaahs.app.ui.layout.dragNDropContext
import baaahs.app.ui.layout.gridTabLayout
import baaahs.show.LegacyTab
import baaahs.show.live.OpenGridTab
import baaahs.show.live.OpenLayout
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutableGridTab
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutableLayout
import baaahs.show.mutable.MutableShow
import baaahs.ui.*
import js.core.jso
import kotlinx.css.FlexBasis
import kotlinx.css.Position
import kotlinx.css.flex
import kotlinx.css.position
import mui.material.Tab
import mui.material.Tabs
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext
import web.cssom.Flex
import web.cssom.number

private val ShowLayoutView = xComponent<ShowLayoutProps>("ShowLayout") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.appUi

    val layout = props.layout
    observe(layout)

    val handleChangeTab by syntheticEventHandler<dynamic>(layout) { _, value ->
        layout.currentTabIndex = value as Int
    }

    val tabEditor = memo(props.layoutEditor, layout.currentTabIndex) {
        object : Editor<MutableIGridLayout> {
            override val title: String = "Tab editor (${layout.currentTab?.title})"

            override fun edit(mutableShow: MutableShow, block: MutableIGridLayout.() -> Unit) {
                mutableShow.editLayouts {
                    props.layoutEditor.edit(mutableShow) {
                        block(tabs[layout.currentTabIndex ?: error("No tab selected.")] as MutableGridTab)
                    }
                }
            }

            override fun delete(mutableShow: MutableShow) {
                mutableShow.editLayouts {
                    props.layoutEditor.edit(mutableShow) {
                        tabs.removeAt(layout.currentTabIndex ?: error("No tab selected."))
                    }
                }
            }
        }
    }

    val tabs = layout.tabs
    val currentTab = layout.currentTab

    val myDragNDropContext = memo<DragNDropContext>(currentTab) {
        jso { this.gridLayoutContext = appContext.gridLayoutContext }
    }

    sharedGlContext {
        attrs.inlineStyles = StyleElement {
            flex = kotlinx.css.Flex(1.0, 0.0, FlexBasis.zero)
            position = Position.relative
        }

        dragNDropContext.Provider {
            attrs.value = myDragNDropContext

            when (currentTab) {
                is LegacyTab ->
                    legacyLayoutWarning {}
                is OpenGridTab ->
                    gridTabLayout {
                        attrs.tab = currentTab
                        attrs.tabEditor = tabEditor
                        attrs.onShowStateChange = props.onShowStateChange
                    }
                null -> { +"No tabs?" }
            }
        }
    }

    if (tabs.size > 1) {
        Tabs {
            attrs.classes = jso { this.root = -styles.showTabs }
            attrs.value = layout.currentTabIndex
            attrs.onChange = handleChangeTab
            tabs.forEachIndexed { index, tab ->
                Tab {
                    attrs.value = index
                    attrs.label = tab.title.asTextNode()
                }
            }

            attrs.sx {
                flex = Flex(number(0.0), number(0.0))
            }
        }
    }
}

private fun <E> List<E>.getBounded(index: Int): E? {
    if (size == 0) return null
    if (index > size) return get(size - 1)
    return get(index)
}

external interface ShowLayoutProps : Props {
    var show: OpenShow
    var layout: OpenLayout
    var layoutEditor: Editor<MutableLayout>
    var onShowStateChange: () -> Unit
}

fun RBuilder.showLayout(handler: RHandler<ShowLayoutProps>) =
    child(ShowLayoutView, handler = handler)
