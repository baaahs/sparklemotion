package baaahs.io

interface Fs {
    fun createFile(name: String, content: ByteArray, allowOverwrite: Boolean = false)
    fun createFile(name: String, content: String, allowOverwrite: Boolean = false)
    fun loadFile(name: String): String?
}
