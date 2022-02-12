package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.ui.xComponent
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
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

private val ControllerConfigurerView = xComponent<DeviceConfigurerProps>("ControllerConfigurer") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = appContext.sceneEditorClient
    observe(sceneEditorClient)

    paper {
        appContext.plugins.controllers
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
                sceneEditorClient.brains.values
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
                sceneEditorClient.dmxDevices.values
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
                sceneEditorClient.sacnDevices.values
                    .sortedBy { it.id }
                    .forEach { wledDevice ->
                        tableRow {
                            tdCell { +wledDevice.id }
                            tdCell { +"—" }
                            tdCell { +"sACN" }
                            tdCell { +"—" }
                            tdCell { +"—" }
                            tdCell { +"—" }
                            tdCell {
                                if (wledDevice.onlineSince != null) {
                                    val onlineSince = DateTime(wledDevice.onlineSince * 1000)
                                        .toString(DateFormat.FORMAT1)
                                    +"Online since $onlineSince"
                                } else {
                                    +"Offline"
                                }
                            }
                        }
                    }
            }
        }
    }
}

external interface DeviceConfigurerProps : Props {
}

fun RBuilder.deviceConfigurer(handler: RHandler<DeviceConfigurerProps>) =
    child(ControllerConfigurerView, handler = handler)