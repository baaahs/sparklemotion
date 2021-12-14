package baaahs.doc

import baaahs.io.Fs
import baaahs.ui.Icon

class FileDisplay(
    var name: String,
    var icon: Icon?,
    var isHidden: Boolean = false,
    var isSelectable: Boolean = true
)

abstract class FileType {
    open val title: String get() = "File"
    abstract val extension: String?

    abstract fun adjustFileDisplay(file: Fs.File, fileDisplay: FileDisplay)

    object Any : FileType() {
        override val extension: String? get() = null

        override fun adjustFileDisplay(file: Fs.File, fileDisplay: FileDisplay) {
            // No op.
        }
    }

    object Show : FileType() {
        override val title: String get() = "Show"
        override val extension: String get() = ".sparkle"

        override fun adjustFileDisplay(file: Fs.File, fileDisplay: FileDisplay) {
            if (file.isDirectory == false) {
                fileDisplay.isSelectable = file.name.endsWith(".sparkle")
            }
        }
    }

    object Scene : FileType() {
        override val title: String get() = "Scene"
        override val extension: String get() = ".scene"

        override fun adjustFileDisplay(file: Fs.File, fileDisplay: FileDisplay) {
            if (file.isDirectory == false) {
                fileDisplay.isSelectable = file.name.endsWith(".scene")
            }
        }
    }
}