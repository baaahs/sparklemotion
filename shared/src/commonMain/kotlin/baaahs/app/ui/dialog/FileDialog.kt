package baaahs.app.ui.dialog

import baaahs.client.document.IFileDialog
import baaahs.doc.FileDisplay
import baaahs.doc.FileType
import baaahs.io.Fs
import baaahs.ui.Observable
import kotlinx.coroutines.channels.Channel

class FileDialog : Observable(), IFileDialog {
    var fileRequest: FileRequest? = null
        private set

    private val channel: Channel<Fs.File?> = Channel(Channel.RENDEZVOUS)

    override suspend fun open(fileType: FileType, defaultFile: Fs.File?): Fs.File? {
        if (fileRequest != null) error("File dialog already open!")
        fileRequest = FileRequest(isSaveAs = false, fileType, defaultFile = defaultFile)
        notifyChanged()
        return channel.receive()
    }

    override suspend fun saveAs(fileType: FileType, defaultFile: Fs.File?, defaultFileName: String?): Fs.File? {
        if (fileRequest != null) error("File dialog already open!")
        fileRequest = FileRequest(isSaveAs = true, fileType, defaultFile, defaultFileName)
        notifyChanged()
        return channel.receive()
    }

    override suspend fun onSelect(file: Fs.File) {
        fileRequest = null
        notifyChanged()
        channel.send(file)
    }

    override suspend fun onCancel() {
        fileRequest = null
        notifyChanged()
        channel.send(null)
    }

    fun adjustFileDisplay(fileDisplay: FileDisplay): FileDisplay =
        fileRequest?.fileType?.adjustFileDisplay(fileDisplay) ?: fileDisplay

    class FileRequest(
        val isSaveAs: Boolean = false,
        val fileType: FileType = FileType.Any,
        val defaultFile: Fs.File? = null,
        val defaultFileName: String? = null,
        dialogTitle: String? = null
    ) {
        val dialogTitle = dialogTitle
            ?: if (isSaveAs) "Save ${fileType.title} As…" else "Open ${fileType.title}…"
    }
}