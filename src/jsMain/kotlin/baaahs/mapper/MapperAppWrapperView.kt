package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.client.SceneEditorClient
import baaahs.ui.xComponent
import js.objects.jso
import mui.material.styles.Theme
import mui.material.styles.useTheme
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val MapperAppWrapperView = xComponent<MapperAppWrapperProps>("MapperAppWrapper") { props ->
    val appContext = useContext(appContext)
    val theme = useTheme<Theme>()

    val myAppContext = memo(theme) {
        jso<MapperAppContext> {
            this.sceneEditorClient = props.sceneEditorClient
            this.plugins = plugins
            this.allStyles = appContext.allStyles
            this.keyboard = appContext.keyboard
            this.clock = clock
        }
    }

    mapperAppContext.Provider {
        attrs.value = myAppContext

        mapperApp {
            attrs.mapperBuilder = props.mapperBuilder
        }
    }
}

external interface MapperAppWrapperProps : Props {
    var sceneEditorClient: SceneEditorClient.Facade
    var mapperBuilder: JsMapperBuilder
}

fun RBuilder.mapperAppWrapper(handler: RHandler<MapperAppWrapperProps>) =
    child(MapperAppWrapperView, handler = handler)