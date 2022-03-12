package baaahs.app.ui.controllers

import baaahs.app.ui.appContext
import baaahs.scene.EditingController
import baaahs.scene.MutableSacnControllerConfig
import baaahs.ui.on
import baaahs.ui.value
import baaahs.ui.xComponent
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.display
import kotlinx.css.flexDirection
import kotlinx.html.js.onChangeFunction
import materialui.components.container.container
import materialui.components.container.enums.ContainerStyle
import materialui.components.textfield.textField
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext
import styled.inlineStyles

private val SacnControllerEditorView = xComponent<SacnControllerEditorProps>("SacnControllerEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val mutableConfig = props.editingController.config

    val handleTitleChange by eventHandler(mutableConfig, props.editingController) {
        mutableConfig.title = it.target.value
        props.editingController.onChange()
    }

    val handleAddressChange by eventHandler(mutableConfig, props.editingController) {
        mutableConfig.address = it.target.value
        props.editingController.onChange()
    }

    container(styles.propertiesEditSection on ContainerStyle.root) {
        inlineStyles {
            display = Display.flex
            flexDirection = FlexDirection.column
        }

        textField {
            attrs.label { +"Title" }
            attrs.fullWidth = true
            attrs.value(mutableConfig.title)
            attrs.onChangeFunction = handleTitleChange
        }

        textField {
            attrs.label { +"Address" }
            attrs.fullWidth = true
            attrs.value(mutableConfig.address)
            attrs.onChangeFunction = handleAddressChange
        }

        with (appContext.allStyles.modelEditor) {
            numberTextField("Universes", mutableConfig.universes, onChange = {
                mutableConfig.universes = it
            })
        }
    }
}

external interface SacnControllerEditorProps : Props {
    var editingController: EditingController<MutableSacnControllerConfig>
}

fun RBuilder.sacnControllerEditor(handler: RHandler<SacnControllerEditorProps>) =
    child(SacnControllerEditorView, handler = handler)