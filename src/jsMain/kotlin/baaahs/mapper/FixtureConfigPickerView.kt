package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.device.FixtureType
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureConfig
import baaahs.ui.asTextNode
import baaahs.ui.on
import baaahs.ui.xComponent
import materialui.components.card.card
import materialui.components.paper.enums.PaperStyle
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

    card(styles.configCardOuter on PaperStyle.root) {
        attrs.elevation = 4

        betterSelect<FixtureType?> {
            attrs.label = "Fixture Type"
            attrs.values = listOf(null) + appContext.plugins.fixtureTypes.all
            attrs.value = fixtureConfig?.fixtureType
            attrs.renderValueOption = { o -> (o?.title ?: "Default").asTextNode() }
            attrs.onChange = handleFixtureTypeChange
        }

        if (fixtureConfig != null) {
            card(styles.configCardInner on PaperStyle.root) {
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