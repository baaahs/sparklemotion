package baaahs.libraries

import baaahs.show.Shader
import baaahs.util.Time
import kotlinx.serialization.Serializable

@Serializable
data class ShaderLibrary(
    val title: String,
    val description: String? = null,
    val license: String? = null,
    val entries: List<Entry> = emptyList()
) {
    @Serializable
    data class Entry(
        val id: String,
        val shader: Shader,
        val description: String? = null,
        val lastModifiedMs: Time? = null,
        val tags: List<String> = emptyList()
    ) {
        fun matches(term: String): Boolean {
            val lcTerm = term.lowercase()
            return shader.title.lowercase().contains(lcTerm) ||
                    description?.lowercase()?.contains(lcTerm) ?: false ||
                    tags.any { it.lowercase().contains(lcTerm) }
        }
    }
}