package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.controller.ControllerId
import baaahs.controller.ControllerMatcher
import baaahs.controller.SacnManager
import baaahs.scene.MutableScene
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import external.searchbar.SearchBar
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonStyle
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import materialui.components.table.enums.TablePadding
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import materialui.icon
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.header
import react.dom.setProp
import react.useContext

private val ControllerConfigurerView = xComponent<DeviceConfigurerProps>("ControllerConfigurer") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = appContext.sceneEditorClient
    observe(sceneEditorClient)

    val styles = appContext.allStyles.controllerEditor

    val mutableControllers = props.mutableScene.controllers
    val controllerStates = sceneEditorClient.controllerStates
    val allControllerIds = (mutableControllers.keys + controllerStates.keys).sorted()

    var controllerMatcher by state { ControllerMatcher("") }
    val handleSearchChange by handler { value: String -> controllerMatcher = ControllerMatcher(value) }
    val handleSearchRequest by handler {}
    val handleSearchCancel by handler { controllerMatcher = ControllerMatcher("") }

    var selectedController by state<ControllerId?> { null }
    val handleControllerSelect by eventHandler { event ->
        val target = event.currentTarget as HTMLElement
        selectedController = ControllerId.fromName(target.dataset["controllerId"] ?: "huh?")
    }

    val handleNewControllerClick by eventHandler {
        selectedController = ControllerId(SacnManager.controllerTypeName, "new")
    }

    paper(styles.editorPanes on PaperStyle.root) {
        div(+styles.navigatorPane) {
            header { +"Controllers" }

            div(+styles.navigatorPaneActions) {
                button(+styles.button on ButtonStyle.root) {
                    attrs.color = ButtonColor.secondary
                    attrs.onClickFunction = handleNewControllerClick

                    attrs.startIcon { icon(materialui.icons.AddCircleOutline) }
                    +"New…"
                }

                SearchBar {
                    attrs.cancelOnEscape = true
                    attrs.className = +styles.searchBarPaper
                    attrs.onCancelSearch = handleSearchCancel
                    attrs.onChange = handleSearchChange
                    attrs.onRequestSearch = handleSearchRequest
                    attrs.value = controllerMatcher.searchString
                }
            }

            div(+styles.navigatorPaneContent) {
                table(+styles.controllersTable) {
                    attrs.padding = TablePadding.dense
                    setProp("stickyHeader", true)

                    tableHead {
                        tableRow {
                            thCell { +"Type" }
                            thCell { +"ID" }
                            thCell { +"Address" }
//                    thCell { +"Model Element" }
//                    thCell { +"Pixels" }
//                    thCell { +"Mapped" }
                            thCell { +"Status" }
                        }
                    }

                    tableBody {
                        allControllerIds.forEach { controllerId ->
                            val mutableController = mutableControllers[controllerId]
                            val state = controllerStates[controllerId]
                            if (controllerMatcher.matches(state, mutableController)) {
                                tableRow {
                                    attrs.onClickFunction = handleControllerSelect
                                    attrs["data-controller-id"] = controllerId.name()

                                    tdCell { +controllerId.controllerType }
                                    tdCell { +(state?.title ?: mutableController?.title ?: "Unnamed Controller") }
                                    tdCell { +(state?.address ?: "None") }
//                            tdCell { +(brainData.modelEntity ?: "Anonymous") }
//                            tdCell { +brainData.pixelCount.toString() }
//                            tdCell { +brainData.mappedPixelCount.toString() }
                                    tdCell {
                                        val onlineSince = state?.onlineSince
                                        if (onlineSince != null) {
                                            +"Online since ${
                                                DateTime(onlineSince * 1000)
                                                    .toString(DateFormat.FORMAT1)
                                            }"
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
        }

        div(+styles.propertiesPane) {
            header { +"Properties" }

            div(+styles.propertiesPaneContent) {
                selectedController?.let { selectedController ->
                    controllerConfigEditor {
                        attrs.mutableScene = props.mutableScene
                        attrs.controllerId = selectedController
                        attrs.onEdit = props.onEdit
                    }
                }
            }
        }

        div(+styles.fixturesPane) {
            header { +"Fixtures" }

//            div(+styles.viPaneContent) {
//                selectedController?.let { selectedController ->
//                    controllerConfigEditor {
//                        attrs.mutableScene = props.mutableScene
//                        attrs.controllerId = selectedController
//                        attrs.onEdit = props.onEdit
//                    }
//                }
//            }
        }
    }
}

external interface DeviceConfigurerProps : Props {
    var mutableScene: MutableScene
    var onEdit: () -> Unit
}

fun RBuilder.deviceConfigurer(handler: RHandler<DeviceConfigurerProps>) =
    child(ControllerConfigurerView, handler = handler)