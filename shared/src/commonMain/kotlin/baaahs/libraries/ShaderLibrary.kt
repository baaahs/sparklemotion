package baaahs.libraries

import baaahs.io.Fs
import baaahs.show.Shader
import baaahs.show.Tag
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
        val shader: Shader,
        val errors: List<String> = emptyList()
    ) {
        val tags: List<Tag>
            get() = shader.tags

        fun matches(term: String): Boolean {
            val negate = term.startsWith('-')
            val lcTerm = term.trimStart('-').lowercase()
            val matches = shader.title.lowercase().contains(lcTerm) ||
                    shader.description?.lowercase()?.contains(lcTerm) == true ||
                    shader.tags.any {
                        it.fullString.lowercase().contains(lcTerm)
                    }
            return if (negate) !matches else matches
        }
    }
}