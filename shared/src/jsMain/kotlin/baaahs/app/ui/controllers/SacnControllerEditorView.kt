package baaahs.app.ui.controllers

import baaahs.app.ui.appContext
import baaahs.app.ui.model.numberTextField
import baaahs.scene.EditingController
import baaahs.scene.MutableSacnControllerConfig
import baaahs.ui.unaryMinus
import baaahs.ui.value
import baaahs.ui.xComponent
import mui.material.Container
import mui.material.TextField
import mui.system.sx
import react.*
import react.dom.onChange
import web.events.EventTarget

private val SacnControllerEditorView = xComponent<SacnControllerEditorProps>("SacnControllerEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val mutableConfig = props.editingController.config

    val handleTitleChange by formEventHandler(mutableConfig, props.editingController) {
        val target: EventTarget = it.target
        mutableConfig.title = target.value
        props.editingController.onChange()
    }

    val handleAddressChange by formEventHandler(mutableConfig, props.editingController) {
        mutableConfig.address = it.target.value
        props.editingController.onChange()
    }

    val handleUniversesChange by handler(mutableConfig, props.editingController) { value: Int ->
        mutableConfig.universes = value
        props.editingController.onChange()
    }

    Container {
        attrs.className = -styles.propertiesEditSection
        attrs.sx {
            display = web.cssom.Display.flex
            flexDirection = web.cssom.FlexDirection.column
        }

        TextField {
            attrs.label = buildElement { +"Title" }
            attrs.fullWidth = true
            attrs.value = mutableConfig.title
            attrs.onChange = handleTitleChange
        }

        TextField {
            attrs.label = buildElement { +"Address" }
            attrs.fullWidth = true
            attrs.value = mutableConfig.address
            attrs.onChange = handleAddressChange
        }

        numberTextField<Int> {
            attrs.label = "Universes"
            attrs.value = mutableConfig.universes
            attrs.onChange = handleUniversesChange
        }
    }
}

external interface SacnControllerEditorProps : Props {
    var editingController: EditingController<MutableSacnControllerConfig>
}

fun RBuilder.sacnControllerEditor(handler: RHandler<SacnControllerEditorProps>) =
    child(SacnControllerEditorView, handler = handler)