package baaahs.client.document

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.app.ui.UiActions
import baaahs.client.Notifier
import baaahs.doc.SceneDocumentType
import baaahs.io.RemoteFsSerializer
import baaahs.io.resourcesFs
import baaahs.mapper.Storage
import baaahs.plugin.Plugins
import baaahs.scene.MutableScene
import baaahs.scene.OpenScene
import baaahs.scene.Scene
import baaahs.scene.SceneMonitor
import baaahs.show.mutable.MutableDocument
import baaahs.ui.DialogHolder
import baaahs.ui.DialogMenuOption.Divider
import baaahs.ui.DialogMenuOption.Option
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
                val newScene = build()
                onNew(newScene)
                dialogHolder.closeDialog()
            }
        }

        dialogHolder.showMenuDialog("New ${documentType.title}…", listOf(
            Option("Empty Scene") { makeNew { null } },
            Divider,
            Option("BAAAHS") {
                makeNew {
                    val file = resourcesFs.resolve("BAAAHS.scene")
                    Storage(resourcesFs, plugins).loadScene(file)?.let {
                        it.copy(model = it.model.copy(title = "New Scene"))
                    } ?: error("Couldn't find scene")
                }
            },
            Option("Demo") {
                makeNew {
                    val file = resourcesFs.resolve("Demo.scene")
                    Storage(resourcesFs, plugins).loadScene(file)?.let {
                        it.copy(model = it.model.copy(title = "New Scene"))
                    } ?: error("Couldn't find scene")
                }
            },
            Option("Honcho") {
                makeNew {
                    val file = resourcesFs.resolve("Honcho.scene")
                    Storage(resourcesFs, plugins).loadScene(file)?.let {
                        it.copy(model = it.model.copy(title = "New Scene"))
                    } ?: error("Couldn't find scene")
                }
            },
            Option("Playa2021") {
                makeNew {
                    val file = resourcesFs.resolve("Playa2021.scene")
                    Storage(resourcesFs, plugins).loadScene(file)?.let {
                        it.copy(model = it.model.copy(title = "New Scene"))
                    } ?: error("Couldn't find scene")
                }
            }
        ))
    }

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