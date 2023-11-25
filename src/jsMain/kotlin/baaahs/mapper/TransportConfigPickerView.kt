package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.dmx.DmxTransport
import baaahs.fixtures.TransportType
import baaahs.scene.EditingController
import baaahs.scene.MutableTransportConfig
import baaahs.ui.asTextNode
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import js.core.jso
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

    val transportConfig = props.mutableTransportConfig

    Card {
        attrs.classes = jso { this.root = -styles.configCardOuter }
//        attrs.elevation = 4

        betterSelect<TransportType?> {
            attrs.label = "Transport Type"
            attrs.values = listOf(null, DmxTransport)
            attrs.value = transportConfig?.transportType
            attrs.renderValueOption = { o -> (o?.title ?: "Default").asTextNode() }
            attrs.onChange = handleTransportTypeChange
        }

        if (transportConfig != null) {
            Card {
                attrs.classes = jso { this.root = -styles.configCardInner }
                with(transportConfig.getEditorView(props.editingController)) { render() }
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