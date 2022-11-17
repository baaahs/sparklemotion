package baaahs.libraries

import baaahs.io.Fs
import baaahs.show.Shader
import baaahs.util.Time
import kotlinx.serialization.Serializable

@Serializable
data class ShaderLibrary(
    val libDir: Fs.File,
    val title: String,
    val description: String? = null,
    val license: String? = null,
    val entries: List<Entry> = emptyList()
) {
    @Serializable
    data class Entry(
        val id: String,
        val shader: Shader
    ) {
        fun matches(term: String): Boolean {
            val lcTerm = term.lowercase()
            return shader.title.lowercase().contains(lcTerm) ||
                    shader.description?.lowercase()?.contains(lcTerm) ?: false ||
                    shader.tags.any {
                        it.lowercase().contains(lcTerm)
                    }
        }
    }
}

/** On-disk representation of [ShaderLibrary]. */
@Serializable
data class ShaderLibraryIndexFile(
    val title: String,
    val description: String? = null,
    val license: String? = null,
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
        val errors: List<String>? = emptyList()
    )
}
