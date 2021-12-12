package baaahs.client.document

import baaahs.PubSub
import baaahs.app.ui.dialog.FileDialog
import baaahs.app.ui.dialog.FileType
import baaahs.app.ui.document.DialogHolder
import baaahs.client.Notifier
import baaahs.gl.Toolchain
import baaahs.io.Fs
import baaahs.io.RemoteFsSerializer
import baaahs.sm.webapi.*
import baaahs.window
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule

abstract class DocumentManager<T>(
    private val documentType: String,
    val documentTypeTitle: String,
    private val pubSub: PubSub.Client,
    private val remoteFsSerializer: RemoteFsSerializer,
    private val toolchain: Toolchain,
    private val notifier: Notifier,
    private val fileDialog: FileDialog,
    private val tSerializer: KSerializer<T>
) {
    protected abstract val fileType: FileType

    var file: Fs.File? = null
        private set
    var document: T? = null
        private set
    var isUnsaved: Boolean = false
        private set
    private var documentAsSaved: T? = null
        private set
    val isLoaded: Boolean
        get() = document != null

    private val serverCommands = object {
        private val commands = Topics.DocumentCommands(documentType, tSerializer, SerializersModule {
            include(remoteFsSerializer.serialModule)
            include(toolchain.plugins.serialModule)
        })
        val newCommand = pubSub.commandSender(commands.newCommand)
        val switchToCommand = pubSub.commandSender(commands.switchToCommand)
        val saveCommand = pubSub.commandSender(commands.saveCommand)
        val saveAsCommand = pubSub.commandSender(commands.saveAsCommand)
    }

    abstract suspend fun onNew(dialogHolder: DialogHolder)

    suspend fun onNew(newDocument: T? = null) {
        serverCommands.newCommand(NewCommand(newDocument))
    }

    suspend fun onOpen() {
        confirmCloseIfUnsaved() || return

        fileDialog.open(FileType.Show, file)
            ?.withExtension(".sparkle")
            ?.also { serverCommands.switchToCommand(SwitchToCommand(it)) }
    }

    suspend fun onOpen(file: Fs.File?) {
        serverCommands.switchToCommand(SwitchToCommand(file))
    }

    suspend fun onSave() {
        serverCommands.saveCommand(SaveCommand())
    }

    suspend fun onSaveAs() {
        fileDialog.saveAs(FileType.Show, file)
            ?.withExtension(".sparkle")
            ?.also { serverCommands.saveAsCommand(SaveAsCommand(it)) }
    }

    suspend fun onSaveAs(file: Fs.File) {
        serverCommands.saveAsCommand(SaveAsCommand(file))
    }
    
    abstract suspend fun onDownload()

    suspend fun onClose() {
        confirmCloseIfUnsaved() || return

        serverCommands.switchToCommand(SwitchToCommand(null))
    }

    protected fun update(newDocument: T?, newFile: Fs.File?, newIsUnsaved: Boolean) {
        document = newDocument
        file = newFile
        isUnsaved = newIsUnsaved
        if (!newIsUnsaved) documentAsSaved = newDocument
    }

    fun isModified(newDocument: T): Boolean {
        return documentAsSaved?.equals(newDocument) != true
    }

    protected fun confirmCloseIfUnsaved(): Boolean {
        if (!isUnsaved) return true

        // TODO: Use react dialog instead.
        return window.confirm("$documentTypeTitle is unsaved, okay to close it?")
    }

    protected fun launch(block: suspend () -> Unit) {
        notifier.facade.launchAndReportErrors(block)
    }

    open inner class Facade : baaahs.ui.Facade() {
        val documentTypeTitle get() = this@DocumentManager.documentTypeTitle
        val file get() = this@DocumentManager.file
        val isLoaded get() = this@DocumentManager.isLoaded
        val isUnsaved get() = this@DocumentManager.isUnsaved

        suspend fun onNew(dialogHolder: DialogHolder) = this@DocumentManager.onNew(dialogHolder)
        suspend fun onNew(document: T) = this@DocumentManager.onNew(document)
        suspend fun onOpen() = this@DocumentManager.onOpen()
        suspend fun onOpen(file: Fs.File) = this@DocumentManager.onOpen(file)
        suspend fun onSave() = this@DocumentManager.onSave()
        suspend fun onSaveAs() = this@DocumentManager.onSaveAs()
        suspend fun onSaveAs(file: Fs.File) = this@DocumentManager.onSaveAs(file)
        suspend fun onDownload() = this@DocumentManager.onDownload()
        suspend fun onClose() = this@DocumentManager.onClose()
    }
}