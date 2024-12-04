package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.controller.ControllerId
import baaahs.controller.ControllerMatcher
import baaahs.controller.SacnManager
import baaahs.dmx.DmxManager
import baaahs.fixtures.FixtureInfo
import baaahs.scene.MutableScene
import baaahs.sm.brain.BrainManager
import baaahs.ui.*
import baaahs.ui.components.ListAndDetail
import baaahs.ui.components.collapsibleSearchBox
import baaahs.ui.components.listAndDetail
import kotlinx.css.Color
import kotlinx.css.RuleSet
import kotlinx.css.backgroundColor
import materialui.icon
import mui.material.*
import mui.system.sx
import react.*
import react.dom.div
import react.dom.header
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.span
import react.dom.li
import styled.inlineStyles
import web.cssom.Padding
import web.cssom.em
import web.cssom.pct
import web.html.HTMLElement

private val ControllerConfigurerView = xComponent<DeviceConfigurerProps>("ControllerConfigurer") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = observe(appContext.sceneEditorClient)

    val styles = appContext.allStyles.controllerEditor

    val mutableControllers = props.mutableScene.controllers
    val controllerStates = sceneEditorClient.controllerStates
    val fixtureInfos = sceneEditorClient.fixtures.groupBy(FixtureInfo::controllerId)
    val allControllerIds = (mutableControllers.keys + controllerStates.keys).sorted()

    var controllerMatcher by state { ControllerMatcher() }
    val handleSearchChange by handler { value: String -> controllerMatcher = ControllerMatcher(value) }
    val handleSearchRequest by handler { value: String -> }
    val handleSearchCancel by handler { controllerMatcher = ControllerMatcher() }

    var selectedController by state<ControllerId?> { null }
    val handleControllerSelect by mouseEventHandler { event ->
        val target = event.currentTarget as HTMLElement
        selectedController = ControllerId.fromName(target.dataset["controllerId"] ?: "huh?")
    }
    val handleDeselectController by handler {
        selectedController = null
    }

    val handleNewControllerClick by mouseEventHandler {
        selectedController = ControllerId(SacnManager.controllerTypeName, "new")
    }

    listAndDetail<ControllerId> {
        attrs.listHeader = buildElement {
            span {
                +"Controllers"
                collapsibleSearchBox {
                    attrs.searchString = controllerMatcher.searchString
                    attrs.onSearchChange = handleSearchChange
                    attrs.onSearchRequest = handleSearchRequest
                    attrs.onSearchCancel = handleSearchCancel
                }
            }
        }
        attrs.listHeaderText = "Controllers".asTextNode()
        attrs.listRenderer = ListAndDetail.ListRenderer {
            div(+styles.navigatorPaneContent) {
                Table {
                    attrs.className = -styles.controllersTable
                    attrs.size = Size.small
                    attrs.stickyHeader = true

                    TableHead {
                        TableRow {
                            TableCell {
                                attrs.sx { width = 1.pct } // So the table fills full width.
                                +""
                            } // Status icon
                            TableCell { +"Name" }
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
                                            attrs.colSpan = 3
                                            attrs.sx { padding = Padding(0.em, 0.em) }
                                            header(+styles.navigatorPaneHeader) {
                                                +controllerId.controllerType
                                            }
                                        }
                                    }

                                    lastControllerType = controllerId.controllerType
                                }

                                TableRow {
                                    attrs.selected = controllerId == selectedController
                                    attrs.onClick = handleControllerSelect
                                    attrs.asDynamic()["data-controller-id"] = controllerId.name()

                                    TableCell {
                                        attrs.align = TableCellAlign.center
                                        attrs.sx { width = 1.pct } // So the table fills full width.

                                        div(+styles.statusDot) {
                                            inlineStyles {
                                                backgroundColor = when {
                                                    state?.lastErrorAt != null -> Color.red
                                                    state?.onlineSince != null -> Color.green
                                                    else -> Color.grey
                                                }
                                            }
                                        }
                                    }

                                    TableCell {
                                        img {
                                            attrs.className = -styles.controllerIcon
                                            attrs.src = "/assets/controllers/${
                                                when (controllerId.controllerType) {
                                                    BrainManager.controllerTypeName -> "baaahs-brain.svg"
                                                    DmxManager.controllerTypeName -> "dmx.svg"
                                                    SacnManager.controllerTypeName -> "sacn.svg"
                                                    else -> "unknown.svg"
                                                }
                                            }"
                                        }
                                        +(state?.title ?: mutableController?.title ?: "Unnamed Controller")
                                    }

                                    TableCell {
                                        fixtureInfos[controllerId]?.forEach { fixtureInfo ->
                                            li(+styles.fixtureListItem) {
                                                +(fixtureInfo.entityName ?: "[anonymous]")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        TableRow {
                            TableCell {
                                attrs.colSpan = 3

                                div(+styles.navigatorPaneActions) {
                                    Button {
                                        attrs.className = -styles.button
                                        attrs.color = ButtonColor.primary
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
        attrs.detailHeader = selectedController?.name()?.asTextNode()
        attrs.detailRenderer = ListAndDetail.DetailRenderer { controller ->
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