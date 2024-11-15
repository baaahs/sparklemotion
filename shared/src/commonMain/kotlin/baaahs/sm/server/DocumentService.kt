package baaahs.sm.server

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.client.document.DataStore
import baaahs.doc.DocumentType
import baaahs.io.Fs
import baaahs.io.RemoteFsSerializer
import baaahs.sm.webapi.DocumentCommands
import baaahs.ui.Observable
import baaahs.util.Logger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule

abstract class DocumentService<T : Any, TState>(
    pubSub: PubSub.Server,
    private val dataStore: DataStore<T>,
    private val dataDir: Fs.File,
    topic: PubSub.Topic<DocumentState<T, TState>?>,
    tSerializer: KSerializer<T>,
    remoteFsSerializer: RemoteFsSerializer,
    serializersModule: SerializersModule,
    val documentType: DocumentType
) : Observable() {
    internal abstract val logger: Logger

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
        val rpc = documentType.getRpcImpl(tSerializer, SerializersModule {
            include(remoteFsSerializer.serialModule)
            include(serializersModule)
        })
        rpc.createReceiver(pubSub, DocumentCommandsHandler())
    }

    abstract fun createDocument(): T
    abstract fun onFileChanged(saveAsFile: Fs.File)
    protected abstract fun getDocumentState(): DocumentState<T, TState>?

    suspend fun load(file: Fs.File): T? =
        dataStore.load(file)

    suspend fun save(file: Fs.File, document: T) =
        dataStore.save(file, document, allowOverwrite = true)

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

    inner class DocumentCommandsHandler : DocumentCommands<T> {
        override suspend fun new(template: T?) {
            logger.info { "Creating new document" }
            switchTo(template ?: createDocument())
        }

        override suspend fun switchTo(file: Fs.File?) {
            logger.info { "Switching to $file" }
            if (file != null) {
                switchTo(load(file), file = file, isUnsaved = false)
            } else {
                switchTo(null, null, null)
            }
        }

        override suspend fun save() {
            logger.info { "Saving $file" }
            file?.let { file ->
                document?.let { document -> doSave(file, document) }
            }
        }

        override suspend fun saveAs(file: Fs.File) {
            logger.info { "Saving as $file" }
            val saveAsFile = dataDir.resolve(file.fullPath)
            document?.let { document -> doSave(saveAsFile, document) }
            onFileChanged(saveAsFile)
        }
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

        notifyChanged()
    }

    fun release() {
        documentStateChannel.unsubscribe()
    }
}