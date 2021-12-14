package baaahs.client.document

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.app.ui.dialog.FileDialog
import baaahs.app.ui.document.DialogHolder
import baaahs.client.Notifier
import baaahs.doc.SceneDocumentType
import baaahs.gl.Toolchain
import baaahs.io.RemoteFsSerializer
import baaahs.scene.Scene

class SceneManager(
    pubSub: PubSub.Client,
    remoteFsSerializer: RemoteFsSerializer,
    toolchain: Toolchain,
    notifier: Notifier,
    fileDialog: FileDialog
) : DocumentManager<Scene>(
    SceneDocumentType, pubSub, remoteFsSerializer, toolchain, notifier, fileDialog, Scene.serializer()
) {
    val facade = Facade()

    private val sceneEditStateChannel =
        pubSub.subscribe(
            Scene.createTopic(toolchain.plugins.serialModule, remoteFsSerializer)
        ) { incoming ->
            switchTo(incoming)
//            undoStack.reset(incoming)
            facade.notifyChanged()
        }

    override suspend fun onNew(dialogHolder: DialogHolder) {
        if (!confirmCloseIfUnsaved()) return

        onNew(Scene.Empty)
    }

    override suspend fun onDownload() {
        TODO("scene download not implemented")
    }

    private fun switchTo(documentState: DocumentState<Scene, Unit>?) {
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
    }

    inner class Facade : DocumentManager<*>.Facade() {
        val scene get() = this@SceneManager.document
    }
}