package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.device.FixtureType
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureConfig
import baaahs.ui.asTextNode
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import kotlinx.js.jso
import mui.material.Card
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val FixtureConfigPickerView = xComponent<FixtureConfigPickerProps>("FixtureConfigPicker") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.controllerEditor

    val handleFixtureTypeChange by handler(
        props.mutableFixtureConfig, props.editingController.onChange
    ) { fixtureType: FixtureType? ->
        props.setMutableFixtureConfig(fixtureType?.emptyConfig?.edit())
        props.editingController.onChange()
    }

    val fixtureConfig = props.mutableFixtureConfig

    Card {
        attrs.classes = jso { this.root = -styles.configCardOuter }
        attrs.elevation = 4

        betterSelect<FixtureType?> {
            attrs.label = "Fixture Type"
            attrs.values = listOf(null) + appContext.plugins.fixtureTypes.all
            attrs.value = fixtureConfig?.fixtureType
            attrs.renderValueOption = { o -> (o?.title ?: "Default").asTextNode() }
            attrs.onChange = handleFixtureTypeChange
        }

        if (fixtureConfig != null) {
            Card {
                attrs.classes = jso { this.root = -styles.configCardInner }
                with(fixtureConfig.getEditorView(props.editingController)) { render() }
            }
        }
    }
}

external interface FixtureConfigPickerProps : Props {
    var editingController: EditingController<*>
    var mutableFixtureConfig: MutableFixtureConfig?
    var setMutableFixtureConfig: (mutableFixtureConfig: MutableFixtureConfig?) -> Unit
    var allowNullFixtureConfig: Boolean?
}

fun RBuilder.fixtureConfigPicker(handler: RHandler<FixtureConfigPickerProps>) =
    child(FixtureConfigPickerView, handler = handler)