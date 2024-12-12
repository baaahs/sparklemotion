package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.fixtures.TransportType
import baaahs.only
import baaahs.scene.EditingController
import baaahs.scene.MutableTransportConfig
import baaahs.ui.asTextNode
import baaahs.ui.render
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import mui.material.Card
import mui.material.Checkbox
import mui.material.Container
import mui.material.FormControl
import mui.material.FormControlLabel
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.useContext
import web.cssom.Padding
import web.cssom.em
import web.cssom.px

private val TransportConfigPickerView = xComponent<TransportConfigPickerProps>("DeviceConfigPicker") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.sceneManager.editMode)
    val styles = appContext.allStyles.controllerEditor

    val handleTransportTypeChange by handler(
        props.mutableTransportConfig, props.editingController.onChange
    ) { transportType: TransportType? ->
        props.setMutableTransportConfig(transportType?.emptyConfig?.edit())
        props.editingController.onChange()
    }

    val supportedTransportTypes = props.editingController.config.supportedTransportTypes
    val handleTransportConfigToggle by switchEventHandler(handleTransportTypeChange, supportedTransportTypes) { _, checked ->
        handleTransportTypeChange(if (checked) supportedTransportTypes.first() else null)
    }

    val transportConfig = props.mutableTransportConfig

    if (
        supportedTransportTypes.size == 1 &&
        !supportedTransportTypes.first().isConfigurable
    ) return@xComponent

    Card {
        attrs.className = -styles.configCardOuter
        attrs.elevation = 4

        if (supportedTransportTypes.size > 1) {
            betterSelect<TransportType?> {
                attrs.label = "Transport Type"
                attrs.values = listOf(null) + supportedTransportTypes
                attrs.value = transportConfig?.transportType
                attrs.renderValueOption = { o, _ -> (o?.title ?: "Default").asTextNode() }
                attrs.disabled = editMode.isOff
                attrs.onChange = handleTransportTypeChange
                attrs.fullWidth = true
            }
        } else {
            val transportType = supportedTransportTypes.only()

            Container {
                attrs.disableGutters = true

                FormControl {
                    attrs.sx { padding = Padding(0.px, .5.em) }

                    attrs.disabled = editMode.isOff
                    FormControlLabel {
                        attrs.label = buildElement { +"Specify ${transportType.title} Options" }
                        attrs.control = buildElement {
                            Checkbox {
//                                attrs.sx { marginLeft = 1.em}
                                attrs.checked = props.mutableTransportConfig != null
                                attrs.onChange = handleTransportConfigToggle
                            }
                        }
                    }
                }
            }
        }

        if (transportConfig != null) {
            Container {
                attrs.disableGutters = true

                transportConfig.getEditorView(props.editingController)
                    .render(this)
            }
        } else {
            props.editingController.config.defaultTransportConfig?.let { defaultTransportConfig ->
                +defaultTransportConfig.transportType.title
                configPreview {
                    attrs.configPreview = defaultTransportConfig.build().preview()
                }
            }
        }
    }
}

external interface TransportConfigPickerProps : Props {
    var editingController: EditingController<*>
    var mutableTransportConfig: MutableTransportConfig?
    var setMutableTransportConfig: (mutableTransportConfig: MutableTransportConfig?) -> Unit
}

fun RBuilder.transportConfigPicker(handler: RHandler<TransportConfigPickerProps>) =
    child(TransportConfigPickerView, handler = handler)