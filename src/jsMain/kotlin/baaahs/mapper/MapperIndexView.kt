package baaahs.mapper

import baaahs.JsMapperUi
import baaahs.admin.AdminClient
import baaahs.app.ui.AllStyles
import baaahs.app.ui.Styles
import baaahs.client.WebClient
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.JsClock
import kotlinext.js.jsObject
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
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.div

private enum class PageTabs {
    Controllers,
    Fixtures,
    Surface_Mapping
}

val MapperIndexView = xComponent<MapperIndexViewProps>("MapperIndexView") { props ->
    val theme = memo {
        createMuiTheme {
            palette { type = PaletteType.dark }
        }
    }

    val clock = memo { JsClock }
    val plugins = memo { WebClient.createPlugins() }

    val myAppContext = memo(theme) {
        jsObject<MapperAppContext> {
            this.adminClient = props.adminClient
            this.plugins = plugins
            this.allStyles = AllStyles(theme)
            this.clock = clock
        }
    }

    var selectedTab by state { PageTabs.Surface_Mapping }
    val handleChangeTab by handler { _: Event, tab: PageTabs ->
        selectedTab = tab
    }

    mapperAppContext.Provider {
        attrs.value = myAppContext

        themeProvider(theme) {
            cssBaseline {}

            div(+Styles.root) {
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
        attrs.hidden = !isCurrent
        if (isCurrent) block()
    }
}

external interface MapperIndexViewProps : RProps {
    var adminClient: AdminClient.Facade
    var mapperUi: JsMapperUi
}

fun RBuilder.mapperIndex(handler: RHandler<MapperIndexViewProps>) =
    child(MapperIndexView, handler = handler)