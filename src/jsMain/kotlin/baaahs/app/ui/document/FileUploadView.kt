package baaahs.app.ui.document

import FileRejection
import baaahs.app.ui.appContext
import baaahs.doc.FileType
import baaahs.ui.withMouseEvent
import baaahs.ui.xComponent
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.get
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.button
import react.dom.div
import react.dom.input
import react.dom.onClick
import react.useContext

private val FileUploadView = xComponent<FileUploadProps>("FileUpload") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.fileUploadStyles
    val fileType = props.fileType

    div {

        input {
            attrs.type = InputType.file
            attrs.onChangeFunction = onchangeLambda@{ e ->
                val files = e.target.unsafeCast<HTMLInputElement>().files ?: return@onchangeLambda
                val filesArr = arrayOf<File>().toMutableList()
                for (i in 0..files.length) {
                    val file = files[i]
                    if (file != null) {
                        filesArr.add(file)
                    }
                }

                props.onUpload(filesArr.toTypedArray(), arrayOf())
                console.log(e)
            }
            if (fileType.contentTypeMasks.isNotEmpty() || fileType.matchingExtensions.isNotEmpty()) {
                attrs.accept = fileType.matchingExtensions.plus(fileType.contentTypeMasks).joinToString(separator = ", ")
                console.log("accept:", attrs.accept)
            }

        }
        button {
            attrs.onClick = props.onCancel.withMouseEvent()
            +"Cancel"
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