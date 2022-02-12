package baaahs.io

import baaahs.PubSub
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class PubSubRemoteFsClientBackend(
    pubSub: PubSub.Client
) : FsClientSideSerializer(), RemoteFsBackend {
    override val name: String
        get() = "Backend for RemoteFs"

    override val backend: RemoteFsBackend
        get() = this

    private val commandPort = createCommandPort()

    private val pubSubChannel = pubSub.openCommandChannel(commandPort)

    private suspend fun <R : RemoteFsOp.Response> sendCommand(command: RemoteFsOp): R {
        @Suppress("UNCHECKED_CAST")
        return pubSubChannel.send(command) as R
    }

    override suspend fun listFiles(directory: Fs.File): List<Fs.File> {
        val reply: RemoteFsOp.Response.ListFilesResponse = sendCommand(RemoteFsOp.ListFiles(directory))
        return reply.files
    }

    override suspend fun loadFile(file: Fs.File): String? {
        val reply: RemoteFsOp.Response.LoadFileResponse = sendCommand(RemoteFsOp.LoadFile(file))
        return reply.contents
    }

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        return saveFile(file, content.decodeToString())
    }

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        sendCommand<RemoteFsOp.Response>(RemoteFsOp.SaveFile(file, content, allowOverwrite))
    }

    override suspend fun exists(file: Fs.File): Boolean {
        val reply: RemoteFsOp.Response.ExistsResponse = sendCommand(RemoteFsOp.Exists(file))
        return reply.exists
    }

    override suspend fun isDirectory(file: Fs.File): Boolean {
        val reply: RemoteFsOp.Response.IsDirectoryResponse = sendCommand(RemoteFsOp.IsDirectory(file))
        return reply.exists
    }

    override suspend fun renameFile(fromFile: Fs.File, toFile: Fs.File) {
        sendCommand<RemoteFsOp.Response>(RemoteFsOp.RenameFile(fromFile, toFile))
    }

    override suspend fun delete(file: Fs.File) {
        sendCommand<RemoteFsOp.Response>(RemoteFsOp.Delete(file))
    }
}

@Polymorphic
@Serializable
sealed class RemoteFsOp {
    abstract suspend fun perform(): Response

    @Serializable
    @SerialName("ListFiles")
    data class ListFiles(val directory: Fs.File) : RemoteFsOp() {
        override suspend fun perform(): Response = Response.ListFilesResponse(directory.listFiles())
    }

    @Serializable
    @SerialName("LoadFile")
    class LoadFile(val file: Fs.File) : RemoteFsOp() {
        override suspend fun perform(): Response = Response.LoadFileResponse(file.read())
    }

    @Serializable
    @SerialName("SaveFile")
    class SaveFile(val file: Fs.File, val content: String, val allowOverwrite: Boolean) : RemoteFsOp() {
        override suspend fun perform(): Response {
            file.write(content, allowOverwrite)
            return Response.SaveFileResponse()
        }
    }

    @Serializable
    @SerialName("Exists")
    class Exists(val file: Fs.File) : RemoteFsOp() {
        override suspend fun perform(): Response = Response.ExistsResponse(file.exists())
    }

    @Serializable
    @SerialName("IsDirectory")
    class IsDirectory(val file: Fs.File) : RemoteFsOp() {
        override suspend fun perform(): Response = Response.IsDirectoryResponse(file.isDir())
    }

    @Serializable
    @SerialName("RenameFile")
    class RenameFile(val fromFile: Fs.File, val toFile: Fs.File) : RemoteFsOp() {
        override suspend fun perform(): Response {
            fromFile.renameTo(toFile)
            return Response.RenameFileResponse()
        }
    }

    @Serializable
    @SerialName("Delete")
    class Delete(val file: Fs.File) : RemoteFsOp() {
        override suspend fun perform(): Response {
            file.delete()
            return Response.DeleteResponse()
        }
    }

    @Polymorphic
    @Serializable
    sealed class Response {
        @Serializable
        @SerialName("ListFiles")
        class ListFilesResponse(val files: List<Fs.File>) : Response()

        @Serializable
        @SerialName("LoadFile")
        class LoadFileResponse(val contents: String?) : Response()

        @Serializable
        @SerialName("SaveFile")
        class SaveFileResponse() : Response()

        @Serializable
        @SerialName("Exists")
        class ExistsResponse(val exists: Boolean) : Response()

        @Serializable
        @SerialName("IsDirectory")
        class IsDirectoryResponse(val exists: Boolean) : Response()

        @Serializable
        @SerialName("RenameFile")
        class RenameFileResponse() : Response()

        @Serializable
        @SerialName("Delete")
        class DeleteResponse() : Response()
    }
}

class PubSubRemoteFsServerBackend(
    pubSub: PubSub.Server,
    serializer: RemoteFsSerializer
) {
    init {
        val commandPort = serializer.createCommandPort()
        pubSub.listenOnCommandChannel(commandPort) { command -> command.perform() }
    }
}
