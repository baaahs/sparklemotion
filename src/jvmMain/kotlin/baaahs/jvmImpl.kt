package baaahs

import baaahs.io.Fs
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
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

    override fun listFiles(parent: Fs.File): List<String> {
        val dir = resolve(parent)
        if (Files.isDirectory(dir)) {
            return Files.list(dir).map { it.fileName.toString() }.toList()
        } else {
            return emptyList()
        }
    }

    override fun loadFile(file: Fs.File): String? {
        val destPath = resolve(file)
        try {
            return Files.readAllBytes(destPath).decodeToString()
        } catch (e: java.nio.file.NoSuchFileException) {
            return null
        }
    }

    override fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        val destPath = resolve(file)
        Files.createDirectories(destPath.parent)
        Files.write(destPath, content, if (allowOverwrite) StandardOpenOption.CREATE else StandardOpenOption.CREATE_NEW)
    }

    override fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        saveFile(file, content.encodeToByteArray(), allowOverwrite)
    }

    private fun resolve(path: String): Path {
        val safeName = path.replace(Regex("^(\\.?\\.?/)*"), "")
        return basePath.resolve(safeName)
    }
}

actual fun log(id: String, level: String, message: String, exception: Throwable?) {
    val logger = LoggerFactory.getLogger(id)
    when (level) {
        "ERROR" -> logger.error(message, exception)
        "WARN" -> logger.warn(message, exception)
        "INFO" -> logger.info(message, exception)
        "DEBUG" -> logger.debug(message, exception)
        else -> logger.info(message, exception)
    }
}