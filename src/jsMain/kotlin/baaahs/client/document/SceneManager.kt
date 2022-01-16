package baaahs.client.document

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.app.ui.UiActions
import baaahs.app.ui.dialog.FileDialog
import baaahs.app.ui.document.DialogHolder
import baaahs.client.Notifier
import baaahs.doc.SceneDocumentType
import baaahs.io.RemoteFsSerializer
import baaahs.plugin.Plugins
import baaahs.scene.*
import baaahs.show.mutable.MutableDocument

class SceneManager(
    pubSub: PubSub.Client,
    remoteFsSerializer: RemoteFsSerializer,
    private val plugins: Plugins,
    notifier: Notifier,
    fileDialog: FileDialog
) : DocumentManager<Scene, Unit>(
    SceneDocumentType, pubSub, Scene.createTopic(plugins.serialModule, remoteFsSerializer),
    remoteFsSerializer, plugins, notifier, fileDialog, Scene.serializer()
), SceneProvider {
    override val facade = Facade()

    private val sceneChangeListeners = mutableListOf<SceneChangeListener>()
    override var openScene: OpenScene? = null
    private var mutableScene: MutableScene? = null

    override suspend fun onNew(dialogHolder: DialogHolder) {
        if (!confirmCloseIfUnsaved()) return

        onNew(Scene.Empty)
    }

    override suspend fun onDownload() {
        UiActions.downloadScene(document!!, plugins)
    }

    override fun addSceneChangeListener(callback: SceneChangeListener): SceneChangeListener {
        sceneChangeListeners.add(callback)
        return callback
    }

    override fun removeSceneChangeListener(callback: SceneChangeListener) {
        sceneChangeListeners.remove(callback)
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
        sceneChangeListeners.forEach { it(openScene) }
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