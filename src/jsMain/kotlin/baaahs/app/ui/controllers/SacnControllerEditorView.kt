package baaahs.app.ui.controllers

import baaahs.app.ui.appContext
import baaahs.scene.EditingController
import baaahs.scene.MutableSacnControllerConfig
import baaahs.ui.unaryMinus
import baaahs.ui.value
import baaahs.ui.xComponent
import kotlinx.js.jso
import mui.material.Container
import mui.material.TextField
import mui.system.sx
import react.*
import react.dom.onChange

private val SacnControllerEditorView = xComponent<SacnControllerEditorProps>("SacnControllerEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val mutableConfig = props.editingController.config

    val handleTitleChange by formEventHandler(mutableConfig, props.editingController) {
        mutableConfig.title = it.target.value
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
        attrs.classes = jso { root = -styles.propertiesEditSection }
        attrs.sx {
            display = csstype.Display.flex
            flexDirection = csstype.FlexDirection.column
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

        with (appContext.allStyles.modelEditor) {
            numberTextField("Universes", mutableConfig.universes, onChange = handleUniversesChange)
        }
    }
}

external interface SacnControllerEditorProps : Props {
    var editingController: EditingController<MutableSacnControllerConfig>
}

fun RBuilder.sacnControllerEditor(handler: RHandler<SacnControllerEditorProps>) =
    child(SacnControllerEditorView, handler = handler)