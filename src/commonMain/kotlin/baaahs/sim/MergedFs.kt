package baaahs.sim

import baaahs.io.Fs

class MergedFs(val baseFs: Fs, val overlayFs: Fs) : Fs {
    override fun listFiles(path: String): List<String> {
        return (baseFs.listFiles(path) + overlayFs.listFiles(path)).distinct()
    }

    override fun loadFile(path: String): String? {
        return overlayFs.loadFile(path) ?: baseFs.loadFile(path)
    }

    override fun createFile(path: String, content: ByteArray, allowOverwrite: Boolean) {
        baseFs.createFile(path, content, allowOverwrite)
    }

    override fun createFile(path: String, content: String, allowOverwrite: Boolean) {
        baseFs.createFile(path, content, allowOverwrite)
    }
}