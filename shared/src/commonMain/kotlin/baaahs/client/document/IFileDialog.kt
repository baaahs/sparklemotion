package baaahs.client.document

import baaahs.doc.FileType
import baaahs.io.Fs

interface IFileDialog {
    suspend fun open(fileType: FileType, defaultFile: Fs.File? = null): Fs.File?
    suspend fun saveAs(fileType: FileType, defaultFile: Fs.File? = null, defaultFileName: String?): Fs.File?
    suspend fun onSelect(file: Fs.File)
    suspend fun onCancel()
}