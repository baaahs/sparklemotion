import org.gradle.internal.os.OperatingSystem
import java.io.File
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

fun createResourceFilesList(baseDir: File) {
    val basePath = baseDir.toPath()
    val list = mutableListOf<String>()
    Files.walkFileTree(basePath, object : SimpleFileVisitor<Path>() {
        override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
            val path = basePath.relativize(file)
            if (path.toString() != "_RESOURCE_FILES_") {
                list.add(path.toString())
            }
            return FileVisitResult.CONTINUE
        }
    })

    list.sort()
    val outFile = File(baseDir, "_RESOURCE_FILES_")
    Files.write(outFile.toPath(), list, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
}

fun isMac(): Boolean {
    return OperatingSystem.current() == OperatingSystem.MAC_OS
}