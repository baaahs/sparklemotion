package baaahs.sim

import baaahs.io.Fs
import kotlin.browser.window

class BrowserSandboxFs : Fs {
    private val storage = window.localStorage

    private fun keys(): List<String> {
        return (storage.getItem("sm.fs:keys") ?: "").split("\n").filter { it.isNotEmpty() }
    }

    override fun listFiles(path: String): List<String> {
        val prefix = if (path.isEmpty()) "" else "$path/"
        val entries = keys().filter { it.startsWith(prefix) }.map {
            val inPath = it.substring(path.length).trimStart('/')
            val slash = inPath.indexOf('/')
            val entry = if (slash == -1) inPath else inPath.substring(0, slash)
            entry
        }.distinct()
        FakeFs.logger.debug { "FakeFs.listFiles($path) -> $entries" }
        return entries
    }

    private fun keyName(path: String): String = "sm.fs:$path"

    override fun loadFile(path: String): String? {
        return storage.getItem(keyName(path))
    }

    override fun createFile(path: String, content: ByteArray, allowOverwrite: Boolean) {
        createFile(path, content.toString(), allowOverwrite)
    }

    override fun createFile(path: String, content: String, allowOverwrite: Boolean) {
        val keys = keys()
        if (keys.contains(path)) {
            if (!allowOverwrite) {
                throw Exception("$path already exists")
            }
        } else {
            storage.setItem("sm.fs:keys", (keys + path).joinToString("\n"))
        }
        storage.setItem(keyName(path), content)
    }
}