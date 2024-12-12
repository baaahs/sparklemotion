package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.device.FixtureType
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureOptions
import baaahs.ui.asTextNode
import baaahs.ui.render
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import mui.material.Card
import mui.material.Checkbox
import mui.material.Container
import mui.system.sx
import mui.material.FormControl
import mui.material.FormControlLabel
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.useContext
import web.cssom.Padding
import web.cssom.em
import web.cssom.px

private val FixtureConfigPickerView = xComponent<FixtureConfigPickerProps>("FixtureConfigPicker") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.sceneManager.editMode)
    val styles = appContext.allStyles.controllerEditor

    val handleFixtureTypeChange by handler(
        props.mutableFixtureOptions, props.editingController.onChange
    ) { fixtureType: FixtureType? ->
        props.setMutableFixtureOptions(fixtureType?.emptyOptions?.edit())
        props.editingController.onChange()
    }

    val handleFixtureConfigToggle by switchEventHandler(handleFixtureTypeChange) { _, checked ->
        handleFixtureTypeChange(if (checked) props.fixtureType else null)
    }
    val fixtureConfig = props.mutableFixtureOptions
    val fixtureType = props.fixtureType

    Card {
        attrs.className = -styles.configCardOuter
        attrs.elevation = 4

        if (fixtureType == null) {
            betterSelect<FixtureType?> {
                attrs.label = "Fixture Type"
                attrs.values = listOf(null) + appContext.plugins.fixtureTypes.all
                attrs.value = fixtureConfig?.fixtureType
                attrs.renderValueOption = { o, _ -> (o?.title ?: "Default").asTextNode() }
                attrs.disabled = editMode.isOff
                attrs.onChange = handleFixtureTypeChange
                attrs.fullWidth = true
            }
        } else {
            Container {
                attrs.disableGutters = true

                FormControl {
                    attrs.sx { padding = Padding(0.px, .5.em) }

                    attrs.disabled = editMode.isOff
                    FormControlLabel {
                        attrs.label = buildElement { +"Specify ${fixtureType.title} Options" }
                        attrs.control = buildElement {
                            Checkbox {
//                                attrs.sx { marginLeft = 1.em}
                                attrs.checked = props.mutableFixtureOptions != null
                                attrs.onChange = handleFixtureConfigToggle
                            }
                        }
                    }
                }
            }
        }

        if (fixtureConfig != null) {
            Container {
                attrs.disableGutters = true

                fixtureConfig.getEditorView(props.editingController)
                    .render(this)
            }
        } else {
            props.editingController.config.defaultFixtureOptions?.let { defaultFixtureOptions ->
                +defaultFixtureOptions.fixtureType.title
                configPreview {
                    attrs.configPreview = defaultFixtureOptions.build().preview()
                }
            }
        }
    }
}

external interface FixtureConfigPickerProps : Props {
    var editingController: EditingController<*>
    var fixtureType: FixtureType?
    var mutableFixtureOptions: MutableFixtureOptions?
    var setMutableFixtureOptions: (mutableFixtureOptions: MutableFixtureOptions?) -> Unit
    var allowNullFixtureOptions: Boolean?
}

fun RBuilder.fixtureConfigPicker(handler: RHandler<FixtureConfigPickerProps>) =
    child(FixtureConfigPickerView, handler = handler)