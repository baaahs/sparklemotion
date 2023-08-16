import org.w3c.dom.events.Event
import org.w3c.files.File
import react.*
import web.html.HTMLElement
import web.html.HTMLInputElement
import kotlin.js.Promise

@JsModule("react-dropzone")
@JsName("Dropzone")
@JsNonModule
external val dropzoneModule: dynamic

val dropzone: ElementType<DropzoneProps> = dropzoneModule.default

external interface FileError {
    var message: String
    var code: String
}

external interface FileRejection {
    var file: File
    var errors: Array<FileError>
}

external interface DropzoneProps : PropsWithChildren, PropsWithClassName {
    /**
     * Set accepted file types.
     * Checkout https://developer.mozilla.org/en-US/docs/Web/API/window/showOpenFilePicker types option for more information.
     * Keep in mind that mime type determination is not reliable across platforms. CSV files,
     * for example, are reported as text/plain under macOS but as application/vnd.ms-excel under
     * Windows. In some cases there might not be a mime type set at all
     * (https://github.com/react-dropzone/react-dropzone/issues/276).
    */
    var accept: Any? // object containing String -> Array<String>
    var minSize: Int?
    var maxSize: Int?
    var maxFiles: Int?
    /** If `false`, allow dropped items to take over the current browser window. Default is `true`. */
    var preventDropOnDocument: Boolean?
    /** If `true`, disables click to open the native file selection dialog. Default is `false`. */
    var noClick: Boolean?
    var noKeyboard: Boolean?
    var noDrag: Boolean?
    var noDragEventsBubbling: Boolean?
    var disabled: Boolean?
    var onDrop: ((acceptedFiles: Array<File>, fileRejections: Array<FileRejection>) -> Unit)?
    var onDropAccepted: ((files: Array<File>, event: Event) -> Unit)?
    var onDropRejected: ((fileRejections: Array<FileRejection>, event: Event) -> Unit)?
    var getFilesFromEvent: (( event: Event ) -> Promise<Array<File/* | DataTransferItem*/>>)?
    var onFileDialogCancel: (() -> Unit)?
    var onFileDialogOpen: (() -> Unit)?
    var onError: ((err: Error) -> Unit)?
    var validator: ((file: File) -> FileError/* | FileError[] | null*/)?
    var useFsAccessApi: (Boolean)?
    var autoFocus: (Boolean)?
}

external interface DropzoneState {
    /** Dropzone area is in focus. */
    val isFocused: Boolean

    /** Active drag is in progress. */
    val isDragActive: Boolean

    /** Dragged files are accepted. */
    val isDragAccept: Boolean

    /** Some dragged files are rejected. */
    val isDragReject: Boolean

    /** File dialog is opened. */
    val isFileDialogActive: Boolean

    val acceptedFiles: Array<File>

    val fileRejections: Array<FileRejection>

    val rootRef: RefObject<HTMLElement>?

    val inputRef: RefObject<HTMLInputElement>?

    /** Returns the props you should apply to the root drop container you render. */
    fun getInputProps(attrs: dynamic = definedExternally): dynamic

    /** Returns the props you should apply to hidden file input you render. */
    fun getRootProps(attrs: dynamic = definedExternally): dynamic

    /** Open the native file selection dialog. */
    fun open(): Unit
}

fun RElementBuilder<DropzoneProps>.renderDropzone(block: RBuilder.(dropzoneState: DropzoneState) -> Unit) {
    val callback = { dropzoneState: DropzoneState ->
        buildElements(RBuilder()) {
            block(dropzoneState)
        }
    }.asDynamic()
    child(callback)
}