package baaahs.io

interface Fs {
    fun createFile(name: String, content: ByteArray)
    fun createFile(name: String, content: String)
}
