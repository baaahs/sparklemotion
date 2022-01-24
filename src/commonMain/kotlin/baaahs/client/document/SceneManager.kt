package baaahs.client.document

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.app.ui.UiActions
import baaahs.client.Notifier
import baaahs.doc.SceneDocumentType
import baaahs.io.RemoteFsSerializer
import baaahs.plugin.Plugins
import baaahs.scene.MutableScene
import baaahs.scene.OpenScene
import baaahs.scene.Scene
import baaahs.scene.SceneMonitor
import baaahs.show.mutable.MutableDocument
import baaahs.ui.DialogHolder
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

        onNew(Scene.Empty)
    }

    override suspend fun onDownload() {
        UiActions.downloadScene(document!!, plugins)
    }

    override fun switchTo(documentState: DocumentState<Scene, Unit>?) {
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
        sceneMonitor.onChange(openScene)
        facade.notifyChanged()
    }

    inner class Facade : DocumentManager<Scene, Unit>.Facade() {
        val scene get() = this@SceneManager.document
        val openScene get() = this@SceneManager.openScene

        override fun onEdit(mutableDocument: MutableDocument<Scene>, pushToUndoStack: Boolean) {
            onEdit(mutableDocument.build(), Unit, pushToUndoStack)
        }

        override fun onEdit(document: Scene, pushToUndoStack: Boolean) {
            onEdit(document, Unit, pushToUndoStack)
        }
    }
}