package baaahs.sim

import baaahs.io.Fs
import kotlin.browser.window

class BrowserSandboxFs : BaseFakeFs() {
    private val storage = window.localStorage

    override val keys: List<String>
        get() = (storage.getItem("sm.fs:keys") ?: "").split("\n").filter { it.isNotEmpty() }

    private fun keyName(file: Fs.File): String = "sm.fs:${file.fullPath}"

    override suspend fun loadFile(file: Fs.File): String? {
        return storage.getItem(keyName(file))
    }

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        saveFile(file, content.toString(), allowOverwrite)
    }

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        if (keys.contains(file.fullPath)) {
            if (!allowOverwrite) {
                throw Exception("$file already exists")
            }
        } else {
            storage.setItem("sm.fs:keys", (keys + file.fullPath).joinToString("\n"))
        }
        storage.setItem(keyName(file), content)
    }
}