package baaahs.io

import baaahs.sim.BaseFakeFs
import baaahs.util.Logger

class ResourcesFs(
    private val prefix: String = ""
) : BaseFakeFs() {
    override val name: String
        get() = "Resources"

    override val keys: List<String>
            by lazy {
                try {
                    getResource("_RESOURCE_FILES_").split("\n")
                        .filterNot { it.isBlank() }
                } catch (e: NoSuchFileException) {
                    logger.warn(e) { "Couldn't find `_RESOURCE_FILES_` index file." }
                    emptyList()
                }
            }

    override suspend fun loadFile(file: Fs.File): String {
        return getResourceAsync(prefix + file.fullPath)
    }

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        throw UnsupportedOperationException("Resources filesystem is read-only.")
    }

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        throw UnsupportedOperationException("Resources filesystem is read-only.")
    }

    override suspend fun renameFile(fromFile: Fs.File, toFile: Fs.File) {
        throw UnsupportedOperationException("Resources filesystem is read-only.")
    }

    override suspend fun delete(file: Fs.File) {
        throw UnsupportedOperationException("Resources filesystem is read-only.")
    }

    companion object {
        private val logger = Logger<ResourcesFs>()
    }
}

expect fun getResource(name: String): String
expect suspend fun getResourceAsync(name: String): String

val resourcesFs = ResourcesFs()