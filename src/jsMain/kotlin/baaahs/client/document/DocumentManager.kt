package baaahs.client.document

import baaahs.PubSub
import baaahs.app.ui.dialog.FileDialog
import baaahs.app.ui.dialog.FileType
import baaahs.app.ui.document.DialogHolder
import baaahs.client.Notifier
import baaahs.gl.Toolchain
import baaahs.io.Fs
import baaahs.io.RemoteFsSerializer
import baaahs.show.Show
import baaahs.sm.webapi.*
import baaahs.ui.Observable
import baaahs.window
import kotlinx.serialization.modules.SerializersModule

abstract class DocumentManager<T>(
    private val documentType: String,
    val documentTypeTitle: String,
    private val pubSub: PubSub.Client,
    private val remoteFsSerializer: RemoteFsSerializer,
    private val toolchain: Toolchain,
    private val notifier: Notifier,
    private val fileDialog: FileDialog
) : Observable() {
    protected abstract val fileType: FileType

    var file: Fs.File? = null
        private set
    var document: T? = null
        private set
    var isUnsaved: Boolean = false
        private set
    var documentAsSaved: T? = null
        private set
    val isLoaded: Boolean
        get() = document != null

    private val serverCommands = object {
        private val commands = Topics.DocumentCommands(documentType, SerializersModule {
            include(remoteFsSerializer.serialModule)
            include(toolchain.plugins.serialModule)
        })
        val newCommand = pubSub.commandSender(commands.newCommand)
        val switchToCommand = pubSub.commandSender(commands.switchToCommand)
        val saveCommand = pubSub.commandSender(commands.saveCommand)
        val saveAsCommand = pubSub.commandSender(commands.saveAsCommand)
    }

    abstract suspend fun onNew(dialogHolder: DialogHolder)

    suspend fun onNew(newShow: Show? = null) {
        serverCommands.newCommand(NewCommand(newShow))
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

    fun update(newDocument: T?, newFile: Fs.File?, newIsUnsaved: Boolean) {
        document = newDocument
        file = newFile
        isUnsaved = newIsUnsaved
        if (!newIsUnsaved) documentAsSaved = newDocument

        notifyChanged()
    }

    fun isModified(newDocument: T): Boolean {
        return documentAsSaved?.equals(newDocument) != true
    }

    fun confirmCloseIfUnsaved(): Boolean {
        if (!isUnsaved) return true

        // TODO: Use react dialog instead.
        return window.confirm("$documentTypeTitle is unsaved, okay to close it?")
    }

    protected fun launch(block: suspend () -> Unit) {
        notifier.facade.launchAndReportErrors(block)
    }
}