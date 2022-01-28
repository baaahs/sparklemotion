package baaahs.mapper

import baaahs.app.ui.AllStyles
import baaahs.client.SceneEditorClient
import baaahs.ui.xComponent
import kotlinext.js.jsObject
import materialui.useTheme
import react.Props
import react.RBuilder
import react.RHandler

private val MapperAppWrapperView = xComponent<MapperAppWrapperProps>("MapperAppWrapper") { props ->
    val theme = useTheme()

    val myAppContext = memo(theme) {
        jsObject<MapperAppContext> {
            this.sceneEditorClient = props.sceneEditorClient
            this.plugins = plugins
            this.allStyles = AllStyles(theme)
            this.clock = clock
        }
    }

    mapperAppContext.Provider {
        attrs.value = myAppContext

        mapperApp {
            attrs.mapperUi = props.mapperUi
        }
    }
}

external interface MapperAppWrapperProps : Props {
    var sceneEditorClient: SceneEditorClient.Facade
    var mapperUi: JsMapperUi
}

fun RBuilder.mapperAppWrapper(handler: RHandler<MapperAppWrapperProps>) =
    child(MapperAppWrapperView, handler = handler)