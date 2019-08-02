package baaahs

import baaahs.io.Fs
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.*

actual fun doRunBlocking(block: suspend () -> Unit) = runBlocking { block() }

actual fun getResource(name: String): String {
    return Pinky::class.java.classLoader.getResource(name).readText()
}

actual fun getTimeMillis(): Long = System.currentTimeMillis()

actual fun decodeBase64(s: String): ByteArray = Base64.getDecoder().decode(s)

@UseExperimental(ExperimentalStdlibApi::class)
class RealFs(val basePath: Path) : Fs {
    override fun loadFile(name: String): String? {
        val safeName = name.replace(Regex("^(\\.?\\.?/)*"), "")
        val path = basePath.resolve(safeName)
        try {
            return Files.readAllBytes(path).decodeToString()
        } catch (e: java.nio.file.NoSuchFileException) {
            return null
        }
    }

    override fun createFile(name: String, content: ByteArray, allowOverwrite: Boolean) {
        val safeName = name.replace(Regex("^(\\.?\\.?/)*"), "")
        val destPath = basePath.resolve(safeName)
        Files.createDirectories(destPath.parent)
        Files.write(destPath, content, if (allowOverwrite) StandardOpenOption.CREATE else StandardOpenOption.CREATE_NEW)
    }

    override fun createFile(name: String, content: String, allowOverwrite: Boolean) {
        createFile(name, content.encodeToByteArray(), allowOverwrite)
    }
}