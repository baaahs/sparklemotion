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
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val FixtureConfigPickerView = xComponent<FixtureConfigPickerProps>("FixtureConfigPicker") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.controllerEditor

    val handleFixtureTypeChange by handler(
        props.mutableFixtureOptions, props.editingController.onChange
    ) { fixtureType: FixtureType? ->
        props.setMutableFixtureOptions(fixtureType?.emptyOptions?.edit())
        props.editingController.onChange()
    }

    val fixtureConfig = props.mutableFixtureOptions

    Card {
        attrs.className = -styles.configCardOuter
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
                attrs.className = -styles.configCardInner
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
    var mutableFixtureOptions: MutableFixtureOptions?
    var setMutableFixtureOptions: (mutableFixtureOptions: MutableFixtureOptions?) -> Unit
    var allowNullFixtureOptions: Boolean?
}

fun RBuilder.fixtureConfigPicker(handler: RHandler<FixtureConfigPickerProps>) =
    child(FixtureConfigPickerView, handler = handler)