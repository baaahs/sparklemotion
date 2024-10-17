package baaahs.client.document

import baaahs.doc.FileType
import baaahs.io.Fs

interface IFileDialog {
    suspend fun open(fileType: FileType, defaultTarget: Fs.File? = null, title: String? = null): Fs.File?
    suspend fun saveAs(fileType: FileType, defaultTarget: Fs.File? = null, title: String? = null): Fs.File?
    suspend fun onSelect(file: Fs.File)
    suspend fun onCancel()
}