package baaahs.io

interface Fs {
    val rootFile: File get() = File(this, "", true)

    fun listFiles(parent: File): List<File>
    fun loadFile(file: File): String?
    fun saveFile(file: File, content: ByteArray, allowOverwrite: Boolean = false)
    fun saveFile(file: File, content: String, allowOverwrite: Boolean = false)

    fun resolve(vararg pathParts: String): File {
        return File(this, pathParts.joinToString("/"))
    }

    fun isDirectory(file: File): Boolean {
        return file.isDirectory ?: error("Is $this a directory? Answer unclear.")
    }

    class File(
        val fs: Fs,
        fullPath: String,
        /** If this is known to be a directory or not then `true` or `false`, otherwise `null` */
        internal val isDirectory: Boolean? = null
    ) {
        val pathParts = fullPath.split("/").filter { it.isNotEmpty() }
        val fullPath: String get() = pathParts.joinToString("/")
        val parentPath: String get() = pathParts.subList(0, pathParts.size - 1).joinToString("/")
        val name: String get() = pathParts.last()
        val isRoot: Boolean get() = pathParts.isEmpty()
        val parent: File? get() = if (isRoot) null else File(fs, parentPath, true)

        override fun toString(): String {
            return "$fs:$fullPath"
        }
    }
}
