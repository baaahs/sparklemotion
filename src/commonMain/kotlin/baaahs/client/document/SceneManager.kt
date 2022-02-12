package baaahs.client.document

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.app.ui.UiActions
import baaahs.client.Notifier
import baaahs.doc.SceneDocumentType
import baaahs.io.Fs
import baaahs.io.RemoteFsSerializer
import baaahs.io.resourcesFs
import baaahs.mapper.Storage
import baaahs.plugin.Plugins
import baaahs.scene.*
import baaahs.show.mutable.MutableDocument
import baaahs.ui.DialogHolder
import baaahs.ui.DialogMenuItem
import baaahs.ui.DialogMenuItem.Divider
import baaahs.ui.DialogMenuItem.Option
import baaahs.ui.IObservable
import baaahs.ui.Observable

class SceneManager(
    pubSub: PubSub.Client,
    remoteFsSerializer: RemoteFsSerializer,
    private val plugins: Plugins,
    notifier: Notifier,
    fileDialog: IFileDialog,
    private val sceneMonitor: SceneMonitor
) : DocumentManager<Scene, Unit>(
    SceneDocumentType, pubSub, Scene.createTopic(plugins.serialModule, remoteFsSerializer),
    remoteFsSerializer, plugins, notifier, fileDialog, Scene.serializer()
), IObservable by Observable() {
    override val facade = Facade()

    private var openScene: OpenScene? = null
    private var mutableScene: MutableScene? = null

    override suspend fun onNew(dialogHolder: DialogHolder) {
        if (!confirmCloseIfUnsaved()) return

        fun makeNew(build: suspend () -> Scene?) {
            launch {
                dialogHolder.closeDialog()
                val scene = build()?.withInlinedImports()
                onNew(scene)
            }
        }

        dialogHolder.showMenuDialog("New ${documentType.title}…", listOf(
            Option("Empty Scene") { makeNew { null } },
            Divider,
            DialogMenuItem.Header("From Template:"),
            Option("BAAAHS") { makeNew { sceneFromResources("BAAAHS.scene") } },
            Option("Demo") { makeNew { sceneFromResources("Demo.scene") } },
            Option("Honcho") { makeNew { sceneFromResources("Honcho.scene") } },
            Option("Playa2021") { makeNew { sceneFromResources("Playa2021.scene") } }
        ))
    }

    // Cheating here, since opening a Scene happens synchronously, we can't use file operations.
    // Need to figure out how to handle that.
    private suspend fun Scene.withInlinedImports(): Scene {
        return edit()
            .apply {
                model.entities.forEachIndexed { index, mutableEntity ->
                    if (mutableEntity is MutableImportedEntity && mutableEntity.objDataIsFileRef) {
                        mutableEntity.objData = fileFromResources(mutableEntity.objData).read()
                            ?: error("Couldn't find ${mutableEntity.objData} in resources.")
                        mutableEntity.objDataIsFileRef = false
                    }
                }
            }.build()
    }

    private suspend fun sceneFromResources(fileName: String): Scene {
        val file = fileFromResources(fileName)
        return Storage(resourcesFs, plugins).loadScene(file)?.let {
            it.copy(model = it.model.copy(title = "New Scene"))
        } ?: error("Couldn't find scene")
    }

    private fun fileFromResources(fileName: String): Fs.File =
        resourcesFs.resolve("templates", "scenes", fileName)

    override suspend fun onDownload() {
        UiActions.downloadScene(document!!, plugins)
    }

    override fun switchTo(documentState: DocumentState<Scene, Unit>?, isLocalEdit: Boolean) {
        val newScene = documentState?.document
        val newSceneState = documentState?.state
        val newIsUnsaved = documentState?.isUnsaved ?: false
        val newFile = documentState?.file
        val newOpenScene = newScene?.let {
//            stageManager.openScene(newScene, newSceneState)
        }
//        openScene?.disuse()
//        openScene = newOpenScene?.also { it.use() }

        update(newScene, newFile, newIsUnsaved)
        openScene = newScene?.open()
        if (!isLocalEdit) mutableScene = null
        sceneMonitor.onChange(openScene)
        facade.notifyChanged()
    }

    private fun edit(): MutableScene =
        mutableScene ?: run {
            (document ?: error("No open scene.")).edit().also {
                mutableScene = it
            }
        }

    inner class Facade : DocumentManager<Scene, Unit>.Facade() {
        val scene get() = this@SceneManager.document
        val openScene get() = this@SceneManager.openScene
        val mutableScene get() = this@SceneManager.edit()

        /** Ugh super janky. */
        private var retainMutableDocument = false

        override fun onEdit(document: Scene, documentState: Unit, pushToUndoStack: Boolean) {
            if (!retainMutableDocument)
                this@SceneManager.mutableScene = null

            super.onEdit(document, documentState, pushToUndoStack)
        }

        override fun onEdit(mutableDocument: MutableDocument<Scene>, pushToUndoStack: Boolean) {
            this@SceneManager.mutableScene = mutableDocument as MutableScene
            retainMutableDocument = true
            try {
                onEdit(mutableDocument.build(), Unit, pushToUndoStack)
            } finally {
                retainMutableDocument = false
            }
        }

        override fun onEdit(document: Scene, pushToUndoStack: Boolean) {
            onEdit(document, Unit, pushToUndoStack)
        }

        fun onEdit(pushToUndoStack: Boolean = true) {
            onEdit(mutableScene, pushToUndoStack)
        }
    }
}