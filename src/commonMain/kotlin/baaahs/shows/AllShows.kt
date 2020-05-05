package baaahs.shows

import baaahs.getResource
import baaahs.shaders.GlslShader

class AllShows {
    companion object {
        val allGlslShows: List<GlslShow> by lazy {
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
                    GlslShow(name, shaderSource, glslContext = GlslShader.globalRenderContext)
                }
        }

        private val nonGlslShows = listOf(PanelTweenShow)

        val allShows = listOf(FallbackShow) + (nonGlslShows + allGlslShows).sortedBy { it.name.toLowerCase() }
    }
}
