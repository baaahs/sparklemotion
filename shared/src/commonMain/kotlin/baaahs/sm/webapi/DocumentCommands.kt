package baaahs.sm.webapi

import baaahs.io.Fs
import baaahs.rpc.Service

@Service
interface DocumentCommands<T> {
    suspend fun new(template: T? = null)
    suspend fun switchTo(file: Fs.File?)
    suspend fun save()
    suspend fun saveAs(file: Fs.File)

    companion object
}