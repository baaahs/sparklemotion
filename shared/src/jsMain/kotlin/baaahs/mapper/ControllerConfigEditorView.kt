package baaahs.mapper

import baaahs.app.ui.appContext
import baaahs.controller.ControllerId
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureMapping
import baaahs.scene.MutableScene
import baaahs.scene.SceneOpener
import baaahs.scene.mutable.SceneBuilder
import baaahs.ui.render
import baaahs.ui.typographyH5
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import materialui.icon
import mui.material.*
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.header
import react.dom.html.ReactHTML.li
import react.useContext

private val ControllerConfigEditorView = xComponent<ControllerConfigEditorProps>("ControllerConfigEditor") { props ->
    val appContext = useContext(appContext)
    val sceneEditorClient = observe(appContext.sceneEditorClient)

    val styles = appContext.allStyles.controllerEditor

    val mutableControllerConfig = memo(props.mutableScene, props.controllerId) {
        props.mutableScene.controllers[props.controllerId]
            ?: sceneEditorClient.createMutableControllerConfigFor(props.controllerId)
                .also { props.mutableScene.controllers[props.controllerId] = it }
    }

    val editingController = EditingController(props.controllerId, mutableControllerConfig, props.onEdit)

    val recentlyAddedFixtureMappingRef = ref<MutableFixtureMapping>(null)
    val handleNewFixtureMappingClick by mouseEventHandler(mutableControllerConfig, props.onEdit) {
        val newMapping = MutableFixtureMapping(null, null, null)
        mutableControllerConfig.fixtures.add(newMapping)
        recentlyAddedFixtureMappingRef.current = newMapping
        props.onEdit()
    }

    Container {
        typographyH5 {
            +mutableControllerConfig.title.ifBlank { "Untitled" }
            +" — ${mutableControllerConfig.controllerMeta.controllerTypeName}"
        }

        val controllerState = sceneEditorClient.controllerStates[props.controllerId]

        Typography {
            li { +"Title: ${controllerState?.title}" }
            li { +"Address: ${controllerState?.address}" }
            li { +"Online Since: ${controllerState?.onlineSince}" }
            li { +"Firmware Version: ${controllerState?.firmwareVersion}" }
            li { +"Last Error Message: ${controllerState?.lastErrorMessage}" }
            li { +"Last Error At: ${controllerState?.lastErrorAt}" }
        }
    }

    editingController.getEditorPanelViews().forEach {
        it.render(this)
    }

    Card {
        attrs.className = -styles.defaultConfigs
        attrs.elevation = 4

        header {
            +"Controller Defaults"
        }

        fixtureConfigPicker {
            attrs.editingController = editingController
            attrs.mutableFixtureOptions = mutableControllerConfig.defaultFixtureOptions
            attrs.setMutableFixtureOptions = { mutableControllerConfig.defaultFixtureOptions = it }
            attrs.allowNullFixtureOptions = true
        }

        transportConfigPicker {
            attrs.editingController = editingController
            attrs.mutableTransportConfig = mutableControllerConfig.defaultTransportConfig
            attrs.setMutableTransportConfig = { mutableControllerConfig.defaultTransportConfig = it }
        }
    }


    header { +"Fixture Mappings" }

    val sceneBuilder = SceneBuilder()
    val tempScene = props.mutableScene.build(sceneBuilder)
    val tempController = mutableControllerConfig.build(sceneBuilder)
    val sceneOpener = SceneOpener(tempScene)
        .also { it.open() }
    val fixturePreviews = tempController.buildFixturePreviews(sceneOpener)
    mutableControllerConfig.fixtures.zip(fixturePreviews).forEach { (mutableFixtureMapping, fixturePreview) ->
        fixtureMappingEditor {
            attrs.mutableScene = props.mutableScene
            attrs.editingController = editingController
            attrs.mutableFixtureMapping = mutableFixtureMapping
            attrs.fixturePreview = fixturePreview
            attrs.initiallyOpen = recentlyAddedFixtureMappingRef.current == mutableFixtureMapping
        }
    }

    Button {
        attrs.className = -styles.button
        attrs.color = ButtonColor.secondary
        attrs.onClick = handleNewFixtureMappingClick

        icon(mui.icons.material.AddCircleOutline)
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