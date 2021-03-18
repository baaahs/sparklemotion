package baaahs.io

import baaahs.getResource
import baaahs.sim.BaseFakeFs

class ResourcesFs : BaseFakeFs() {
    override val name: String
        get() = "Resources"

    override val keys: List<String>
            by lazy {
                getResource("_RESOURCE_FILES_").split("\n")
                    .filterNot { it.isBlank() }
                    .also { println("resource files: $it") }
            }

    override suspend fun loadFile(file: Fs.File): String? {
        return getResource(file.fullPath)
    }

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        throw UnsupportedOperationException("Resources filesystem is read-only.")
    }

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        throw UnsupportedOperationException("Resources filesystem is read-only.")
    }

    override suspend fun delete(file: Fs.File) {
        throw UnsupportedOperationException("Resources filesystem is read-only.")
    }
}