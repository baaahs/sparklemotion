package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.ui.xComponent
import materialui.components.paper.paper
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import materialui.components.typography.typographyH4
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val FixtureConfigurerView = xComponent<FixtureConfigurerProps>("FixtureConfigurer") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = appContext.sceneEditorClient
    observe(sceneEditorClient)

    paper {
        typographyH4 { +"Fixtures" }

        table {
            tableHead {
                tableRow {
                    thCell { +"Name" }
                    thCell { +"Model Entity" }
                    thCell { +"Controller" }
                    thCell { +"Config" }
                    thCell { +"Pixels" }
                }
            }

            tableBody {
                sceneEditorClient.fixtures
                    .sortedBy { it.name }
                    .forEach { fixtureInfo ->
                        tableRow {
                            tdCell { +fixtureInfo.name }
                            tdCell { +(fixtureInfo.entityId ?: "Anonymous") }
                            tdCell { +fixtureInfo.controllerId.shortName() }
                            tdCell { +fixtureInfo.transportConfig.toString() }
                            tdCell { +"?" }
//                            tdCell { +fixtureInfo.pixelCount.toString() }
//                            tdCell { +fixtureInfo.mappedPixelCount.toString() }
//                            tdCell { +fixtureInfo.status.toString() }
                        }
                    }
            }
        }
    }
}

external interface FixtureConfigurerProps : Props {
}

fun RBuilder.fixtureConfigurer(handler: RHandler<FixtureConfigurerProps>) =
    child(FixtureConfigurerView, handler = handler)
