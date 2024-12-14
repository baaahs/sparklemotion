package baaahs.mapper

import baaahs.SparkleMotion.maybeRemoveAnonymous
import baaahs.app.ui.appContext
import baaahs.app.ui.editor.textFieldEditor
import baaahs.controller.ControllerId
import baaahs.fixtures.FixturePreviewError
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureMapping
import baaahs.scene.MutableScene
import baaahs.scene.SceneOpener
import baaahs.scene.mutable.SceneBuilder
import baaahs.ui.muiClasses
import baaahs.ui.render
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import materialui.icon
import mui.icons.material.ExpandMore
import mui.material.Accordion
import mui.material.AccordionDetails
import mui.material.AccordionSummary
import mui.material.Box
import mui.material.Button
import mui.material.ButtonColor
import mui.material.Container
import mui.material.FormHelperText
import mui.material.Paper
import mui.material.Size
import mui.material.Table
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableCellVariant
import mui.material.TablePadding
import mui.material.TableRow
import mui.material.Typography
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.create
import react.useContext
import web.cssom.em

private val ControllerConfigEditorView = xComponent<ControllerConfigEditorProps>("ControllerConfigEditor") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = observe(appContext.sceneEditorClient)
    val editMode = observe(appContext.sceneManager.editMode)
    val styles = appContext.allStyles.controllerEditor

    val mutableControllerConfig = memo(props.mutableScene, props.controllerId) {
        props.mutableScene.controllers[props.controllerId]
            ?: sceneEditorClient.createMutableControllerConfigFor(props.controllerId)
                .also { props.mutableScene.controllers[props.controllerId] = it }
    }

    val mutableFixtureMappings = memo(props.mutableScene, props.controllerId) {
        props.mutableScene.fixtureMappings.getOrPut(props.controllerId) {
            mutableListOf<MutableFixtureMapping>()
        }
    }
    val editingFixtureMappings = memo(props.mutableScene, props.controllerId, mutableFixtureMappings) {
        mutableFixtureMappings.toMutableList()
    }

    /**
     * If SparkleMotion.SUPPORT_ANONYMOUS_FIXTURE_MAPPINGS is false, we need to filter out fixture
     * mappings with no entity must be removed, but we still need to be able to edit them in that
     * state, so we hold another unfiltered list for editing.
     */
    val handleEdit by handler(props.onEdit, props.controllerId, mutableFixtureMappings, editingFixtureMappings) {
        val filtered = editingFixtureMappings.maybeRemoveAnonymous()
        if (filtered != mutableFixtureMappings) {
            mutableFixtureMappings.clear()
            mutableFixtureMappings.addAll(filtered)
            props.onEdit()
        } else {
            forceRender()
        }
    }
    val editingController = EditingController(props.controllerId, mutableControllerConfig, editingFixtureMappings, handleEdit)

    val recentlyAddedFixtureMappingRef = ref<MutableFixtureMapping>(null)
    val handleNewFixtureMappingClick by mouseEventHandler(editingFixtureMappings, handleEdit) {
        val newMapping = MutableFixtureMapping(null, null, null)
        editingFixtureMappings.add(newMapping)
        recentlyAddedFixtureMappingRef.current = newMapping
        handleEdit()
    }

    val handleDeleteFixtureMapping by handler(mutableControllerConfig, editingFixtureMappings, handleEdit) { fixtureMapping: MutableFixtureMapping ->
        editingFixtureMappings.remove(fixtureMapping)
        handleEdit()
    }

    val sceneBuilder = SceneBuilder()
    val tempScene = props.mutableScene.build(sceneBuilder)
    val tempController = mutableControllerConfig.build(sceneBuilder)
    val previewBuilder = tempController.createPreviewBuilder()
    val sceneOpener = SceneOpener(tempScene)
        .also { it.open() }
    val fixturePreviews = editingFixtureMappings.map { mapping ->
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
    val mutableFixtureMappingToPreview = editingFixtureMappings.zip(fixturePreviews)

    Container {
        attrs.disableGutters = true
        attrs.sx {
            paddingTop = 1.em
        }

        val controllerState = sceneEditorClient.controllerStates[props.controllerId]

        Accordion {
            attrs.elevation = 4
            attrs.defaultExpanded = true

            AccordionSummary {
                attrs.className = -styles.accordionSummaryRoot
                attrs.expandIcon = ExpandMore.create()
                Typography { +"Fixtures" }

                Typography {
                    attrs.className = -styles.accordionPreview
                    +editingFixtureMappings.joinToString(", ") {
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
                            attrs.color = ButtonColor.primary
                            attrs.disabled = editMode.isOff
                            attrs.fullWidth = true
                            attrs.onClick = handleNewFixtureMappingClick

                            attrs.startIcon = buildElement { icon(mui.icons.material.AddCircleOutline) }
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
                Typography { +"Controller Info" }
            }

            AccordionDetails {
                Table {
                    attrs.padding = TablePadding.none
                    attrs.size = Size.small

                    TableBody {
                        TableRow {
                            TableCell { attrs.variant = TableCellVariant.head; +"Name:" }
                            TableCell {
                                textFieldEditor {
                                    attrs.disabled = editMode.isOff
                                    attrs.getValue = { mutableControllerConfig.title }
                                    attrs.setValue = { mutableControllerConfig.title = it }
                                    attrs.onChange = { handleEdit() }
                                }
                            }
                        }
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
                attrs.classes = muiClasses { content = -styles.accordionSummaryContentRows }
                attrs.expandIcon = ExpandMore.create()
                Typography { +"Controller Fixture Defaults" }
                FormHelperText {
                    +"""
                         Fixture settings given here will be used as defaults for mapped fixtures.
                    """.trimIndent()
                }
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
                attrs.classes = muiClasses { content = -styles.accordionSummaryContentRows }
                attrs.expandIcon = ExpandMore.create()
                Typography { +"Controller Transport Defaults" }
                FormHelperText {
                    +"""
                         Transport settings given here will be used as defaults for mapped fixtures.
                    """.trimIndent()
                }
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