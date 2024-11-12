package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.controller.ControllerId
import baaahs.controller.ControllerMatcher
import baaahs.controller.SacnManager
import baaahs.fixtures.FixtureInfo
import baaahs.scene.MutableScene
import baaahs.ui.*
import js.objects.jso
import kotlinx.css.RuleSet
import materialui.icon
import mui.icons.material.Search
import mui.material.*
import mui.system.Breakpoint
import mui.system.Theme
import mui.system.sx
import mui.system.useMediaQuery
import react.*
import react.dom.div
import react.dom.header
import react.dom.html.ReactHTML.span
import web.cssom.Float
import web.cssom.Padding
import web.cssom.em
import web.cssom.vw
import web.html.HTMLElement

private val ControllerConfigurerView = xComponent<DeviceConfigurerProps>("ControllerConfigurer") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = appContext.sceneEditorClient
    observe(sceneEditorClient)

    val isSmallScreen = useMediaQuery({ theme: Theme -> theme.breakpoints.down(Breakpoint.sm) })

    val styles = appContext.allStyles.controllerEditor

    val mutableControllers = props.mutableScene.controllers
    val controllerStates = sceneEditorClient.controllerStates
    val fixtureInfos = sceneEditorClient.fixtures.groupBy(FixtureInfo::controllerId)
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
    val handleDeselectController by mouseEventHandler { event ->
        selectedController = null
    }

    val handleNewControllerClick by mouseEventHandler() {
        selectedController = ControllerId(SacnManager.controllerTypeName, "new")
    }

    Paper {
        attrs.className = if (selectedController == null) {
            -styles.editorPanes and styles.noControllerSelected
        } else {
            -styles.editorPanes
        }

        div(+styles.navigatorPane
                and styleIf(isSmallScreen, styles.screenWidthNavigatorPane)
                and styleIf(isSmallScreen && selectedController != null, styles.hideNavigatorPane)
        ) {
            header { +"Controllers" }

            div(+styles.navigatorPaneActions) {
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

                Button {
                    attrs.className = -styles.button
                    attrs.color = ButtonColor.secondary
                    attrs.onClick = handleNewControllerClick

                    attrs.startIcon = buildElement { icon(mui.icons.material.AddCircleOutline) }
                    +"New…"
                }
            }

            div(+styles.navigatorPaneContent) {
                Table {
                    attrs.className = -styles.controllersTable
                    attrs.size = Size.small
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
                            TableCell { +"Fixtures" }
                        }
                    }

                    var lastControllerType: String? = null
                    TableBody {
                        allControllerIds.forEach { controllerId ->
                            val mutableController = mutableControllers[controllerId]
                            val state = controllerStates[controllerId]
                            if (controllerMatcher.matches(state, mutableController, fixtureInfos[controllerId])) {
                                if (controllerId.controllerType != lastControllerType) {
                                    TableRow {
                                        TableCell {
                                            attrs.colSpan = 6
                                            attrs.sx { padding = Padding(0.em, 0.em) }
                                            header(+styles.navigatorPaneHeader) {
                                                +controllerId.controllerType }
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

                                            attrs.title = "Online since $onlineSince"
                                        } else {
                                            +"Offline"
                                        }
                                    }
                                    TableCell { +(state?.firmwareVersion ?: "") }
                                    TableCell {
                                        +(state?.lastErrorMessage ?: "")
                                        state?.lastErrorAt?.let { +" at $it" }
                                    }

                                    TableCell {
                                        fixtureInfos[controllerId]?.forEach { fixtureInfo ->
                                            span {
                                                attrs.title = run {
                                                    fixtureInfo.entityId?.let {
                                                        props.mutableScene.model.findById(it)?.title
                                                    } ?: "[anonymous]"
                                                }
                                                +(fixtureInfo.entityId ?: "[anonymous]")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        selectedController?.let { selectedController ->
            Box {
                attrs.className = -styles.propertiesPane
                if (isSmallScreen) {
                    attrs.sx { width = 100.vw }
                }

                header {
                    +"${selectedController.name()} Properties"
                    IconButton {
                        attrs.sx { float = Float.right }
                        attrs.title = "Close"
                        attrs.onClick = handleDeselectController
                        icon(mui.icons.material.Close)
                    }
                }

                div(+styles.propertiesPaneContent) {
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

fun styleIf(condition: Boolean, style: RuleSet): String {
    return if (condition) +style else ""
}

external interface DeviceConfigurerProps : Props {
    var mutableScene: MutableScene
    var onEdit: () -> Unit
}

fun RBuilder.deviceConfigurer(handler: RHandler<DeviceConfigurerProps>) =
    child(ControllerConfigurerView, handler = handler)