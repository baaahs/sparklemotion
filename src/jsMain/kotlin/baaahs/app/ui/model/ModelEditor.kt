package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.model.Model
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import baaahs.visualizer.Visualizer
import kotlinx.css.*
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.styles.muitheme.MuiTheme
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.header
import react.useContext
import styled.StyleSheet

private val ModelEditorView = xComponent<ModelEditorProps>("ModelEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

//    val model = appContext.sceneManager.scene?.model
    var model by state<Model?> { null }
    globalLaunch { model = appContext.webClient.modelProvider.getModel() }

    val visualizer = memo { Visualizer(appContext.webClient.modelProvider, appContext.clock) }
    val visualizerEl = ref<Element>()

    onMount {
        visualizer.facade.container = visualizerEl.current as HTMLDivElement

        withCleanup {
            visualizer.facade.container = null
        }
    }


    div(+styles.editorPanes) {
        div(+styles.navigatorPane) {
            header { +"Navigator" }

            list {
                model?.allEntities?.forEach { entity ->
                    listItem { +entity.name }
                }
            }
        }

        div(+styles.visualizerPane) {
            div(+styles.visualizer) {
                ref = visualizerEl
            }
        }

        div(+styles.propertiesPane) {
            header { +"Properties" }
        }
    }
}
class ModelEditorStyles(val theme: MuiTheme) : StyleSheet("app-model", isStatic = true) {
    val editorPanes by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns(
            GridAutoRows(20.pct),
            GridAutoRows(60.pct),
            GridAutoRows(20.pct)
        )
        height = 100.pct
    }

    val navigatorPane by css {}

    val visualizerPane by css {
        position = Position.relative
    }

    val visualizer by css {
        position = Position.absolute
        top = 0.px
        left = 0.px
        bottom = 0.px
        right = 0.px
    }

    val propertiesPane by css {}
}

private enum class ModelEditorWindows {
    Entities, Visualizer, Properties
}

external interface ModelEditorProps : Props {
}

fun RBuilder.modelEditor(handler: RHandler<ModelEditorProps>) =
    child(ModelEditorView, handler = handler)