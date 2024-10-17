package baaahs.sim

import baaahs.io.Fs
import baaahs.util.Logger

class FakeFs(override val name: String = "FakeFs") : BaseFakeFs() {
    private val files = mutableMapOf<String, ByteArray>()

    override val keys: List<String>
        get() = files.keys.toList()

    override suspend fun loadFile(file: Fs.File): String? {
        logger.debug { "FakeFs.loadFile($file)" }
        return files[file.fullPath]?.decodeToString()
    }

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        logger.debug { "FakeFs.createFile($file) -> ${content.size} bytes" }
        addFile(file, content, allowOverwrite)
    }

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        saveFile(file, content.encodeToByteArray(), allowOverwrite)
    }

    override suspend fun renameFile(fromFile: Fs.File, toFile: Fs.File) {
        files[toFile.fullPath] = files.remove(fromFile.fullPath)!!
    }

    override suspend fun delete(file: Fs.File) {
        files.remove(file.fullPath)
    }

    private fun addFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        val path = file.fullPath
        if (files.containsKey(path) && !allowOverwrite) {
            throw Exception("$path already exists")
        }
        files[path] = content
    }
}

abstract class BaseFakeFs : Fs {
    protected abstract val keys: List<String>

    override suspend fun listFiles(directory: Fs.File): List<Fs.File> {
        val prefix = if (directory.isRoot) "" else "${directory.fullPath}/"
        return listFiles(directory, prefix)
    }

    protected fun listFiles(directory: Fs.File, prefix: String): List<Fs.File> {
        val entries = keys
            .filter { it.startsWith(prefix) }
            .map {
                val inPath = it.substring(prefix.length)
                val slash = inPath.indexOf('/')
                if (slash == -1) {
                    directory.resolve(inPath, isDirectory = false)
                } else {
                    directory.resolve(inPath.substring(0, slash), isDirectory = true)
                }
            }.distinct()
        logger.debug { "FakeFs.listFiles($directory) -> $entries" }
        return entries
    }

    override suspend fun exists(file: Fs.File): Boolean {
        val allKeys = keys
        return allKeys.contains(file.fullPath) ||
                allKeys.any { resolve(it).isWithin(file) }
    }

    override suspend fun isDirectory(file: Fs.File): Boolean {
        return file.isDirectory ?: keys.any { it.startsWith(file.fullPath + "/") }
    }

    companion object {
        val logger = Logger("FakeFs")
    }
}