package baaahs.mapper

import baaahs.app.ui.Styles
import baaahs.app.ui.appContext
import baaahs.app.ui.model.modelEditor
import baaahs.client.SceneEditorClient
import baaahs.client.document.SceneManager
import baaahs.ui.muiClasses
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.JsPlatform
import kotlinx.html.hidden
import mui.material.Tab
import mui.material.TabClasses
import mui.material.Tabs
import mui.material.TabsClasses
import react.*
import react.dom.div

private enum class PageTabs(val title: String) {
    Controllers("Controllers"),
    Model("Model"),
    //    Fixtures("Fixtures"),
    Mapping("Pixel Mapping")
}

val SceneEditorView = xComponent<SceneEditorViewProps>("SceneEditorView") { props ->
    val appContext = useContext(appContext)
    val themeStyles = appContext.allStyles.appUi

    observe(props.sceneManager)

    var selectedTab by state { PageTabs.entries.first() }
    val handleChangeTab by syntheticEventHandler { _, tab: PageTabs ->
        selectedTab = tab
    }

    val handleEdit by handler {
        props.sceneManager.onEdit()
    }
    val mutableScene = props.sceneManager.mutableScene

    div(+Styles.sceneEditorRoot) {
        Tabs {
            attrs.classes = muiClasses<TabsClasses> {
                root = -themeStyles.sceneEditorTabs
            }
            attrs.value = selectedTab
            attrs.onChange = handleChangeTab

            for (tab in PageTabs.entries) {
                if (tab == PageTabs.Mapping && JsPlatform.isNative)
                    continue

                Tab {
                    attrs.classes = muiClasses<TabClasses> {
                        selected = -themeStyles.sceneEditorTabSelected
                    }
                    attrs.label = buildElement { +tab.title }
                    attrs.value = tab
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
            controllerList {
                attrs.mutableScene = mutableScene
                attrs.onEdit = handleEdit
            }
        }

//        tabPanel(PageTabs.Fixtures, selectedTab) {
//            fixtureConfigurer {
//                attrs.mutableScene = mutableScene
//                attrs.onEdit = handleEdit
//            }
//        }

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

    div(+Styles.sceneEditorTabPanel) {
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