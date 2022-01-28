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

private val ControllerConfigurer = xComponent<DeviceConfigurerProps>("ControllerConfigurer") { props ->
    val appContext = useContext(appContext)
    val adminClient = appContext.sceneEditorClient
    observe(adminClient)

    paper {
        typographyH4 { +"Brains" }

        table {
            tableHead {
                tableRow {
                    thCell { +"ID" }
                    thCell { +"Address" }
                    thCell { +"Model Element" }
                    thCell { +"Pixels" }
                    thCell { +"Mapped" }
                    thCell { +"Status" }
                }
            }

            tableBody {
                adminClient.brains.values
                    .sortedBy { it.id.uuid }
                    .forEach { brainData ->
                        tableRow {
                            tdCell { +brainData.id.uuid }
                            tdCell { +brainData.address }
                            tdCell { +(brainData.modelEntity ?: "Anonymous") }
                            tdCell { +brainData.pixelCount.toString() }
                            tdCell { +brainData.mappedPixelCount.toString() }
                            tdCell { +brainData.status.toString() }
                        }
                    }
            }
        }

        typographyH4 { +"DMX Interfaces" }

        table {
            tableHead {
                tableRow {
                    thCell { +"Interface" }         // DMX Dongle, sACN
                    thCell { +"Universe" }          // int, for sACN only
                    thCell { +"Device" }            // "Light Bar 1", "Left Sharpy"
                    thCell { +"Type" }              // PixelArrayDevice, MovingHeadDevice, LightBarDevice
                    thCell { +"DMX Base Channel" }  // 1
                    thCell { +"Model Element" }     // "Light Bar 1", "Left Eye"
                    thCell { +"Status" }
                }
            }

            tableBody {
                adminClient.dmxDevices.values
                    .sortedBy { it.id }
                    .forEach { dmxInfo ->
                        tableRow {
                            tdCell { +dmxInfo.id }
                            tdCell { +(dmxInfo.universe?.toString() ?: "?") }
                            tdCell { +"DMX" }
                            tdCell { +dmxInfo.type }
                            tdCell { +"—" }
                            tdCell { +"—" }
                            tdCell { +"—" }
                        }
                    }
            }

            tableBody {
                adminClient.sacnDevices.values
                    .sortedBy { it.id }
                    .forEach { wledDevice ->
                        tableRow {
                            tdCell { +wledDevice.id }
                            tdCell { +"—" }
                            tdCell { +"sACN" }
                            tdCell { +"—" }
                            tdCell { +"—" }
                            tdCell { +"—" }
                            tdCell { +"Online since ${wledDevice.onlineSince}" }
                        }
                    }
            }
        }
    }
}

external interface DeviceConfigurerProps : Props {
}

fun RBuilder.deviceConfigurer(handler: RHandler<DeviceConfigurerProps>) =
    child(ControllerConfigurer, handler = handler)