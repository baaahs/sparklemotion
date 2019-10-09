package baaahs

import baaahs.io.Fs
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.streams.toList

actual fun doRunBlocking(block: suspend () -> Unit) = runBlocking { block() }

actual fun getResource(name: String): String {
    return Pinky::class.java.classLoader.getResource(name).readText()
}

actual fun getTimeMillis(): Long = System.currentTimeMillis()

actual fun decodeBase64(s: String): ByteArray = Base64.getDecoder().decode(s)

class RealFs(private val basePath: Path) : Fs {

    override fun listFiles(path: String): List<String> {
        val dir = resolve(path)
        if (Files.isDirectory(dir)) {
            return Files.list(dir).map { it.fileName.toString() }.toList()
        } else {
            return emptyList()
        }
    }

    override fun loadFile(path: String): String? {
        val destPath = resolve(path)
        try {
            return Files.readAllBytes(destPath).decodeToString()
        } catch (e: java.nio.file.NoSuchFileException) {
            return null
        }
    }

    override fun createFile(path: String, content: ByteArray, allowOverwrite: Boolean) {
        val destPath = resolve(path)
        Files.createDirectories(destPath.parent)
        Files.write(destPath, content, if (allowOverwrite) StandardOpenOption.CREATE else StandardOpenOption.CREATE_NEW)
    }

    override fun createFile(path: String, content: String, allowOverwrite: Boolean) {
        createFile(path, content.encodeToByteArray(), allowOverwrite)
    }

    private fun resolve(path: String): Path {
        val safeName = path.replace(Regex("^(\\.?\\.?/)*"), "")
        return basePath.resolve(safeName)
    }
}

actual fun logMessage(level: String, message: String, exception: Exception?) {
    when (level) {
        "ERROR" -> println("$level: $message")
        "WARN" -> println("$level: $message")
        "INFO" -> println("$level: $message")
        "DEBUG" -> println("$level: $message")
        else -> println("$level: $message")
    }
    exception?.printStackTrace()
}