package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.controller.ControllerId
import baaahs.scene.EditingController
import baaahs.scene.FixtureMappingData
import baaahs.scene.MutableFixtureMapping
import baaahs.scene.MutableScene
import baaahs.ui.on
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonStyle
import materialui.components.container.container
import materialui.components.typography.typographyH5
import materialui.icon
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.header
import react.useContext
import kotlin.collections.set

private val ControllerConfigEditorView = xComponent<ControllerConfigEditorProps>("ControllerConfigEditor") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = appContext.sceneEditorClient
    observe(sceneEditorClient)

    val styles = appContext.allStyles.controllerEditor

    val state = sceneEditorClient.controllerStates[props.controllerId]
    val mutableControllerConfig = memo(props.mutableScene, props.controllerId) {
        props.mutableScene.controllers[props.controllerId]
            ?: sceneEditorClient.createMutableControllerConfigFor(props.controllerId, state)
                .also { props.mutableScene.controllers[props.controllerId] = it }
    }

    val editingController = EditingController(mutableControllerConfig, props.onEdit)

    val recentlyAddedFixtureMappingRef = ref<MutableFixtureMapping>(null)
    val handleNewFixtureMappingClick by eventHandler(mutableControllerConfig, props.onEdit) {
        val newMapping = MutableFixtureMapping(FixtureMappingData())
        mutableControllerConfig.fixtures.add(newMapping)
        recentlyAddedFixtureMappingRef.current = newMapping
        props.onEdit()
    }

    container {
        typographyH5 {
            +mutableControllerConfig.title.ifBlank { "Untitled" }
            +" — ${mutableControllerConfig.controllerMeta.controllerTypeName}"
        }
    }

    editingController.getEditorPanelViews().forEach {
        with(it) { render() }
    }

    header { +"Defautls" }

    fixtureConfigPicker {
        attrs.editingController = editingController
        attrs.mutableFixtureConfig = mutableControllerConfig.defaultFixtureConfig
        attrs.setMutableFixtureConfig = { mutableControllerConfig.defaultFixtureConfig = it }
    }

    transportConfigPicker {
        attrs.editingController = editingController
        attrs.mutableTransportConfig = mutableControllerConfig.defaultTransportConfig
        attrs.setMutableTransportConfig = { mutableControllerConfig.defaultTransportConfig = it }
    }


    header { +"Fixture Mappings" }

    mutableControllerConfig.fixtures.forEach {
        fixtureMappingEditor {
            attrs.mutableScene = props.mutableScene
            attrs.editingController = editingController
            attrs.mutableFixtureMapping = it
            attrs.initiallyOpen = recentlyAddedFixtureMappingRef.current == it
        }
    }

    button(styles.button on ButtonStyle.root) {
        attrs.color = ButtonColor.secondary
        attrs.onClickFunction = handleNewFixtureMappingClick

        icon(materialui.icons.AddCircleOutline)
        +"New…"
    }
}

external interface ControllerConfigEditorProps : Props {
    var mutableScene: MutableScene
    var controllerId: ControllerId
    var onEdit: () -> Unit
}

fun RBuilder.controllerConfigEditor(handler: RHandler<ControllerConfigEditorProps>) =
    child(ControllerConfigEditorView, handler = handler)