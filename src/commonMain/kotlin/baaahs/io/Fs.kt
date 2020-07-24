package baaahs.io

interface Fs {
    val rootFile: File get() = File(this, "", true)

    suspend fun listFiles(parent: File): List<File>
    suspend fun loadFile(file: File): String?
    suspend fun saveFile(file: File, content: ByteArray, allowOverwrite: Boolean = false)
    suspend fun saveFile(file: File, content: String, allowOverwrite: Boolean = false)

    fun resolve(vararg pathParts: String): File {
        return File(this, pathParts.joinToString("/"))
    }

    fun exists(file: File): Boolean

    suspend fun isDirectory(file: File): Boolean {
        return file.isDirectory ?: error("Is $this a directory? Answer unclear.")
    }

    class File(
        val fs: Fs,
        fullPath: String,
        /** If this is known to be a directory or not then `true` or `false`, otherwise `null` */
        internal val isDirectory: Boolean? = null
    ): Comparable<File> {
        val pathParts = fullPath.split("/").filter { it.isNotEmpty() && it != "." && it != ".." }
        val fullPath: String get() = pathParts.joinToString("/")
        val parentPath: String get() = pathParts.subList(0, pathParts.size - 1).joinToString("/")
        val name: String get() = pathParts.last()
        val isRoot: Boolean get() = pathParts.isEmpty()
        val parent: File? get() = if (isRoot) null else File(fs, parentPath, true)

        fun isWithin(parent: File): Boolean {
            return if (parent.isRoot) {
                true
            } else {
                parentPath.startsWith("${parent.fullPath}/")
            }
        }

        fun resolve(relPath: String, isDirectory: Boolean? = null): File {
            return if (isRoot) {
                File(fs, relPath, isDirectory)
            } else {
                File(fs, "${fullPath}/$relPath", isDirectory)
            }
        }

        override fun toString(): String {
            return "$fs:$fullPath"
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
