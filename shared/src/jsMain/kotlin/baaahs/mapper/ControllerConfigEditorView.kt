package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.controller.ControllerId
import baaahs.device.PixelArrayDevice
import baaahs.scene.EditingController
import baaahs.scene.FixtureMappingData
import baaahs.scene.MutableFixtureMapping
import baaahs.scene.MutableScene
import baaahs.ui.render
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import materialui.icon
import mui.icons.material.ExpandMore
import mui.material.Accordion
import mui.material.AccordionDetails
import mui.material.AccordionSummary
import mui.material.Button
import mui.material.ButtonColor
import mui.material.Container
import mui.material.Size
import mui.material.Table
import mui.material.TableCell
import mui.material.TableHead
import mui.material.TableRow
import mui.material.Typography
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.create
import react.useContext
import web.cssom.em
import kotlin.collections.set

private val ControllerConfigEditorView = xComponent<ControllerConfigEditorProps>("ControllerConfigEditor") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = observe(appContext.sceneEditorClient)
    val editMode = observe(appContext.sceneManager.editMode)

    val styles = appContext.allStyles.controllerEditor
    val theme = useTheme<Theme>()

    val state = sceneEditorClient.controllerStates[props.controllerId]
    val mutableControllerConfig = memo(props.mutableScene, props.controllerId) {
        props.mutableScene.controllers[props.controllerId]
            ?: sceneEditorClient.createMutableControllerConfigFor(props.controllerId, state)
                .also { props.mutableScene.controllers[props.controllerId] = it }
    }

    val editingController = EditingController(props.controllerId, mutableControllerConfig, props.onEdit)

    val recentlyAddedFixtureMappingRef = ref<MutableFixtureMapping>(null)
    val handleNewFixtureMappingClick by mouseEventHandler(mutableControllerConfig, props.onEdit) {
        val newMapping = MutableFixtureMapping(FixtureMappingData(fixtureOptions = PixelArrayDevice.Options()))
        mutableControllerConfig.fixtures.add(newMapping)
        recentlyAddedFixtureMappingRef.current = newMapping
        props.onEdit()
    }

    Container {
        attrs.sx {
            paddingTop = 1.em
        }
//        typographyH5 {
//            +mutableControllerConfig.title.ifBlank { "Untitled" }
//            +" — ${mutableControllerConfig.controllerMeta.controllerTypeName}"
//        }

        val controllerState = sceneEditorClient.controllerStates[props.controllerId]

        Accordion {
            attrs.elevation = 4

            AccordionSummary {
                attrs.expandIcon = ExpandMore.create()
                Typography { +"Controller Info" }
            }

            AccordionDetails {
                Table {
                    attrs.size = Size.small

                    TableRow {
                        TableHead { +"Type:" }
                        TableCell { +mutableControllerConfig.controllerMeta.controllerTypeName }
                    }
                    TableRow {
                        TableHead { +"Address:" }
                        TableCell { +(controllerState?.address ?: "") }
                    }
                    TableRow {
                        TableHead { +"Online Since:" }
                        TableCell { +(controllerState?.onlineSince?.toString() ?: "Offline") }
                    }
                    TableRow {
                        TableHead { +"Firmware Version:" }
                        TableCell { +(controllerState?.firmwareVersion ?: "") }
                    }
                    TableRow {
                        TableHead { +"Last Error Message:" }
                        TableCell { +(controllerState?.lastErrorMessage ?: "") }
                    }
                    TableRow {
                        TableHead { +"Last Error At:" }
                        TableCell { +(controllerState?.lastErrorAt?.toString() ?: "") }
                    }
                }

                editingController.getEditorPanelViews().forEach {
                    it.render(this)
                }
            }
        }

        Accordion {
            attrs.elevation = 4

            AccordionSummary {
                attrs.expandIcon = ExpandMore.create()
                Typography { +"Fixtures" }
            }

            AccordionDetails {
                val tempModel = props.mutableScene.model.build().open()
                val tempController = mutableControllerConfig.build()
                val fixturePreviews = tempController.buildFixturePreviews(tempModel)
                mutableControllerConfig.fixtures.zip(fixturePreviews).forEach { (mutableFixtureMapping, fixturePreview) ->
                    fixtureMappingEditor {
                        attrs.mutableScene = props.mutableScene
                        attrs.editingController = editingController
                        attrs.mutableFixtureMapping = mutableFixtureMapping
                        attrs.fixturePreview = fixturePreview
                        attrs.initiallyOpen = recentlyAddedFixtureMappingRef.current == mutableFixtureMapping
                    }
                }

                Button {
                    attrs.className = -styles.button
                    attrs.color = ButtonColor.secondary
                    attrs.onClick = handleNewFixtureMappingClick

                    icon(mui.icons.material.AddCircleOutline)
                    +"New…"
                }
            }
        }

        Accordion {
            attrs.elevation = 4

            AccordionSummary {
                attrs.expandIcon = ExpandMore.create()
                Typography { +"Controller Fixture Defaults" }
            }
            AccordionDetails {
                fixtureConfigPicker {
                    attrs.editingController = editingController
                    attrs.mutableFixtureOptions = mutableControllerConfig.defaultFixtureOptions
                    attrs.setMutableFixtureOptions = { mutableControllerConfig.defaultFixtureOptions = it }
                    attrs.allowNullFixtureOptions = true
                }

            }
        }

        Accordion {
            attrs.elevation = 4

            AccordionSummary {
                attrs.expandIcon = ExpandMore.create()
                Typography { +"Controller Transport Defaults" }
            }
            AccordionDetails {
                transportConfigPicker {
                    attrs.editingController = editingController
                    attrs.mutableTransportConfig = mutableControllerConfig.defaultTransportConfig
                    attrs.setMutableTransportConfig = { mutableControllerConfig.defaultTransportConfig = it }
                }
            }
        }
    }
}

external interface ControllerConfigEditorProps : Props {
    var mutableScene: MutableScene
    var controllerId: ControllerId
    var onEdit: () -> Unit
}

fun RBuilder.controllerConfigEditor(handler: RHandler<ControllerConfigEditorProps>) =
    child(ControllerConfigEditorView, handler = handler)