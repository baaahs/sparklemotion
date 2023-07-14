package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.controller.ControllerId
import baaahs.controller.ControllerMatcher
import baaahs.controller.SacnManager
import baaahs.scene.MutableScene
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.value
import baaahs.ui.xComponent
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import js.core.jso
import materialui.icon
import mui.icons.material.Search
import mui.material.*
import mui.system.sx
import react.*
import react.dom.div
import react.dom.header
import web.cssom.Padding
import web.cssom.em
import web.html.HTMLElement

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
    val handleControllerSelect by mouseEventHandler { event ->
        val target = event.currentTarget as HTMLElement
        selectedController = ControllerId.fromName(target.dataset["controllerId"] ?: "huh?")
    }

    val handleNewControllerClick by mouseEventHandler() {
        selectedController = ControllerId(SacnManager.controllerTypeName, "new")
    }

    Paper {
        attrs.classes = jso { this.root = -styles.editorPanes }

        div(+styles.navigatorPane) {
            header { +"Controllers" }

            div(+styles.navigatorPaneActions) {
                Button {
                    attrs.classes = jso { this.root = -styles.button }
                    attrs.color = ButtonColor.secondary
                    attrs.onClick = handleNewControllerClick

                    attrs.startIcon = buildElement { icon(mui.icons.material.AddCircleOutline) }
                    +"New…"
                }

                FormControl {
                    TextField<StandardTextFieldProps> {
                        attrs.autoFocus = true
                        attrs.fullWidth = true
//                attrs.label { +props.label }
                        attrs.InputProps = jso {
                            endAdornment = buildElement { icon(Search) }
                        }
                        attrs.defaultValue = controllerMatcher.searchString

                        attrs.onChange = { event ->
                            handleSearchChange(event.target.value)
                        }
                    }

                    FormHelperText { +"Enter stuff to search for!" }
                }
            }

            div(+styles.navigatorPaneContent) {
                Table {
                    attrs.classes = jso { this.root = -styles.controllersTable }
                    attrs.stickyHeader = true

                    TableHead {
                        TableRow {
//                            TableCell { +"Type" }
                            TableCell { +"ID" }
                            TableCell { +"Address" }
//                    TableCell { +"Model Element" }
//                    TableCell { +"Pixels" }
//                    TableCell { +"Mapped" }
                            TableCell { +"Status" }
                            TableCell { +"Firmware" }
                            TableCell { +"Last Error" }
                            TableCell { +"Last Error At" }
                        }
                    }

                    var lastControllerType: String? = null
                    TableBody {
                        allControllerIds.forEach { controllerId ->
                            val mutableController = mutableControllers[controllerId]
                            val state = controllerStates[controllerId]
                            if (controllerMatcher.matches(state, mutableController)) {
                                if (controllerId.controllerType != lastControllerType) {
                                    TableRow {
                                        TableCell {
                                            attrs.colSpan = 6
                                            attrs.sx { padding = Padding(0.em, 0.em) }
                                            header { +controllerId.controllerType }
                                        }
                                    }

                                    lastControllerType = controllerId.controllerType
                                }

                                TableRow {
                                    attrs.onClick = handleControllerSelect
                                    attrs.asDynamic()["data-controller-id"] = controllerId.name()

                                    TableCell { +(state?.title ?: mutableController?.title ?: "Unnamed Controller") }
                                    TableCell { +(state?.address ?: "None") }
//                            TableCell { +(brainData.modelEntity ?: "Anonymous") }
//                            TableCell { +brainData.pixelCount.toString() }
//                            TableCell { +brainData.mappedPixelCount.toString() }
                                    TableCell {
                                        val onlineSince = state?.onlineSince
                                        if (onlineSince != null) {
                                            +"Online"

                                            val since = DateTime(onlineSince * 1000)
                                                .toString(DateFormat.FORMAT1)
                                            attrs.title = "Online since $since"
                                        } else {
                                            +"Offline"
                                        }
                                    }
                                    TableCell { +(state?.firmwareVersion ?: "") }
                                    TableCell { +(state?.lastErrorMessage ?: "") }
                                    TableCell {
                                        +(state?.lastErrorAt?.let {
                                            DateTime(it * 1000)
                                                .toString(DateFormat.FORMAT1)
                                        } ?: "")
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