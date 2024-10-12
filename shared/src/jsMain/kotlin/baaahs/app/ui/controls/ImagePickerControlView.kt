package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.control.OpenImagePickerControl
import baaahs.show.live.ControlProps
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val ImagePickerControlView = xComponent<ImagePickerControlProps>("ImagePickerControl") { props ->
    val appContext = useContext(appContext)
    val controlsStyles = appContext.allStyles.controls

    val imagePickerControl = props.imagePickerControl

    div(+props.imagePickerControl.inUseStyle) {
        imagePicker {
            attrs.gadget = imagePickerControl.imagePicker
        }

        div(+controlsStyles.feedTitle) { +imagePickerControl.imagePicker.title }
    }
}

external interface ImagePickerControlProps : Props {
    var controlProps: ControlProps
    var imagePickerControl: OpenImagePickerControl
}

fun RBuilder.imagePickerControl(handler: RHandler<ImagePickerControlProps>) =
    child(ImagePickerControlView, handler = handler)