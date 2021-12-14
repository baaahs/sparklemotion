package baaahs.app.ui.dialog

import baaahs.doc.FileDisplay
import baaahs.doc.FileType
import baaahs.io.Fs
import baaahs.ui.Observable
import kotlinx.coroutines.channels.Channel

class FileDialog : Observable() {
    var fileRequest: FileRequest? = null
        private set

    private val channel: Channel<Fs.File?> = Channel(Channel.RENDEZVOUS)

    suspend fun open(fileType: FileType, defaultTarget: Fs.File? = null, title: String? = null): Fs.File? {
        if (fileRequest != null) error("File dialog already open!")
        fileRequest = FileRequest(title, false, fileType, defaultTarget)
        notifyChanged()
        return channel.receive()
    }

    suspend fun saveAs(fileType: FileType, defaultTarget: Fs.File? = null, title: String? = null): Fs.File? {
        if (fileRequest != null) error("File dialog already open!")
        fileRequest = FileRequest(title, true, fileType, defaultTarget)
        notifyChanged()
        return channel.receive()
    }

    suspend fun onSelect(file: Fs.File) {
        channel.send(file)
        fileRequest = null
        notifyChanged()
    }

    suspend fun onCancel() {
        channel.send(null)
        fileRequest = null
        notifyChanged()
    }

    fun adjustFileDisplay(file: Fs.File, fileDisplay: FileDisplay) {
        fileRequest?.fileType?.adjustFileDisplay(file, fileDisplay)
    }

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