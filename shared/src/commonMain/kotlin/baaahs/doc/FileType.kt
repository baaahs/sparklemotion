package baaahs.doc

import baaahs.io.Fs
import baaahs.ui.Icon

data class FileDisplay(
    val file: Fs.File,
    val name: String,
    val icon: Icon?,
    val isHidden: Boolean = false,
    var isSelectable: Boolean = true
)

abstract class FileType {
    open val title: String get() = "File"
    open val titleLower: String get() = title.lowercase()
    open val indefiniteArticle: String get() = "a"
    open val indefiniteTitle: String get() = "$indefiniteArticle $title"
    open val indefiniteTitleLower: String get() = "$indefiniteArticle $titleLower"

    abstract val extension: String?
    open val contentTypeMasks: List<String> get() = emptyList()
    open val matchingExtensions: List<String> get() = listOfNotNull(extension)

    open fun adjustFileDisplay(fileDisplay: FileDisplay): FileDisplay =
        if (fileDisplay.file.isDirectory != true && matchingExtensions.isNotEmpty()) {
            fileDisplay.copy(
                isSelectable = matchingExtensions.any { fileDisplay.file.name.endsWith(it) }
            )
        } else fileDisplay

    object Any : FileType() {
        override val extension: String? get() = null

        override fun adjustFileDisplay(fileDisplay: FileDisplay): FileDisplay =
            fileDisplay
    }

    object Show : FileType() {
        override val title: String get() = "Show"
        override val extension: String get() = ".sparkle"
        override val matchingExtensions: List<String>
            get() = listOf(".sparkle", ".sparkle.json")
        override val contentTypeMasks: List<String>
            get() = listOf("application/json", "x-application/json", "text/plain")
    }

    object Scene : FileType() {
        override val title: String get() = "Scene"
        override val extension: String get() = ".scene"
        override val matchingExtensions: List<String>
            get() = listOf(".scene", ".scene.json")
        override val contentTypeMasks: List<String>
            get() = listOf("application/json", "x-application/json", "text/plain")
    }

    object Image : FileType() {
        override val title: String get() = "Image"
        override val indefiniteArticle: String get() = "an"
        override val extension: String? get() = null
        override val contentTypeMasks: List<String>
            get() = listOf("image/*")
        override val matchingExtensions: List<String>
            get() = listOf(".jpg", ".jpeg", ".gif", ".png")
    }
}