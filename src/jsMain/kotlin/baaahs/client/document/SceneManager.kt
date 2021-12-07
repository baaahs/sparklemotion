package baaahs.client.document

import baaahs.PubSub
import baaahs.app.ui.dialog.FileDialog
import baaahs.app.ui.dialog.FileType
import baaahs.app.ui.document.DialogHolder
import baaahs.client.Notifier
import baaahs.gl.Toolchain
import baaahs.io.RemoteFsSerializer
import baaahs.show.Show

class SceneManager(
    pubSub: PubSub.Client,
    remoteFsSerializer: RemoteFsSerializer,
    toolchain: Toolchain,
    notifier: Notifier,
    fileDialog: FileDialog
) : DocumentManager<Show>(
    "scene", "Scene", pubSub, remoteFsSerializer, toolchain, notifier, fileDialog
) {
    override val fileType: FileType
        get() = FileType.Scene

    override suspend fun onNew(dialogHolder: DialogHolder) {
        TODO("new scene not implemented")
    }

    override suspend fun onDownload() {
        TODO("scene download not implemented")
    }
}