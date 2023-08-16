package baaahs.app.ui.document

import FileRejection
import baaahs.app.ui.appContext
import baaahs.doc.FileType
import baaahs.ui.*
import dropzone
import js.core.jso
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
import web.cssom.em

private val FileUploadView = xComponent<FileUploadProps>("FileUpload") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.fileUploadStyles
    val fileType = props.fileType

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
        if (props.maxFiles != null) {
            attrs.maxFiles = props.maxFiles
        }
        attrs.onDrop = props.onUpload
        attrs.onFileDialogCancel

        renderDropzone { dropzoneState ->
            div(+styles.container) {
                mixin(dropzoneState.getRootProps())

                div(+styles.upload
                        and dropzoneState.isDragAccept.then(styles.dragAccept)
                        and dropzoneState.isDragActive.then(styles.dragActive)
                        and dropzoneState.isDragReject.then(styles.dragReject)
                        and dropzoneState.isFileDialogActive.then(styles.fileDialogActive)
                        and dropzoneState.isFocused.then(styles.focused)
                ) {
                    input {
                        mixin(dropzoneState.getInputProps())
                    }

                    icon(FileUpload) {
                        fontSize = SvgIconSize.large
                        sx = jso { margin = .25.em }
                    }

                    p {
                        if (dropzoneState.isDragAccept) { +"dragAccept" }
                        if (dropzoneState.isDragActive) { +"dragActive" }
                        if (dropzoneState.isDragReject) { +"dragReject" }
                        if (dropzoneState.isFileDialogActive) { +"fileDialogActive" }
                        if (dropzoneState.isFocused) { +"focused" }

                        if (dropzoneState.isDragReject) {
                            +"That's not ${fileType.indefiniteTitleLower}."
                        } else if (dropzoneState.isDragActive) {
                            +"yom yom it's ${fileType.indefiniteTitleLower}!"
                        } else {
                            +"Drop ${fileType.indefiniteTitleLower} here, or "
                            span(+styles.linkink) { +"browse" }
                            +"…"
                        }
                    }

                    if (dropzoneState.acceptedFiles.isNotEmpty()) {
                        div {
                            header { +"Accepted: " }

                            ul {
                                dropzoneState.acceptedFiles.forEach { file ->
                                    li { +file.name }
                                }
                            }
                        }
                    }

                    if (dropzoneState.fileRejections.isNotEmpty()) {
                        div {
                            header { +"Rejected: " }

                            ul {
                                dropzoneState.fileRejections.forEach { rejection ->
                                    li {
                                        +rejection.file.name
                                        p { +": " }
                                        +rejection.errors.joinToString()
                                    }
                                }
                            }
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
    var maxFiles: Int?
    var onUpload: (acceptedFiles: Array<File>, fileRejections: Array<FileRejection>) -> Unit
    var onCancel: () -> Unit
}

fun RBuilder.fileUpload(handler: RHandler<FileUploadProps>) =
    child(FileUploadView, handler = handler)
