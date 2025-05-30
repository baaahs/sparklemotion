package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.controller.ControllerId
import baaahs.controller.ControllerMatcher
import baaahs.controller.SacnManager
import baaahs.fixtures.FixtureInfo
import baaahs.scene.MutableScene
import baaahs.ui.*
import baaahs.ui.components.ListAndDetail
import baaahs.ui.components.collapsibleSearchBox
import baaahs.ui.components.listAndDetail
import baaahs.util.JsPlatform
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
import web.cssom.JustifyContent
import web.cssom.Padding
import web.cssom.VerticalAlign
import web.cssom.em
import web.cssom.number
import web.cssom.pct
import web.html.HTMLElement

private val ControllerListView = xComponent<DeviceListProps>("ControllerList") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = observe(appContext.sceneEditorClient)
    val editMode = observe(appContext.sceneManager.editMode)

    val styles = appContext.allStyles.controllerEditor

    val mutableControllers = props.mutableScene.controllers
    val controllerStates = sceneEditorClient.controllerStates
    val fixtureInfos = sceneEditorClient.fixtures.groupBy(FixtureInfo::controllerId)
    val allControllerIds = (mutableControllers.keys + controllerStates.keys).sorted()

    var controllerMatcher by state { ControllerMatcher() }
    val handleSearchChange by handler { value: String -> controllerMatcher = ControllerMatcher(value) }
    val handleSearchRequest by handler { value: String -> }
    val handleSearchCancel by handler { controllerMatcher = ControllerMatcher() }

    val scanningIndicatorRef = ref<HTMLElement>()
    val handleSearchBoxFocusChange by handler { focused: Boolean ->
        scanningIndicatorRef.current?.style?.visibility = if (focused) "hidden" else ""
    }

    var selectedController by state<ControllerId?> { null }
    val handleControllerSelect by mouseEventHandler { event ->
        val target = event.currentTarget as HTMLElement
        selectedController = ControllerId.fromName(target.dataset["controllerId"] ?: "huh?")
    }
    val handleDeselectController by handler {
        selectedController = null
    }

    val handleNewControllerClick by mouseEventHandler {
        // TODO: We can't just always use "new" here.
        selectedController = ControllerId(SacnManager.controllerTypeName, "new")
    }

    listAndDetail<ControllerId> {
        attrs.listHeader = buildElement {
            span {
                +"Controllers"

                div(+styles.scanningIndicator) {
                    ref = scanningIndicatorRef
                    CircularProgress {
                        attrs.sx {
                            marginLeft = 2.em
                            marginRight = .5.em
                            verticalAlign = VerticalAlign.middle
                        }
                        attrs.size = "1rem"
                        attrs.color = CircularProgressColor.primary
                        attrs.variant = CircularProgressVariant.indeterminate
                    }

                    Typography {
                        attrs.component = span
                        attrs.sx {
                            fontSize = .8.em
                            opacity = number(.75)
                        }

                        +"Scanning…"
                    }
                }

                collapsibleSearchBox {
                    attrs.alignRight = true
                    attrs.defaultSearchString = controllerMatcher.searchString
                    attrs.onSearchChange = handleSearchChange
                    attrs.onSearchRequest = handleSearchRequest
                    attrs.onSearchCancel = handleSearchCancel
                    attrs.onFocusChange = handleSearchBoxFocusChange
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

                    var lastControllerType: String? = null
                    TableBody {
                        allControllerIds.forEach { controllerId ->
                            val mutableController = mutableControllers[controllerId]
                            val state = controllerStates[controllerId]
                            if (controllerMatcher.matches(state, mutableController, fixtureInfos[controllerId])) {
                                if (controllerId.controllerType != lastControllerType) {
                                    TableRow {
                                        TableCell {
                                            attrs.colSpan = 4
                                            attrs.sx { padding = Padding(0.em, 0.em) }
                                            header(+styles.navigatorPaneHeader) {
                                                +controllerId.controllerType
                                            }
                                        }
                                    }

                                    TableRow {
                                        TableCell {
                                            attrs.colSpan = 2
                                            attrs.sx { width = 1.pct } // So the table fills full width.
                                            +""
                                        } // Status icon
                                        TableCell {
                                            Typography {
                                                attrs.sx { fontSize = .8.em }
                                                +"Name"
                                            }
                                        }
                                        TableCell {
                                            Typography {
                                                attrs.sx { fontSize = .8.em }
                                                +"Fixtures"
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
                                            val icon = appContext.plugins.controllers.findManager(controllerId)
                                                .controllerIcon
                                            attrs.src = JsPlatform.imageUrl("/assets/controllers/$icon")
                                        }
                                    }

                                    TableCell {
                                        +(mutableController?.title ?: "Unnamed Controller")
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
                                attrs.colSpan = 4

                                div(+styles.navigatorPaneActions) {
                                    Button {
                                        attrs.className = -styles.button
                                        attrs.sx { justifyContent = JustifyContent.flexStart }
                                        attrs.color = ButtonColor.primary
                                        attrs.disabled = editMode.isOff
                                        attrs.fullWidth = true
                                        attrs.onClick = handleNewControllerClick

                                        attrs.startIcon = buildElement { icon(mui.icons.material.AddCircleOutline) }
                                        +"New Controller…"
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        attrs.selection = selectedController
        attrs.detailHeader = buildElement {
            span {
                selectedController?.let { controllerId ->
                    img {
                        attrs.className = -styles.controllerIcon
                        val icon = appContext.plugins.controllers.findManager(controllerId)
                            .controllerIcon
                        attrs.src = JsPlatform.imageUrl("/assets/controllers/$icon")
                    }

                    +controllerId.name()
                }
            }
        }
        attrs.detailRenderer = ListAndDetail.DetailRenderer { controllerId ->
            controllerConfigEditor {
                attrs.mutableScene = props.mutableScene
                attrs.controllerId = controllerId
                attrs.onEdit = props.onEdit
            }
        }
        attrs.onDeselect = handleDeselectController
    }
}

fun styleIf(condition: Boolean?, style: RuleSet, otherwise: RuleSet? = null): String {
    return if (condition == true) +style else otherwise?.let { +it } ?: ""
}

external interface DeviceListProps : Props {
    var mutableScene: MutableScene
    var onEdit: () -> Unit
}

fun RBuilder.controllerList(handler: RHandler<DeviceListProps>) =
    child(ControllerListView, handler = handler)