package baaahs.shows

import baaahs.getResource
import baaahs.glsl.GlslBase
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
                    object : GlslShow(name) {
                        override val program = GlslShader.renderContext.createProgram(shaderSource)
                    }
                }
        }

        private val nonGlslShows = listOf(
            SomeDumbShow,
//            RandomShow,
            CompositeShow,
//            ThumpShow,
            PanelTweenShow
//            PixelTweenShow
//            LifeyShow,
//            SimpleSpatialShow,
//            HeartbleatShow,
//            CreepingPixelsShow
        )

        val allShows = listOf(
            SolidColorShow
        ) + (nonGlslShows + allGlslShows).sortedBy { it.name.toLowerCase() }
    }
}
