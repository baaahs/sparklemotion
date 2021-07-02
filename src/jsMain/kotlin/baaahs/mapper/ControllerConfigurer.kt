package baaahs.mapper

import baaahs.ui.xComponent
import materialui.components.paper.paper
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import materialui.components.typography.typographyH4
import react.*

private val ControllerConfigurer = xComponent<DeviceConfigurerProps>("ControllerConfigurer") { props ->
    val adminContext = useContext(mapperAppContext)
    val adminClient = adminContext.adminClient
    observe(adminClient)

    paper {
        typographyH4 { +"Brains" }

        table {
            tableHead {
                thCell { +"ID" }
                thCell { +"Address" }
                thCell { +"Model Element" }
                thCell { +"Pixels" }
                thCell { +"Mapped" }
                thCell { +"Status" }
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
                thCell { +"Interface" }         // DMX Dongle, sACN
                thCell { +"Universe" }          // int, for sACN only
                thCell { +"Device" }            // "Light Bar 1", "Left Sharpy"
                thCell { +"Type" }              // PixelArrayDevice, MovingHeadDevice, LightBarDevice
                thCell { +"DMX Base Channel" }  // 1
                thCell { +"Model Element" }     // "Light Bar 1", "Left Eye"
                thCell { +"Status" }
            }

            tableBody {
                adminClient.dmxDevices.values
                    .sortedBy { it.id }
                    .forEach { dmxInfo ->
                        tableRow {
                            thCell { +dmxInfo.id }
                            thCell { +(dmxInfo.universe?.toString() ?: "?") }
                            thCell { +"DMX" }
                            thCell { +dmxInfo.type }
                            thCell { +"—" }
                            thCell { +"—" }
                            thCell { +"—" }
                        }
                    }
            }

            tableBody {
                adminClient.wledDevices.values
                    .sortedBy { it.id }
                    .forEach { wledDevice ->
                        tableRow {
                            thCell { +wledDevice.id }
                            thCell { +"—" }
                            thCell { +"sACN" }
                            thCell { +"—" }
                            thCell { +"—" }
                            thCell { +"—" }
                            thCell { +"Online since ${wledDevice.onlineSince}" }
                        }
                    }
            }
        }
    }
}

external interface DeviceConfigurerProps : RProps {
}

fun RBuilder.deviceConfigurer(handler: RHandler<DeviceConfigurerProps>) =
    child(ControllerConfigurer, handler = handler)