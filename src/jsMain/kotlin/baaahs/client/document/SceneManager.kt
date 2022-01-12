package baaahs.client.document

import baaahs.DocumentState
import baaahs.ModelProvider
import baaahs.PubSub
import baaahs.app.ui.dialog.FileDialog
import baaahs.app.ui.document.DialogHolder
import baaahs.client.Notifier
import baaahs.doc.SceneDocumentType
import baaahs.gl.Toolchain
import baaahs.io.RemoteFsSerializer
import baaahs.model.Model
import baaahs.scene.MutableScene
import baaahs.scene.OpenScene
import baaahs.scene.Scene
import baaahs.show.mutable.MutableDocument
import kotlinx.coroutines.CompletableDeferred

class SceneManager(
    pubSub: PubSub.Client,
    remoteFsSerializer: RemoteFsSerializer,
    toolchain: Toolchain,
    notifier: Notifier,
    fileDialog: FileDialog
) : DocumentManager<Scene, Unit>(
    SceneDocumentType, pubSub, Scene.createTopic(toolchain.plugins.serialModule, remoteFsSerializer),
    remoteFsSerializer, toolchain, notifier, fileDialog, Scene.serializer()
), ModelProvider {
    override val facade = Facade()

    private val deferredScene: CompletableDeferred<OpenScene> = CompletableDeferred()
    private lateinit var mutableScene: MutableScene

    override suspend fun onNew(dialogHolder: DialogHolder) {
        if (!confirmCloseIfUnsaved()) return

        onNew(Scene.Empty)
    }

    override suspend fun onDownload() {
        TODO("scene download not implemented")
    }

    suspend fun getScene(): OpenScene {
        return deferredScene.await()
    }

    override suspend fun getModel(): Model {
        return deferredScene.await().model
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
        if (newScene != null) {
            deferredScene.complete(newScene.open())
        }
    }

    inner class Facade : DocumentManager<Scene, Unit>.Facade() {
        val scene get() = this@SceneManager.document

        override fun onEdit(mutableDocument: MutableDocument<Scene>, pushToUndoStack: Boolean) {
            onEdit(mutableDocument.build(), Unit, pushToUndoStack)
        }

        override fun onEdit(document: Scene, pushToUndoStack: Boolean) {
            onEdit(document, Unit, pushToUndoStack)
        }
    }
}