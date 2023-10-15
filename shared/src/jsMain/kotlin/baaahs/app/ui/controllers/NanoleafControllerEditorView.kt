package baaahs.app.ui.controllers

import baaahs.app.ui.appContext
import baaahs.controller.MutableNanoleafControllerConfig
import baaahs.scene.EditingController
import baaahs.ui.unaryMinus
import baaahs.ui.value
import baaahs.ui.xComponent
import js.core.jso
import mui.material.Container
import mui.material.TextField
import mui.system.sx
import react.*
import react.dom.onChange
import web.cssom.Display
import web.cssom.FlexDirection

private val NanoleafControllerEditorView = xComponent<NanoleafControllerEditorProps>("NanoleafControllerEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val mutableConfig = props.editingController.config

    val handleTitleChange by formEventHandler(mutableConfig, props.editingController) {
        mutableConfig.title = it.target.value
        props.editingController.onChange()
    }

    val handleHostNameChange by formEventHandler(mutableConfig, props.editingController) {
        mutableConfig.hostName = it.target.value
        props.editingController.onChange()
    }

    val handlePortChange by formEventHandler(mutableConfig, props.editingController) {
        mutableConfig.port = it.target.value.toInt()
        props.editingController.onChange()
    }

    val handleAccessTokenChange by formEventHandler(mutableConfig, props.editingController) {
        mutableConfig.accessToken = it.target.value
        props.editingController.onChange()
    }

    Container {
        attrs.classes = jso { root = -styles.propertiesEditSection }
        attrs.sx {
            display = Display.flex
            flexDirection = FlexDirection.column
        }

        TextField {
            attrs.label = buildElement { +"Title" }
            attrs.fullWidth = true
            attrs.value = mutableConfig.title
            attrs.onChange = handleTitleChange
        }

        TextField {
            attrs.label = buildElement { +"Host" }
            attrs.fullWidth = true
            attrs.value = mutableConfig.hostName
            attrs.onChange = handleHostNameChange
        }

        TextField {
            attrs.label = buildElement { +"Port" }
            attrs.fullWidth = true
            attrs.value = mutableConfig.port.toString()
            attrs.onChange = handlePortChange
        }

        TextField {
            attrs.label = buildElement { +"Access Token" }
            attrs.fullWidth = true
            attrs.value = mutableConfig.accessToken
            attrs.onChange = handleAccessTokenChange
        }

//        with (appContext.allStyles.modelEditor) {
//            numberTextField("Universes", mutableConfig.universes, onChange = handleUniversesChange)
//        }
    }
}

external interface NanoleafControllerEditorProps : Props {
    var editingController: EditingController<MutableNanoleafControllerConfig>
}

fun RBuilder.nanoleafControllerEditor(handler: RHandler<NanoleafControllerEditorProps>) =
    child(NanoleafControllerEditorView, handler = handler)