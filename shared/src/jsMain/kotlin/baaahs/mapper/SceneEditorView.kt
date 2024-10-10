package baaahs.mapper

import baaahs.app.ui.Styles
import baaahs.app.ui.model.modelEditor
import baaahs.client.SceneEditorClient
import baaahs.client.document.SceneManager
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.hidden
import mui.material.AppBar
import mui.material.AppBarPosition
import mui.material.Tab
import mui.material.Tabs
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.dom.div
import styled.inlineStyles

private enum class PageTabs {
    Model,
    Controllers,
    Fixtures,
    Mapping
}

val SceneEditorView = xComponent<SceneEditorViewProps>("SceneEditorView") { props ->
    observe(props.sceneManager)

    var selectedTab by state { PageTabs.Model }
    val handleChangeTab by handler { _: Event, tab: PageTabs ->
        selectedTab = tab
    }

    val handleEdit by handler {
        props.sceneManager.onEdit()
    }
    val mutableScene = props.sceneManager.mutableScene

    div(+Styles.adminRoot) {
        AppBar {
            attrs.position = AppBarPosition.relative

            Tabs {
                attrs.value = selectedTab
                attrs.onChange = handleChangeTab.asDynamic()

                PageTabs.values().forEach { tab ->
                    Tab {
                        attrs.label = buildElement { +tab.name.replace("_", " ") }
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
            deviceConfigurer {
                attrs.mutableScene = mutableScene
                attrs.onEdit = handleEdit
            }
        }

        tabPanel(PageTabs.Fixtures, selectedTab) {
            fixtureConfigurer {
                attrs.mutableScene = mutableScene
                attrs.onEdit = handleEdit
            }
        }

        tabPanel(PageTabs.Mapping, selectedTab) {
            mapperAppWrapper {
                attrs.sceneEditorClient = props.sceneEditorClient
                attrs.mapperBuilder = props.mapperBuilder
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
    var sceneManager: SceneManager.Facade
    var mapperBuilder: JsMapperBuilder
}

fun RBuilder.sceneEditor(handler: RHandler<SceneEditorViewProps>) =
    child(SceneEditorView, handler = handler)