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
        val description: String? = null,
        val lastModifiedMs: Time? = null,
        val tags: List<String> = emptyList(),
        val srcFile: String,
        val errors: List<String>? = null
    )
}

