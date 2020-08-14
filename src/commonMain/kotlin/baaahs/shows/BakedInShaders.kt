package baaahs.shows

import baaahs.getResource

class BakedInShaders {
    companion object {
        private val allBakedInShaders: List<BakedInShader> by lazy {
            getResource("_RESOURCE_FILES_")
                .split("\n")
                .filter { it.startsWith("baaahs/shows/") && it.endsWith(".glsl")}
                .map { fileName ->
                    val shaderSource = getResource(fileName)
                    val nameFromGlsl = Regex("^// (.*)").find(shaderSource)?.groupValues?.get(1)
                    val name = nameFromGlsl ?: fileName
                        .split("/").last()
                        .replace(".glsl", "")
                        .replace("_", " ")
                    BakedInShader(name, shaderSource)
                }
        }

        val all =
            allBakedInShaders.sortedBy { it.name.toLowerCase() }
    }

    class BakedInShader(val name: String, val src: String)
}
