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
    override fun createFile(name: String, content: ByteArray) {
        val safeName = name.replace(Regex("^(\\.?\\.?/)*"), "")
        val destPath = basePath.resolve(safeName)
        Files.createDirectories(destPath.parent)
        Files.write(destPath, content, StandardOpenOption.CREATE_NEW)
    }

    override fun createFile(name: String, content: String) {
        createFile(name, content.encodeToByteArray())
    }
}