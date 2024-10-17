package baaahs.libraries

import baaahs.util.Time
import kotlinx.serialization.Serializable

/** On-disk representation of [ShaderLibrary]. */
@Serializable
data class ShaderLibraryIndex(
    val title: String,
    val description: String?,
    val license: String?,
    val entries: List<Entry> = emptyList()
) {
    @Serializable
    data class Entry(
        val id: String,
        val title: String,
        val description: String?,
        val lastModifiedMs: Time,
        val tags: List<String>,
        val srcFile: String
    )
}