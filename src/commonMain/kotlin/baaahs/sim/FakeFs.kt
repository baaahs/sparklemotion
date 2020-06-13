package baaahs.sim

import baaahs.Logger
import baaahs.io.Fs

class FakeFs : BaseFakeFs() {
    private val files = mutableMapOf<String, ByteArray>()

    override val keys: List<String>
        get() = files.keys.toList()

    override fun loadFile(file: Fs.File): String? {
        logger.debug { "FakeFs.loadFile($file)" }
        return files[file.fullPath]?.decodeToString()
    }

    override fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        logger.debug { "FakeFs.createFile($file) -> ${content.size} bytes" }
        addFile(file, content)
    }

    override fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        saveFile(file, content.encodeToByteArray(), allowOverwrite)
    }

    private fun addFile(file: Fs.File, content: ByteArray) {
        val path = file.fullPath
        if (files.containsKey(path)) {
            throw Exception("$path already exists")
        }
        files[path] = content
    }

    fun renameFile(from: Fs.File, to: Fs.File) {
        files[to.fullPath] = files.remove(from.fullPath)!!
    }
}

abstract class BaseFakeFs : Fs {
    protected abstract val keys: List<String>

    override fun listFiles(parent: Fs.File): List<Fs.File> {
        val prefix = if (parent.isRoot) "" else "${parent.fullPath}/"
        val entries = keys
            .filter { parent.isRoot || it.startsWith(prefix) }
            .map {
                val inPath = it.substring(prefix.length)
                val slash = inPath.indexOf('/')
                if (slash == -1) {
                    parent.resolve(inPath, false)
                } else {
                    parent.resolve(inPath.substring(0, slash), true)
                }
            }.distinct()
        logger.debug { "FakeFs.listFiles($parent) -> $entries" }
        return entries
    }

    override fun exists(file: Fs.File): Boolean {
        val allKeys = keys
        return allKeys.contains(file.fullPath) ||
                allKeys.any { resolve(it).isWithin(file) }
    }

    companion object {
        val logger = Logger("FakeFs")
    }
}