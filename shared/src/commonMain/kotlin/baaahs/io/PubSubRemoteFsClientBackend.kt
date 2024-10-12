package baaahs.io

import baaahs.PubSub

class PubSubRemoteFsClientBackend(
    pubSub: PubSub.Client
) : FsClientSideSerializer(), RemoteFsBackend {
    override val name: String
        get() = "Backend for RemoteFs"

    override val backend: RemoteFsBackend
        get() = this

    private val remoteFs = createRpcImpl().createSender(pubSub)

    override suspend fun listFiles(directory: Fs.File): List<Fs.File> =
        remoteFs.listFiles(directory)

    override suspend fun loadFile(file: Fs.File): String? =
        remoteFs.loadFile(file)

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) =
        remoteFs.saveFile(file, content, allowOverwrite)

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) =
        remoteFs.saveFile(file, content, allowOverwrite)

    override suspend fun exists(file: Fs.File): Boolean =
        remoteFs.exists(file)

    override suspend fun isDirectory(file: Fs.File): Boolean =
        remoteFs.isDirectory(file)

    override suspend fun renameFile(fromFile: Fs.File, toFile: Fs.File) =
        remoteFs.renameFile(fromFile, toFile)

    override suspend fun delete(file: Fs.File) =
        remoteFs.delete(file)
}

class PubSubRemoteFsServerBackend(
    pubSub: PubSub.Server,
    serializer: RemoteFsSerializer
) {
    init {
        serializer.createRpcImpl()
            .createReceiver(pubSub, object : RemoteFsCommands {
                override suspend fun listFiles(directory: Fs.File): List<Fs.File> =
                    directory.listFiles()

                override suspend fun loadFile(file: Fs.File): String? =
                    file.read()

                override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean): Unit =
                    file.write(content.decodeToString(), allowOverwrite)

                override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean): Unit =
                    file.write(content, allowOverwrite)

                override suspend fun exists(file: Fs.File): Boolean =
                    file.exists()

                override suspend fun isDirectory(file: Fs.File): Boolean =
                    file.isDir()

                override suspend fun renameFile(fromFile: Fs.File, toFile: Fs.File): Unit =
                    fromFile.renameTo(toFile)

                override suspend fun delete(file: Fs.File): Unit =
                    file.delete()
            })
    }
}
