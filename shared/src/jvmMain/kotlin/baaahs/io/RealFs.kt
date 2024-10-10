package baaahs.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.streams.toList

class RealFs(
    override val name: String,
    private val basePath: Path
) : Fs {
    override suspend fun listFiles(directory: Fs.File): List<Fs.File> =
        withContext(Dispatchers.IO) {
            val dir = resolve(directory)
            if (Files.isDirectory(dir)) {
                Files.list(dir).map { directory.resolve(it.fileName.toString()) }.toList()
            } else {
                emptyList()
            }
        }

    override suspend fun loadFile(file: Fs.File): String? =
        withContext(Dispatchers.IO) {
            val destPath = resolve(file)
            try {
                Files.readAllBytes(destPath).decodeToString()
            } catch (e: java.nio.file.NoSuchFileException) {
                null
            }
        }

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean): Unit =
        withContext(Dispatchers.IO)  {
            val destPath = resolve(file)
            if (!Files.exists(destPath.parent)) {
                Files.createDirectories(destPath.parent)
            }
            Files.write(
                destPath,
                content,
                if (allowOverwrite) StandardOpenOption.CREATE else StandardOpenOption.CREATE_NEW,
                StandardOpenOption.TRUNCATE_EXISTING
            )
        }

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        saveFile(file, content.encodeToByteArray(), allowOverwrite)
    }

    override suspend fun exists(file: Fs.File): Boolean =
        withContext(Dispatchers.IO) {
            Files.exists(resolve(file))
        }

    override suspend fun isDirectory(file: Fs.File): Boolean =
        withContext(Dispatchers.IO) {
            Files.isDirectory(resolve(file))
        }

    override suspend fun renameFile(fromFile: Fs.File, toFile: Fs.File) =
        withContext(Dispatchers.IO) {
            Files.move(resolve(fromFile), resolve(toFile))
            Unit
        }

    override suspend fun delete(file: Fs.File) =
        withContext(Dispatchers.IO) {
            Files.delete(resolve(file))
        }

    fun resolve(file: Fs.File): Path {
        return basePath.resolve(file.fullPath)
    }
}