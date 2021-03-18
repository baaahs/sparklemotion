package baaahs.libraries

import baaahs.show.Shader
import baaahs.util.Time
import kotlinx.serialization.Serializable

@Serializable
data class ShaderLibrary(
    val title: String,
    val description: String?,
    val license: String?,
    val entries: List<Entry> = emptyList()
) {
    @Serializable
    data class Entry(
        val id: String,
        val shader: Shader,
        val description: String?,
        val lastModifiedMs: Time,
        val tags: List<String>
    ) {
        fun matches(term: String): Boolean {
            return shader.title.contains(term) ||
                    description?.contains(term) ?: false ||
                    tags.any { it.contains(term) }
        }
    }
}

/** On-disk representation of [ShaderLibrary]. */
@Serializable
data class ShaderLibraryIndexFile(
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
