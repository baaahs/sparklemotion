package baaahs.mapper

import baaahs.app.ui.AllStyles
import baaahs.app.ui.Styles
import baaahs.app.ui.appContext
import baaahs.app.ui.model.modelEditor
import baaahs.client.SceneEditorClient
import baaahs.scene.OpenScene
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.JsClock
import baaahs.util.globalLaunch
import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.html.hidden
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarPosition
import materialui.components.cssbaseline.cssBaseline
import materialui.components.tab.tab
import materialui.components.tabs.tabs
import materialui.styles.createMuiTheme
import materialui.styles.muitheme.options.palette
import materialui.styles.palette.PaletteType
import materialui.styles.palette.options.type
import materialui.styles.themeprovider.themeProvider
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.inlineStyles

private enum class PageTabs {
    Model,
    Controllers,
    Fixtures,
    Surface_Mapping
}

val SceneEditorView = xComponent<SceneEditorViewProps>("SceneEditorView") { props ->
    val appContext = useContext(appContext)
    val theme = memo {
        createMuiTheme {
            palette { type = PaletteType.dark }
        }
    }

    val clock = memo { JsClock }
    val plugins = props.sceneEditorClient.plugins

    val myAppContext = memo(theme) {
        jsObject<MapperAppContext> {
            this.sceneEditorClient = props.sceneEditorClient
            this.plugins = plugins
            this.allStyles = AllStyles(theme)
            this.clock = clock
        }
    }

    var selectedTab by state { PageTabs.Model }
    val handleChangeTab by handler { _: Event, tab: PageTabs ->
        selectedTab = tab
    }

    var scene by state<OpenScene?> { null }
    onMount {
        globalLaunch {
            scene = appContext.sceneManager.getScene()
        }
    }

    val openScene = scene ?: return@xComponent

    mapperAppContext.Provider {
        attrs.value = myAppContext

        themeProvider(theme) {
            cssBaseline {}

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
                        attrs.scene = openScene
                    }
                }

                tabPanel(PageTabs.Controllers, selectedTab) {
                    deviceConfigurer {}
                }

                tabPanel(PageTabs.Fixtures, selectedTab) {
                }

                tabPanel(PageTabs.Surface_Mapping, selectedTab) {
                    mapperApp {
                        attrs.mapperUi = props.mapperUi
                    }
                }
            }
        }
    }
}

private fun RBuilder.tabPanel(tab: PageTabs, selectedTab: PageTabs, block: RBuilder.() -> Unit) {
    val isCurrent = tab == selectedTab

    div {
        inlineStyles {
            height = 0.px
            overflow = Overflow.scroll
            flex(1.0, 0.0)
        }

        attrs.hidden = !isCurrent
        if (isCurrent) block()
    }
}

external interface SceneEditorViewProps : Props {
    var sceneEditorClient: SceneEditorClient.Facade
    var mapperUi: JsMapperUi
}

fun RBuilder.sceneEditor(handler: RHandler<SceneEditorViewProps>) =
    child(SceneEditorView, handler = handler)