package baaahs.sim

import baaahs.io.Fs

class MergedFs(
    val baseFs: Fs,
    val overlayFs: Fs,
    override val name: String = "${baseFs.name} + ${overlayFs.name}"
) : Fs {
    override suspend fun listFiles(directory: Fs.File): List<Fs.File> {
        return (baseFs.listFiles(directory) + overlayFs.listFiles(directory))
            .distinct()
            .map { Fs.File(this, it.fullPath, it.isDirectory) }
    }

    override suspend fun loadFile(file: Fs.File): String? {
        return overlayFs.loadFile(file) ?: baseFs.loadFile(file)
    }

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        baseFs.saveFile(file, content, allowOverwrite)
    }

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        baseFs.saveFile(file, content, allowOverwrite)
    }

    override suspend fun exists(file: Fs.File): Boolean {
        return baseFs.exists(file) || overlayFs.exists(file)
    }
}