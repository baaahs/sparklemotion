package baaahs.io

import baaahs.PubSub
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
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

    private var nextRequestId = 0
    private val responseChannels = hashMapOf<Int, Channel<RemoteFsOp.Response>>()
    private val commandPort = createCommandPort()

    private val pubSubChannel =
        pubSub.openCommandChannel(commandPort) { response ->
            val responseChannel = responseChannels.remove(response.requestId)!!
            GlobalScope.launch {
                responseChannel.send(response)
            }
        }

    private suspend fun <R : RemoteFsOp.Response> sendCommand(command: RemoteFsOp): R {
        val responseChannel = Channel<RemoteFsOp.Response>(1)
        responseChannels[command.requestId] = responseChannel
        pubSubChannel.send(command)
        @Suppress("UNCHECKED_CAST")
        return responseChannel.receive() as R
    }

    override suspend fun listFiles(directory: Fs.File): List<Fs.File> {
        val reply: RemoteFsOp.Response.ListFilesResponse = sendCommand(RemoteFsOp.ListFiles(nextRequestId++, directory))
        return reply.files
    }

    override suspend fun loadFile(file: Fs.File): String? {
        val reply: RemoteFsOp.Response.LoadFileResponse = sendCommand(RemoteFsOp.LoadFile(nextRequestId++, file))
        return reply.contents
    }

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        return saveFile(file, content.decodeToString())
    }

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        sendCommand<RemoteFsOp.Response>(RemoteFsOp.SaveFile(nextRequestId++, file, content, allowOverwrite))
    }

    override suspend fun exists(file: Fs.File): Boolean {
        val reply: RemoteFsOp.Response.ExistsResponse = sendCommand(RemoteFsOp.Exists(nextRequestId++, file))
        return reply.exists
    }

    override suspend fun isDirectory(file: Fs.File): Boolean {
        val reply: RemoteFsOp.Response.IsDirectoryResponse = sendCommand(RemoteFsOp.IsDirectory(nextRequestId++, file))
        return reply.exists
    }
}

@Polymorphic
@Serializable
sealed class RemoteFsOp {
    abstract val requestId: Int

    abstract suspend fun perform(): Response

    @Serializable
    @SerialName("ListFiles")
    data class ListFiles(override val requestId: Int, val directory: Fs.File) : RemoteFsOp() {
        override suspend fun perform(): Response =
            Response.ListFilesResponse(requestId, directory.listFiles())
    }

    @Serializable
    @SerialName("LoadFile")
    class LoadFile(override val requestId: Int, val file: Fs.File) : RemoteFsOp() {
        override suspend fun perform(): Response =
            Response.LoadFileResponse(requestId, file.read())
    }

    @Serializable
    @SerialName("SaveFile")
    class SaveFile(override val requestId: Int, val file: Fs.File, val content: String, val allowOverwrite: Boolean) : RemoteFsOp() {
        override suspend fun perform(): Response {
            file.write(content, allowOverwrite)
            return Response.SaveFileResponse(requestId)
        }
    }

    @Serializable
    @SerialName("Exists")
    class Exists(override val requestId: Int, val file: Fs.File) : RemoteFsOp() {
        override suspend fun perform(): Response {
            return Response.ExistsResponse(requestId, file.exists())
        }
    }

    @Serializable
    @SerialName("IsDirectory")
    class IsDirectory(override val requestId: Int, val file: Fs.File) : RemoteFsOp() {
        override suspend fun perform(): Response {
            return Response.IsDirectoryResponse(requestId, file.isDir())
        }
    }

    @Polymorphic
    @Serializable
    sealed class Response {
        abstract val requestId: Int

        @Serializable
        @SerialName("ListFiles")
        class ListFilesResponse(override val requestId: Int, val files: List<Fs.File>) : RemoteFsOp.Response()

        @Serializable
        @SerialName("LoadFile")
        class LoadFileResponse(override val requestId: Int, val contents: String?) : RemoteFsOp.Response()

        @Serializable
        @SerialName("SaveFile")
        class SaveFileResponse(override val requestId: Int) : RemoteFsOp.Response()

        @Serializable
        @SerialName("Exists")
        class ExistsResponse(override val requestId: Int, val exists: Boolean) : RemoteFsOp.Response()

        @Serializable
        @SerialName("IsDirectory")
        class IsDirectoryResponse(override val requestId: Int, val exists: Boolean) : RemoteFsOp.Response()
    }
}

class PubSubRemoteFsServerBackend(
    pubSub: PubSub.Server,
    serializer: RemoteFsSerializer
) {
    init {
        val commandPort = serializer.createCommandPort()
        pubSub.listenOnCommandChannel(commandPort) { command, reply ->
            reply(command.perform())
        }
    }
}
