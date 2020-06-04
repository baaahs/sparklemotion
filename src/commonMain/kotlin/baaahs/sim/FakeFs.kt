package baaahs.sim

import baaahs.Logger
import baaahs.io.Fs

class FakeFs : Fs {
    private val files = mutableMapOf<String, ByteArray>()

    override fun listFiles(path: String): List<Fs.File> {
        val prefix = if (path.isEmpty()) "" else "$path/"
        val entries = files.keys.filter { it.startsWith(prefix) }.map {
            val inPath = it.substring(path.length).trimStart('/')
            val slash = inPath.indexOf('/')
            val entry = if (slash == -1) inPath else inPath.substring(0, slash)
            entry
        }.distinct()
        logger.debug { "FakeFs.listFiles($path) -> $entries" }
        return entries
    }

    override fun loadFile(path: String): String? {
        logger.debug { "FakeFs.loadFile($path)" }
        return files[path]?.decodeToString()
    }

    override fun createFile(path: String, content: ByteArray, allowOverwrite: Boolean) {
        logger.debug { "FakeFs.createFile($path) -> ${content.size} bytes" }
        addFile(path, content)
    }

    override fun createFile(path: String, content: String, allowOverwrite: Boolean) {
        createFile(path, content.encodeToByteArray(), allowOverwrite)
    }

    private fun addFile(path: String, content: ByteArray) {
        if (files.containsKey(path)) {
            throw Exception("$path already exists")
        }
        files[path] = content
    }

    fun renameFile(from: String, to: String) {
        files[to] = files.remove(from)!!
    }

    companion object {
        val logger = Logger("FakeFs")
    }
}