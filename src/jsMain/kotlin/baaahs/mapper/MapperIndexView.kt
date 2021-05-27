package baaahs.mapper

import baaahs.JsMapperUi
import baaahs.app.ui.AllStyles
import baaahs.client.WebClient
import baaahs.ui.xComponent
import baaahs.util.JsClock
import kotlinext.js.jsObject
import materialui.components.cssbaseline.cssBaseline
import materialui.styles.createMuiTheme
import materialui.styles.muitheme.options.palette
import materialui.styles.palette.PaletteType
import materialui.styles.palette.options.type
import materialui.styles.themeprovider.themeProvider
import react.RBuilder
import react.RHandler
import react.RProps
import react.child

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
            this.plugins = plugins
            this.allStyles = AllStyles(theme)
            this.clock = clock
        }
    }

    mapperAppContext.Provider {
        attrs.value = myAppContext

        themeProvider(theme) {
            cssBaseline {}

            mapperApp {
                attrs.mapperUi = props.mapperUi
            }
        }
    }

}

external interface MapperIndexViewProps : RProps {
    var mapperUi: JsMapperUi
}

fun RBuilder.mapperIndex(handler: RHandler<MapperIndexViewProps>) =
    child(MapperIndexView, handler = handler)