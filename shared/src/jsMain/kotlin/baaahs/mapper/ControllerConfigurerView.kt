package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.controller.ControllerId
import baaahs.controller.ControllerMatcher
import baaahs.controller.SacnManager
import baaahs.fixtures.FixtureInfo
import baaahs.scene.MutableScene
import baaahs.ui.*
import baaahs.ui.components.DetailRenderer
import baaahs.ui.components.ListRenderer
import baaahs.ui.components.listAndDetail
import js.objects.jso
import kotlinx.css.RuleSet
import materialui.icon
import mui.icons.material.Search
import mui.material.*
import mui.system.sx
import react.*
import react.dom.div
import react.dom.header
import react.dom.html.ReactHTML.span
import web.cssom.Padding
import web.cssom.Transition
import web.cssom.em
import web.html.HTMLElement
import web.html.HTMLInputElement

private val ControllerConfigurerView = xComponent<DeviceConfigurerProps>("ControllerConfigurer") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = observe(appContext.sceneEditorClient)

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
    val handleDeselectController by handler {
        selectedController = null
    }

    val handleNewControllerClick by mouseEventHandler() {
        selectedController = ControllerId(SacnManager.controllerTypeName, "new")
    }

    var searchFieldFocused by state { false }
    val searchFieldRef = useRef<HTMLElement>()
    val handleSearchBoxClick by mouseEventHandler { e ->
        searchFieldFocused = true
        (searchFieldRef.current?.querySelector("input") as? HTMLInputElement)
            ?.focus()
        e.preventDefault()
    }

    listAndDetail<ControllerId> {
        attrs.listHeader = buildElement {
            span {
                +"Controllers"

                FormControl {
                    attrs.className = -styles.searchBoxFormControl
                    attrs.onClick = handleSearchBoxClick

                    TextField<StandardTextFieldProps> {
                        ref = searchFieldRef
                        attrs.sx {
                            val isOpen = searchFieldFocused || controllerMatcher.searchString.isNotBlank()
                            width = if (isOpen) 15.em else 3.em
                            backgroundColor = if (isOpen) rgba(0, 0, 0, 0.25).asColor() else rgba(0, 0, 0, 0.0).asColor()
                            transition = "width 300ms, backgrond-color 300ms".unsafeCast<Transition>()
                        }
                        attrs.size = Size.small
                        attrs.InputProps = jso {
                            endAdornment = buildElement { icon(Search) }
                        }
                        attrs.defaultValue = controllerMatcher.searchString

                        attrs.onChange = { event ->
                            handleSearchChange(event.target.value)
                        }
                        attrs.onFocus = { _ -> searchFieldFocused = true }
                        attrs.onBlur = { _ -> searchFieldFocused = false }
                    }

//                    FormHelperText { +"Enter stuff to search for!" }
                }
            }
        }
        attrs.listHeaderText = "Controllers".asTextNode()
        attrs.listRenderer = ListRenderer {
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
                                                +controllerId.controllerType
                                            }
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

                        TableRow {
                            TableCell {
                                attrs.colSpan = 6

                                div(+styles.navigatorPaneActions) {
                                    Button {
                                        attrs.className = -styles.button
                                        attrs.color = ButtonColor.secondary
                                        attrs.onClick = handleNewControllerClick

                                        attrs.startIcon = buildElement { icon(mui.icons.material.AddCircleOutline) }
                                        +"New…"
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        attrs.selection = selectedController
        attrs.detailHeader = selectedController?.name()
        attrs.detailRenderer = DetailRenderer { controller ->
            controllerConfigEditor {
                attrs.mutableScene = props.mutableScene
                attrs.controllerId = controller
                attrs.onEdit = props.onEdit
            }
        }
        attrs.onDeselect = handleDeselectController
    }
}

fun styleIf(condition: Boolean, style: RuleSet, otherwise: RuleSet? = null): String {
    return if (condition) +style else otherwise?.let { +it } ?: ""
}

external interface DeviceConfigurerProps : Props {
    var mutableScene: MutableScene
    var onEdit: () -> Unit
}

fun RBuilder.deviceConfigurer(handler: RHandler<DeviceConfigurerProps>) =
    child(ControllerConfigurerView, handler = handler)