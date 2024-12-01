package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.controller.ControllerId
import baaahs.fixtures.FixturePreviewError
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureMapping
import baaahs.scene.MutableScene
import baaahs.scene.SceneOpener
import baaahs.scene.mutable.SceneBuilder
import baaahs.ui.render
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import materialui.icon
import mui.icons.material.ExpandMore
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.sx
import react.*
import web.cssom.em

private val ControllerConfigEditorView = xComponent<ControllerConfigEditorProps>("ControllerConfigEditor") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = observe(appContext.sceneEditorClient)
    val editMode = observe(appContext.sceneManager.editMode)

    val styles = appContext.allStyles.controllerEditor
    val theme = useTheme<Theme>()

    val mutableControllerConfig = memo(props.mutableScene, props.controllerId) {
        props.mutableScene.controllers[props.controllerId]
            ?: sceneEditorClient.createMutableControllerConfigFor(props.controllerId)
                .also { props.mutableScene.controllers[props.controllerId] = it }
    }

    val mutableFixtureMappings = memo(props.mutableScene, props.controllerId) {
        props.mutableScene.fixtureMappings[props.controllerId]
            ?: mutableListOf<MutableFixtureMapping>()
    }

    val editingController = EditingController(props.controllerId, mutableControllerConfig, mutableFixtureMappings, props.onEdit)

    val recentlyAddedFixtureMappingRef = ref<MutableFixtureMapping>(null)
    val handleNewFixtureMappingClick by mouseEventHandler(mutableFixtureMappings, props.onEdit) {
        val newMapping = MutableFixtureMapping(null, null, null)
        mutableFixtureMappings.add(newMapping)
        recentlyAddedFixtureMappingRef.current = newMapping
        props.onEdit()
    }

    val handleDeleteFixtureMapping by handler(mutableControllerConfig, props.onEdit) { fixtureMapping: MutableFixtureMapping ->
        mutableFixtureMappings.remove(fixtureMapping)
        props.onEdit()
    }

    val sceneBuilder = SceneBuilder()
    val tempScene = props.mutableScene.build(sceneBuilder)
    val tempController = mutableControllerConfig.build(sceneBuilder)
    val previewBuilder = tempController.createPreviewBuilder()
    val sceneOpener = SceneOpener(tempScene)
        .also { it.open() }
    val fixturePreviews = mutableFixtureMappings.map { mapping ->
        try {
            val fixtureMappingData = mapping.build(sceneBuilder)
            val fixtureMapping = with (sceneOpener) { fixtureMappingData.open() }
            val fixtureOptions = fixtureMapping.resolveFixtureOptions(tempController.defaultFixtureOptions)
            val transportConfig = fixtureMapping.resolveTransportConfig(tempController)
            previewBuilder.createFixturePreview(fixtureOptions, transportConfig)
        } catch (e: Exception) {
            FixturePreviewError(e)
        }
    }
    val mutableFixtureMappingToPreview = mutableFixtureMappings.zip(fixturePreviews)

    Container {
        attrs.disableGutters = true
        attrs.sx {
            paddingTop = 1.em
        }

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

                    TableBody {
                        TableRow {
                            TableCell { attrs.variant = TableCellVariant.head; +"Type:" }
                            TableCell { +mutableControllerConfig.controllerMeta.controllerTypeName }
                        }
                        TableRow {
                            TableCell { attrs.variant = TableCellVariant.head; +"Address:" }
                            TableCell { +(controllerState?.address ?: "") }
                        }
                        TableRow {
                            TableCell { attrs.variant = TableCellVariant.head; +"Online Since:" }
                            TableCell { +(controllerState?.onlineSince?.toString() ?: "Offline") }
                        }
                        TableRow {
                            TableCell { attrs.variant = TableCellVariant.head; +"Firmware Version:" }
                            TableCell { +(controllerState?.firmwareVersion ?: "") }
                        }
                        TableRow {
                            TableCell { attrs.variant = TableCellVariant.head; +"Last Error Message:" }
                            TableCell { +(controllerState?.lastErrorMessage ?: "") }
                        }
                        TableRow {
                            TableCell { attrs.variant = TableCellVariant.head; +"Last Error At:" }
                            TableCell { +(controllerState?.lastErrorAt?.toString() ?: "") }
                        }
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

                Typography {
                    attrs.className = -styles.accordionPreview
                    +mutableFixtureMappings.joinToString(", ") {
                        it.entity?.title ?: "Anonymous"
                    }
                }
            }

            AccordionDetails {
                attrs.className = -styles.accordionDetails
                mutableFixtureMappingToPreview.forEach { (mutableFixtureMapping, fixturePreview) ->
                    fixtureMappingEditor {
                        attrs.mutableScene = props.mutableScene
                        attrs.editingController = editingController
                        attrs.mutableFixtureMapping = mutableFixtureMapping
                        attrs.fixturePreview = fixturePreview
                        attrs.initiallyOpen = recentlyAddedFixtureMappingRef.current == mutableFixtureMapping
                        attrs.onDelete = handleDeleteFixtureMapping
                    }
                }

                Paper {
                    attrs.className = -styles.accordionRoot

                    Box {
                        Button {
                            attrs.className = -styles.button
                            attrs.color = ButtonColor.secondary
                            attrs.fullWidth = true
                            attrs.onClick = handleNewFixtureMappingClick

                            icon(mui.icons.material.AddCircleOutline)
                            +"New Fixture Mapping…"
                        }
                    }
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