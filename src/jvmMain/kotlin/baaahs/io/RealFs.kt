package baaahs.io

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.streams.toList

class RealFs(private val basePath: Path) : Fs {

    override fun listFiles(parent: Fs.File): List<Fs.File> {
        val dir = resolve(parent)
        if (Files.isDirectory(dir)) {
            return Files.list(dir).map { parent.resolve(it.fileName.toString()) }.toList()
        } else {
            return emptyList()
        }
    }

    override fun loadFile(file: Fs.File): String? {
        val destPath = resolve(file)
        try {
            return Files.readAllBytes(destPath).decodeToString()
        } catch (e: java.nio.file.NoSuchFileException) {
            return null
        }
    }

    override fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        val destPath = resolve(file)
        Files.createDirectories(destPath.parent)
        Files.write(
            destPath,
            content,
            if (allowOverwrite) StandardOpenOption.CREATE else StandardOpenOption.CREATE_NEW
        )
    }

    override fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        saveFile(file, content.encodeToByteArray(), allowOverwrite)
    }

    override fun exists(file: Fs.File): Boolean {
        return Files.exists(resolve(file))
    }

    override fun isDirectory(file: Fs.File): Boolean {
        return Files.isDirectory(resolve(file))
    }

    private fun resolve(file: Fs.File): Path {
        return basePath.resolve(file.fullPath)
    }
}