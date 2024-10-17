package baaahs.sim

import baaahs.io.Fs

class MergedFs(
    private val baseFs: Fs,
    private vararg val overlayFses: Fs,
    override val name: String = "${baseFs.name} + ${overlayFses.joinToString(" + ") { it.name }}"
) : Fs {
    override suspend fun listFiles(directory: Fs.File): List<Fs.File> {
        return (
                baseFs.listFiles(directory) +
                        overlayFses.flatMap { it.listFiles(directory) }
                )
            .map { it.fullPath to it.isDirectory }
            .distinct()
            .map { (fullPath, isDirectory) -> Fs.File(this, fullPath, isDirectory) }
    }

    override suspend fun loadFile(file: Fs.File): String? {
        return overlayFses.firstNonNull { if (it.exists(file)) it.loadFile(file) else null }
            ?: baseFs.loadFile(file)
    }

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        baseFs.saveFile(file, content, allowOverwrite)
    }

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        baseFs.saveFile(file, content, allowOverwrite)
    }

    override suspend fun exists(file: Fs.File): Boolean {
        return baseFs.exists(file) || overlayFses.any { it.exists(file) }
    }

    override suspend fun isDirectory(file: Fs.File): Boolean {
        return overlayFses.firstOrNull { it.exists(file) }?.isDirectory(file)
            ?: baseFs.isDirectory(file)
    }

    override suspend fun renameFile(fromFile: Fs.File, toFile: Fs.File) {
        overlayFses.forEach {
            if (it.exists(fromFile)) it.renameFile(fromFile, toFile)
        }

        if (baseFs.exists(fromFile)) baseFs.renameFile(fromFile, toFile)
    }

    override suspend fun delete(file: Fs.File) {
        overlayFses.forEach {
            if (it.exists(file)) it.delete(file)
        }

        if (baseFs.exists(file)) baseFs.delete(file)
    }
}

private inline fun <T, R> Array<T>.firstNonNull(fn: (T) -> R): R? {
    for (item in this) {
        fn(item)?.let { return it }
    }
    return null
}
