package baaahs.sm.server

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.doc.DocumentType
import baaahs.io.Fs
import baaahs.io.RemoteFsSerializer
import baaahs.mapper.Storage
import baaahs.sm.webapi.NewCommand
import baaahs.sm.webapi.Topics
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule

abstract class DocumentService<T, TState>(
    pubSub: PubSub.Server,
    private val storage: Storage,
    topic: PubSub.Topic<DocumentState<T, TState>?>,
    tSerializer: KSerializer<T>,
    remoteFsSerializer: RemoteFsSerializer,
    serializersModule: SerializersModule,
    val documentType: DocumentType
) {
    var document: T? = null
        private set
    var file: Fs.File? = null
        private set
    var isUnsaved: Boolean = false
        private set

    private val documentStateChannel: PubSub.Channel<DocumentState<T, TState>?> =
        pubSub.publish(
            topic,
            getDocumentState()
        ) { incoming ->
            val newShow = incoming?.document
            val newShowState = incoming?.state
            val newIsUnsaved = incoming?.isUnsaved ?: false
            switchTo(
                newShow, newShowState, file,
                newIsUnsaved, fromClientUpdate = true
            )
        }

    init {
        val commands = Topics.DocumentCommands(documentType.channelName, tSerializer, SerializersModule {
            include(remoteFsSerializer.serialModule)
            include(serializersModule)
        })
        pubSub.listenOnCommandChannel(commands.newCommand) { command -> handleNew(command) }
        pubSub.listenOnCommandChannel(commands.switchToCommand) { command -> handleSwitchTo(command.file) }
        pubSub.listenOnCommandChannel(commands.saveCommand) { command -> handleSave() }
        pubSub.listenOnCommandChannel(commands.saveAsCommand) { command ->
            val saveAsFile = storage.resolve(command.file.fullPath)
            handleSaveAs(saveAsFile)
            onFileChanged(saveAsFile)
        }
    }

    abstract fun createDocument(): T
    abstract suspend fun load(file: Fs.File): T?
    abstract suspend fun save(file: Fs.File, document: T)
    abstract fun onFileChanged(saveAsFile: Fs.File)
    protected abstract fun getDocumentState(): DocumentState<T, TState>?

    open fun notifyOfDocumentChanges(fromClientUpdate: Boolean = false) {
        if (!fromClientUpdate) {
            documentStateChannel.onChange(getDocumentState())
        }
    }

    protected fun update(document: T?, file: Fs.File?, isUnsaved: Boolean) {
        this.document = document
        this.file = file
        this.isUnsaved = isUnsaved
    }

    private suspend fun handleNew(command: NewCommand<T>) {
        switchTo(command.template ?: createDocument())
    }

    private suspend fun handleSwitchTo(file: Fs.File?) {
        if (file != null) {
            switchTo(load(file), file = file, isUnsaved = false)
        } else {
            switchTo(null, null, null)
        }
    }

    private suspend fun handleSave() {
        file?.let { file ->
            document?.let { document -> doSave(file, document) }
        }
    }

    private suspend fun handleSaveAs(file: Fs.File) {
        document?.let { document -> doSave(file, document) }
    }

    private suspend fun doSave(file: Fs.File, document: T) {
        save(file, document)
        this.file = file
        isUnsaved = false
        notifyOfDocumentChanges()
    }

    open fun switchTo(
        newDocument: T?,
        newState: TState? = null,
        file: Fs.File? = null,
        isUnsaved: Boolean = file == null,
        fromClientUpdate: Boolean = false
    ) {
        this.document = newDocument
        this.file = file
        this.isUnsaved = isUnsaved
    }

    fun release() {
        documentStateChannel.unsubscribe()
    }
}