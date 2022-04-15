package baaahs.app.ui

import baaahs.app.ui.editor.Editor
import baaahs.app.ui.layout.gridTabLayout
import baaahs.app.ui.layout.legacyTabLayout
import baaahs.show.LegacyTab
import baaahs.show.live.*
import baaahs.show.mutable.MutableGridTab
import baaahs.show.mutable.MutableLayout
import baaahs.show.mutable.MutableShow
import baaahs.ui.sharedGlContext
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler

val ShowLayout = xComponent<ShowLayoutProps>("ShowLayout") { props ->
    var currentTabIndex by state { 0 }
    val currentTab = props.layout.tabs.getBounded(currentTabIndex)

    val tabEditor = memo(props.layoutEditor, currentTabIndex) {
        object : Editor<MutableGridTab> {
            override fun edit(mutableShow: MutableShow, block: MutableGridTab.() -> Unit) {
                mutableShow.editLayouts {
                    props.layoutEditor.edit(mutableShow) {
                        block(tabs[currentTabIndex] as MutableGridTab)
                    }
                }
            }
        }
    }

    sharedGlContext {
        when (currentTab) {
            is LegacyTab ->
                legacyTabLayout {
                    attrs.show = props.show
                    attrs.tab = currentTab
                    attrs.controlDisplay = props.controlDisplay
                    attrs.controlProps = props.controlProps
                    attrs.editMode = props.editMode
                }
            is OpenGridTab ->
                gridTabLayout {
                    attrs.tab = currentTab
                    attrs.controlProps = props.controlProps
                    attrs.editMode = props.editMode
                    attrs.tabEditor = tabEditor
                }
            null -> { +"No tabs?" }
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
    var onShowStateChange: () -> Unit
    var layout: OpenLayout
    var controlDisplay: ControlDisplay
    var controlProps: ControlProps
    var editMode: Boolean?
    var layoutEditor: Editor<MutableLayout>
}

fun RBuilder.showLayout(handler: RHandler<ShowLayoutProps>) =
    child(ShowLayout, handler = handler)
