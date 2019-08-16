package baaahs.sim

import baaahs.Logger
import baaahs.io.Fs

@UseExperimental(ExperimentalStdlibApi::class)
class FakeFs : Fs {
    private val files = mutableMapOf<String, ByteArray>()

    override fun listFiles(path: String): List<String> {
        logger.debug { "FakeFs.listFiles($path)" }
        return files.keys.filter { it.startsWith("$path/") }
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

    companion object {
        val logger = Logger("FakeFs")
    }
}