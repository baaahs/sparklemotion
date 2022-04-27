package baaahs.app.ui

import baaahs.app.ui.editor.Editor
import baaahs.app.ui.layout.DragNDropContext
import baaahs.app.ui.layout.dragNDropContext
import baaahs.app.ui.layout.gridTabLayout
import baaahs.app.ui.layout.legacyTabLayout
import baaahs.show.LegacyTab
import baaahs.show.live.OpenGridTab
import baaahs.show.live.OpenLayout
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutableGridTab
import baaahs.show.mutable.MutableLayout
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.MutableTab
import baaahs.ui.StyleElement
import baaahs.ui.asTextNode
import baaahs.ui.sharedGlContext
import baaahs.ui.xComponent
import csstype.Flex
import csstype.number
import kotlinx.css.FlexBasis
import kotlinx.css.Position
import kotlinx.css.flex
import kotlinx.css.position
import kotlinx.js.jso
import mui.material.Tab
import mui.material.Tabs
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

val ShowLayout = xComponent<ShowLayoutProps>("ShowLayout") { props ->
    val appContext = useContext(appContext)

    val layout = props.layout
    observe(layout)

    val handleChangeTab by syntheticEventHandler<dynamic>(layout) { _, value ->
        layout.currentTabIndex = value as Int
    }

    val tabEditor = memo(props.layoutEditor, layout.currentTabIndex) {
        object : Editor<MutableTab> {
            override fun edit(mutableShow: MutableShow, block: MutableTab.() -> Unit) {
                mutableShow.editLayouts {
                    props.layoutEditor.edit(mutableShow) {
                        block(this.tabs[layout.currentTabIndex ?: error("No tab selected.")])
                    }
                }
            }
        }
    }

    val tabs = layout.tabs
    val currentTab = layout.currentTab

    val myDragNDropContext = memo<DragNDropContext>(currentTab) {
        when (currentTab) {
            is LegacyTab -> jso { this.isLegacy = true }
            is OpenGridTab -> jso { this.isLegacy = false; this.gridLayoutContext = appContext.gridLayoutContext }
            else -> error("huh?")
        }
    }

    sharedGlContext {
        attrs.inlineStyles = StyleElement {
            flex(1.0, 0.0, FlexBasis.zero)
            position = Position.relative
        }

        dragNDropContext.Provider {
            attrs.value = myDragNDropContext
            when (currentTab) {
                is LegacyTab ->
                    legacyTabLayout {
                        attrs.show = props.show
                        attrs.tab = currentTab
                        attrs.onShowStateChange = props.onShowStateChange
                    }

                is OpenGridTab ->
                    gridTabLayout {
                        attrs.tab = currentTab
                        attrs.tabEditor = tabEditor as Editor<MutableGridTab>
                        attrs.onShowStateChange = props.onShowStateChange
                    }
                null -> { +"No tabs?" }
            }
        }
    }

    if (tabs.size > 1) {
        Tabs {
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
    child(ShowLayout, handler = handler)
