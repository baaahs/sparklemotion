package baaahs.sim

import baaahs.io.Fs
import web.storage.localStorage

class BrowserSandboxFs(
    override val name: String,
    baseDir: String? = null
) : BaseFakeFs() {
    private val storage = localStorage
    private val basePath = if (baseDir != null) {
        baseDir.trim('/') + "/"
    } else ""

    override val keys: List<String>
        get() = getFileList()

    override suspend fun listFiles(directory: Fs.File): List<Fs.File> {
        val prefix = if (directory.isRoot) basePath else "$basePath${directory.fullPath}/"
        return listFiles(directory, prefix)
    }

    override suspend fun loadFile(file: Fs.File): String? {
        return storage.getItem(keyName(file))
    }

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        val byteStr = buildString {
            content.forEach { append((it.toInt() and 0xff).toChar()) }
        }
        saveFile(file, byteStr, allowOverwrite)
    }

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        if (keys.contains(file.fullPath)) {
            if (!allowOverwrite) {
                throw Exception("$file already exists")
            }
        } else {
            updateFileList((keys + "${basePath}${file.fullPath}"))
        }
        storage.setItem(keyName(file), content)
    }

    override suspend fun renameFile(fromFile: Fs.File, toFile: Fs.File) {
        val fromKey = keyName(fromFile)
        val content = storage.getItem(fromKey)
        if (content != null) {
            storage.removeItem(fromKey)
            storage.setItem(keyName(toFile), content)
            updateFileList(getFileList() - fromFile.fullPath + toFile.fullPath)
        }
    }

    override suspend fun delete(file: Fs.File) {
        storage.removeItem(keyName(file))
        updateFileList(getFileList() - file.fullPath)
    }

    private fun keyName(file: Fs.File): String = "$filePrefix$basePath${file.fullPath}"

    private fun getFileList() =
        storage.getItem(keysKey)
            ?.split("\n")
            ?.filter { it.isNotEmpty() }
            ?: rebuildFileList()

    private fun rebuildFileList(): List<String> =
        (0 until storage.length).mapNotNull { i ->
            storage.key(i)?.let {
                if (it != keysKey && it.startsWith(filePrefix)) {
                    it.substring(filePrefix.length)
                } else null
            }
        }

    private fun updateFileList(keys: List<String> = rebuildFileList()) {
        storage.setItem(keysKey, keys.joinToString("\n"))
    }

    @JsName("rm")
    fun rm(pattern: String, doIt: Boolean = false) {
        val re = Regex(pattern)

        val toDelete = (0 until storage.length).mapNotNull { i ->
            storage.key(i)?.let {
                if (it != keysKey && it.startsWith(filePrefix) &&
                    re.matches(it.substring(filePrefix.length))
                ) it else null
            }
        }

        toDelete.forEach {
            console.log("Local storage: rm ", it)
            if (doIt) storage.removeItem(it)
        }

        if (!doIt) console.log("... to actually do it, add \"true\" as the final arg.")
        updateFileList()
    }

    private companion object {
        const val filePrefix = "sm.fs:"
        const val keysKey = "sm.fs:keys"
    }
}