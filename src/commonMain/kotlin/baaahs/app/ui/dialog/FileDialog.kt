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

    override suspend fun open(fileType: FileType, defaultTarget: Fs.File?, title: String?): Fs.File? {
        if (fileRequest != null) error("File dialog already open!")
        fileRequest = FileRequest(title, false, fileType, defaultTarget)
        notifyChanged()
        return channel.receive()
    }

    override suspend fun saveAs(fileType: FileType, defaultTarget: Fs.File?, title: String?): Fs.File? {
        if (fileRequest != null) error("File dialog already open!")
        fileRequest = FileRequest(title, true, fileType, defaultTarget)
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
        title: String?,
        val isSaveAs: Boolean = false,
        val fileType: FileType = FileType.Any,
        val defaultTarget: Fs.File? = null
    ) {
        val title = title
            ?: if (isSaveAs) "Save ${fileType.title} As…" else "Open ${fileType.title}…"
    }
}