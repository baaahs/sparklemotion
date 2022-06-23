package baaahs.app.ui.document

import baaahs.app.ui.appContext
import baaahs.doc.FileType
import baaahs.ui.*
import csstype.em
import dropzone
import kotlinx.js.jso
import materialui.icon
import mui.icons.material.FileUpload
import mui.material.SvgIconSize
import org.w3c.files.File
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.useContext
import renderDropzone

private val FileUploadView = xComponent<FileUploadProps>("FileUpload") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.fileUploadStyles
    val fileType = props.fileType

//    val handleUploadDrop by handler { files: Array<File> ->
//
//    }

    dropzone {
        if (fileType.contentTypeMasks.isNotEmpty() || fileType.matchingExtensions.isNotEmpty()) {
            val accept = jso<Any>().asDynamic()
            fileType.contentTypeMasks.ifEmpty { listOf("*/*") }
                .forEach { contentType ->
                    accept[contentType] = fileType.matchingExtensions.toTypedArray()
                }
            attrs.accept = accept
            console.log("accept:", accept)
        }
        attrs.onDrop = props.onUpload
        attrs.onFileDialogCancel

        renderDropzone { callbackProps ->
            div(+styles.container) {
                mixin(callbackProps.getRootProps())

                div(+styles.upload
                        and callbackProps.isDragAccept.then(styles.dragAccept)
                        and callbackProps.isDragActive.then(styles.dragActive)
                        and callbackProps.isDragReject.then(styles.dragReject)
                        and callbackProps.isFileDialogActive.then(styles.fileDialogActive)
                        and callbackProps.isFocused.then(styles.focused)
                ) {
                    input {
                        mixin(callbackProps.getInputProps())
                    }

                    icon(FileUpload) {
                        fontSize = SvgIconSize.large
                        sx = jso { margin = .25.em }
                    }

                    p {
                        if (callbackProps.isDragReject) {
                            +"That's not ${fileType.indefiniteTitleLower}."
                        } else if (callbackProps.isDragActive) {
                            +"yom yom it's ${fileType.indefiniteTitleLower}!"
                        } else {
                            +"Drop ${fileType.indefiniteTitleLower} here, or "
                            span(+styles.linkink) { +"browse" }
                            +"…"
                        }
                    }
                }
            }

            button {
                attrs.onClick = props.onCancel.withMouseEvent()
                +"Cancel"
            }
        }
    }
}

external interface FileUploadProps : Props {
    var fileType: FileType
    var onUpload: (Array<File>) -> Unit
    var onCancel: () -> Unit
}

fun RBuilder.fileUpload(handler: RHandler<FileUploadProps>) =
    child(FileUploadView, handler = handler)