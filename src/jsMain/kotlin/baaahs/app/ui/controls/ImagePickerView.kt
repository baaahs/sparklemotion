package baaahs.app.ui.controls

import baaahs.Color
import baaahs.Gadget
import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.app.ui.document.fileUpload
import baaahs.doc.FileType
import baaahs.encodeBase64
import baaahs.gadgets.ImagePicker
import baaahs.gadgets.ImageRef
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import csstype.*
import kotlinx.js.jso
import materialui.icon
import mui.material.Box
import mui.material.Modal
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.files.FileReader
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.button
import react.dom.div
import react.dom.onClick
import react.useContext

private val ImagePickerView = xComponent<ImagePickerProps>("ImagePicker") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    val handleChangeFromUi by handler { newColors: Array<Color> ->
//        colors = newColors
//        props.gadget.color = newColors[0]
    }

    val handleChangeFromServer by handler { _: Gadget ->
//        colors = arrayOf(props.gadget.color)
    }

    var showUpload by state { false }
    val handleUploadFile by mouseEventHandler { showUpload = true }

    val handleFileUploaded by handler { name: String, contentType: String, result: ArrayBuffer ->
        console.log("file uploaded: $name", result)
        val buf = Uint8Array(result)
        val b = ByteArray(result.byteLength) { i -> buf[i] }
        val dataUrl = "data:$contentType;base64," + encodeBase64(b)
        props.gadget.imageRef = ImageRef.Local(dataUrl)
    }

//    onMount(props.gadget, handleChangeFromServer) {
//        props.gadget.listen(handleChangeFromServer)
//
//        withCleanup {
//            props.gadget.unlisten(handleChangeFromServer)
//        }
//    }


    div(+styles.wrapper) {
        button {
            attrs.onClick = handleUploadFile

            icon(CommonIcons.Upload.getReactIcon())
        }
    }



    if (showUpload) {
        Modal {
            attrs.open = true

            Box {
                attrs.sx = jso {
                    this.position = Position.absolute
                    this.top = 50.pct
                    this.left = 50.pct
                    this.transform = translate((-50).pct)
                    this.width = 400.px
                    this.backgroundColor = Color("background.paper")
                    this.border = Border(2.px, LineStyle.solid, NamedColor.black)
                    this.boxShadow = "24".unsafeCast<BoxShadow>()
                    this.padding = 4.px
                }

                fileUpload {
                    attrs.fileType = FileType.Image
                    attrs.onUpload = { files, rejections ->
                        console.log("Uploaded!", files)

                        files.forEach { file ->
                            val reader = FileReader()

                            reader.onabort = { console.log("file reading was aborted") }
                            reader.onerror = { console.log("file reading has failed") }
                            reader.onload = {
                                // Do whatever you want with the file contents
                                val result = reader.result
                                globalLaunch {
                                    handleFileUploaded(file.name, file.type, result)
                                    showUpload = false
                                }
                            }
                            reader.readAsArrayBuffer(file)
                        }
                    }
                    attrs.onCancel = { showUpload = false }
                }
            }
        }
    }
}

external interface ImagePickerProps : Props {
    var gadget: ImagePicker
}

fun RBuilder.imagePicker(handler: RHandler<ImagePickerProps>) =
    child(ImagePickerView, handler = handler)