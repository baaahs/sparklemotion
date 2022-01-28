package baaahs.mapper

import baaahs.app.ui.Styles
import baaahs.app.ui.model.modelEditor
import baaahs.client.SceneEditorClient
import baaahs.client.document.SceneManager
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.JsClock
import kotlinx.html.hidden
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarPosition
import materialui.components.tab.tab
import materialui.components.tabs.tabs
import materialui.styles.createMuiTheme
import materialui.styles.muitheme.options.palette
import materialui.styles.palette.PaletteType
import materialui.styles.palette.options.type
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import styled.inlineStyles

private enum class PageTabs {
    Model,
    Controllers,
    Fixtures,
    Surface_Mapping
}

val SceneEditorView = xComponent<SceneEditorViewProps>("SceneEditorView") { props ->
    val theme = memo {
        createMuiTheme {
            palette { type = PaletteType.dark }
        }
    }

    observe(props.sceneManager)
    val clock = memo { JsClock }
    val plugins = props.sceneEditorClient.plugins

    var selectedTab by state { PageTabs.Model }
    val handleChangeTab by handler { _: Event, tab: PageTabs ->
        selectedTab = tab
    }

    val handleEdit by handler {
        props.sceneManager.onEdit()
    }
    val mutableScene = props.sceneManager.mutableScene

    div(+Styles.adminRoot) {
        appBar {
            attrs.position = AppBarPosition.relative

            tabs {
                attrs.value = selectedTab
                attrs.onChange = handleChangeTab.asDynamic()

                PageTabs.values().forEach { tab ->
                    tab {
                        attrs.label { +tab.name.replace("_", " ") }
                        attrs.value = tab.asDynamic()
                    }
                }
            }
        }

        tabPanel(PageTabs.Model, selectedTab) {
            modelEditor {
                attrs.mutableScene = mutableScene
                attrs.onEdit = handleEdit
            }
        }

        tabPanel(PageTabs.Controllers, selectedTab) {
            deviceConfigurer {}
        }

        tabPanel(PageTabs.Fixtures, selectedTab) {
            fixtureConfigurer {}
        }

        tabPanel(PageTabs.Surface_Mapping, selectedTab) {
            mapperAppWrapper {
                attrs.sceneEditorClient = props.sceneEditorClient
                attrs.mapperUi = props.mapperUi
            }
        }
    }
}

private fun RBuilder.tabPanel(tab: PageTabs, selectedTab: PageTabs, block: RBuilder.() -> Unit) {
    val isCurrent = tab == selectedTab

    div(+Styles.adminTabPanel) {
        inlineStyles {
//            minHeight = 0.px
//            flex(1.0, 0.0)
        }

        attrs.hidden = !isCurrent
        if (isCurrent) block()
    }
}

external interface SceneEditorViewProps : Props {
    var sceneEditorClient: SceneEditorClient.Facade
    var mapperUi: JsMapperUi
    var sceneManager: SceneManager.Facade
}

fun RBuilder.sceneEditor(handler: RHandler<SceneEditorViewProps>) =
    child(SceneEditorView, handler = handler)