package baaahs.doc

import baaahs.rpc.RpcService
import baaahs.sm.webapi.DocumentCommands
import baaahs.sm.webapi.getImpl
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule

interface DocumentType {
    val title: String
    val channelName: String
    val fileType: FileType

    fun <T> getRpcService(
        tSerializer: KSerializer<T>,
        serializersModule: SerializersModule
    ): RpcService<DocumentCommands<T>> =
        DocumentCommands.getImpl("pinky/$channelName", tSerializer, serializersModule)
}

object SceneDocumentType : DocumentType {
    override val title: String get() = "Scene"
    override val channelName: String get() = "scene"
    override val fileType: FileType get() = FileType.Scene
}

object ShowDocumentType : DocumentType {
    override val title: String get() = "Show"
    override val channelName: String get() = "show"
    override val fileType: FileType get() = FileType.Show
}