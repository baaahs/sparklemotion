package baaahs.io

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.streams.toList

class RealFs(
    override val name: String,
    private val basePath: Path
) : Fs {
    override suspend fun listFiles(directory: Fs.File): List<Fs.File> {
        val dir = resolve(directory)
        return if (Files.isDirectory(dir)) {
            Files.list(dir).map { directory.resolve(it.fileName.toString()) }.toList()
        } else {
            emptyList()
        }
    }

    override suspend fun loadFile(file: Fs.File): String? {
        val destPath = resolve(file)
        try {
            return Files.readAllBytes(destPath).decodeToString()
        } catch (e: java.nio.file.NoSuchFileException) {
            return null
        }
    }

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        val destPath = resolve(file)
        if (!Files.exists(destPath.parent)) {
            Files.createDirectories(destPath.parent)
        }
        Files.write(
            destPath,
            content,
            if (allowOverwrite) StandardOpenOption.CREATE else StandardOpenOption.CREATE_NEW
        )
    }

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        saveFile(file, content.encodeToByteArray(), allowOverwrite)
    }

    override suspend fun exists(file: Fs.File): Boolean {
        return Files.exists(resolve(file))
    }

    override suspend fun isDirectory(file: Fs.File): Boolean {
        return Files.isDirectory(resolve(file))
    }

    private fun resolve(file: Fs.File): Path {
        return basePath.resolve(file.fullPath)
    }
}