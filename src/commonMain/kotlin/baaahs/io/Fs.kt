@file:UseContextualSerialization(Fs::class)

package baaahs.io

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization

@Polymorphic
interface Fs {
    val name: String
    val rootFile: File get() = File(this, "", true)

    suspend fun listFiles(directory: File): List<File>
    suspend fun loadFile(file: File): String?
    suspend fun saveFile(file: File, content: ByteArray, allowOverwrite: Boolean = false)
    suspend fun saveFile(file: File, content: String, allowOverwrite: Boolean = false)

    fun resolve(vararg pathParts: String): File {
        return File(this, pathParts.joinToString("/"))
    }

    suspend fun exists(file: File): Boolean

    suspend fun isDirectory(file: File): Boolean {
        return file.isDirectory ?: error("Is $file a directory? Answer unclear.")
    }

    suspend fun renameFile(fromFile: File, toFile: File)

    suspend fun delete(file: File)

    @Serializable
    class File(
        val fs: Fs,
        val pathParts: List<String>,
        /** If this is known to be a directory or not then `true` or `false`, otherwise `null` */
        internal val isDirectory: Boolean? = null
    ) : Comparable<File> {
        constructor(fs: Fs, fullPath: String, isDirectory: Boolean? = null) : this(
            fs,
            fullPath.split("/").filter { it.isNotEmpty() && it != "." && it != ".." },
            isDirectory
        )

        val fullPath: String get() = pathParts.joinToString("/")
        val parentPath: String get() = pathParts.subList(0, pathParts.size - 1).joinToString("/")
        val name: String get() = pathParts.lastOrNull() ?: ""
        val isRoot: Boolean get() = pathParts.isEmpty()
        val parent: File? get() = if (isRoot) null else File(fs, parentPath, true)


        suspend fun listFiles(): List<File> = fs.listFiles(this)
        suspend fun read(): String? = fs.loadFile(this)
        suspend fun write(content: ByteArray, allowOverwrite: Boolean = false) = fs.saveFile(this, content, allowOverwrite)
        suspend fun write(content: String, allowOverwrite: Boolean = false) = fs.saveFile(this, content, allowOverwrite)
        suspend fun exists(): Boolean = fs.exists(this)
        suspend fun isDir(): Boolean = fs.isDirectory(this)
        suspend fun renameTo(toFile: File) = fs.renameFile(this, toFile)
        suspend fun delete(): Unit = fs.delete(this)

        fun isWithin(parent: File): Boolean {
            return if (parent.isRoot) {
                true
            } else {
                "${parentPath}/".startsWith("${parent.fullPath}/")
            }
        }

        fun resolve(vararg relPath: String, isDirectory: Boolean? = null): File =
            if (isRoot) {
                File(fs, relPath.joinToString("/"), isDirectory)
            } else {
                File(fs, "${fullPath}/${relPath.joinToString("/")}", isDirectory)
            }

        fun relativeTo(relPath: File): String =
            if (!relPath.isRoot && isWithin(relPath)) {
                fullPath.substring(relPath.fullPath.length + 1)
            } else fullPath

//  TODO  fun resolve(relPath: String, isDirectory: Boolean? = null): RemoteFile {
//            val resolvedPathParts = ArrayList(pathParts)
//            var looksLikeDirectory = true
//            relPath.split("/").forEach {
//                when (it) {
//                    "." -> {
//                        looksLikeDirectory = true
//                    }
//                    ".." -> {
//                        if (resolvedPathParts.size > 0) resolvedPathParts.removeLast()
//                        looksLikeDirectory = true
//                    }
//                    else -> {
//                        resolvedPathParts.add(it)
//                        looksLikeDirectory = false // TODO not really true.
//                    }
//                }
//            }
//            return RemoteFile(fsId, resolvedPathParts.joinToString("/"), isDirectory ?: looksLikeDirectory)
//        }

        fun withExtension(extension: String?): File {
            extension ?: return this
            return if (!name.endsWith(extension)) {
                File(fs, "$fullPath$extension", isDirectory)
            } else this
        }

        override fun toString(): String {
            return "${fs.name}:$fullPath"
        }

        override fun compareTo(other: File): Int {
            return fullPath.compareTo(other.fullPath)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is File) return false

            if (fs != other.fs) return false
            if (isDirectory != other.isDirectory) return false
            if (pathParts != other.pathParts) return false

            return true
        }

        override fun hashCode(): Int {
            var result = fs.hashCode()
            result = 31 * result + (isDirectory?.hashCode() ?: 0)
            result = 31 * result + pathParts.hashCode()
            return result
        }
    }
}
