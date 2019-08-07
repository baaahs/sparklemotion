package baaahs.io

interface Fs {
    fun listFiles(path: String): List<String>
    fun loadFile(path: String): String?
    fun createFile(path: String, content: ByteArray, allowOverwrite: Boolean = false)
    fun createFile(path: String, content: String, allowOverwrite: Boolean = false)
}
