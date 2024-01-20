package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.scene.MutableScene
import baaahs.ui.typographyH4
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import js.objects.jso
import mui.material.*
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val FixtureConfigurerView = xComponent<FixtureConfigurerProps>("FixtureConfigurer") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = appContext.sceneEditorClient
    observe(sceneEditorClient)

    val styles = appContext.allStyles.controllerEditor

    Paper {
        attrs.classes = jso { this.root = -styles.editorPanes }
        typographyH4 { +"Fixtures" }

        Table {
            attrs.classes = jso { this.root = -styles.controllersTable }
            attrs.stickyHeader = true

            TableHead {
                TableRow {
                    TableCell { +"Name" }
                    TableCell { +"Model Entity" }
                    TableCell { +"Controller" }
                    TableCell { +"Config" }
                    TableCell { +"Pixels" }
                }
            }

            TableBody {
                sceneEditorClient.fixtures
                    .sortedBy { it.name }
                    .forEach { fixtureInfo ->
                        TableRow {
                            TableCell { +fixtureInfo.name }
                            TableCell { +(fixtureInfo.entityId ?: "Anonymous") }
                            TableCell { +fixtureInfo.controllerId.name() }
                            TableCell { +fixtureInfo.transportConfig.toString() }
                            TableCell { +"?" }
//                            TableCell { +fixtureInfo.pixelCount.toString() }
//                            TableCell { +fixtureInfo.mappedPixelCount.toString() }
//                            TableCell { +fixtureInfo.status.toString() }
                        }
                    }
            }
        }
    }
}

external interface FixtureConfigurerProps : Props {
    var mutableScene: MutableScene
    var onEdit: () -> Unit
}

fun RBuilder.fixtureConfigurer(handler: RHandler<FixtureConfigurerProps>) =
    child(FixtureConfigurerView, handler = handler)
