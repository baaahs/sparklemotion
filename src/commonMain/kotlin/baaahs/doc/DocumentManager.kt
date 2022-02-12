package baaahs.doc

interface DocumentType {
    val title: String
    val channelName: String
    val fileType: FileType
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