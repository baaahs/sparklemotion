package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.fixtures.TransportType
import baaahs.scene.EditingController
import baaahs.scene.MutableTransportConfig
import baaahs.ui.asTextNode
import baaahs.ui.render
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import mui.material.Card
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val TransportConfigPickerView = xComponent<TransportConfigPickerProps>("DeviceConfigPicker") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.controllerEditor

    val handleTransportTypeChange by handler(
        props.mutableTransportConfig, props.editingController.onChange
    ) { transportType: TransportType? ->
        props.setMutableTransportConfig(transportType?.emptyConfig?.edit())
        props.editingController.onChange()
    }

    val supportedTransportTypes = props.editingController.config.supportedTransportTypes
    val transportConfig = props.mutableTransportConfig

    if (
        supportedTransportTypes.size == 1 &&
        !supportedTransportTypes.first().isConfigurable
    ) return@xComponent

    Card {
        attrs.className = -styles.configCardOuter
        attrs.elevation = 4

        betterSelect<TransportType?> {
            attrs.label = "Transport Type"
            attrs.values = listOf(null) + supportedTransportTypes
            attrs.value = transportConfig?.transportType
            attrs.renderValueOption = { o -> (o?.title ?: "Default").asTextNode() }
            attrs.onChange = handleTransportTypeChange
            attrs.fullWidth = true
        }

        if (transportConfig != null) {
            Card {
                attrs.className = -styles.configCardInner
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