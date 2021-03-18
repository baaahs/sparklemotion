package baaahs.sim

import baaahs.io.Fs
import baaahs.window

class BrowserSandboxFs(override val name: String) : BaseFakeFs() {
    private val storage = window.localStorage

    override val keys: List<String>
        get() = getFileList()

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
            updateFileList((keys + file.fullPath))
        }
        storage.setItem(keyName(file), content)
    }

    override suspend fun delete(file: Fs.File) {
        storage.removeItem(keyName(file))
        updateFileList(getFileList() - file.fullPath)
    }

    private fun keyName(file: Fs.File): String = "sm.fs:${file.fullPath}"

    private fun getFileList() = (storage.getItem("sm.fs:keys") ?: "")
        .split("\n")
        .filter { it.isNotEmpty() }

    private fun updateFileList(keys: List<String>) {
        storage.setItem("sm.fs:keys", keys.joinToString("\n"))
    }
}