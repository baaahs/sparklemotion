package baaahs.models

import baaahs.geom.Vector3F
import baaahs.getResource
import baaahs.glsl.CylindricalModelSpaceUvTranslator
import baaahs.glsl.UvTranslator
import baaahs.model.ObjModel

class SheepModel : ObjModel<SheepModel.Panel>("baaahs-model.obj") {
    override val name: String = "BAAAHS"
    override val defaultUvTranslator: UvTranslator by lazy {
        CylindricalModelSpaceUvTranslator(
            this
        )
    }
    private val pixelsPerPanel = hashMapOf<String, Int>()

    override fun load() {
        getResource("baaahs-panel-info.txt")
            .split("\n")
            .map { it.split(Regex("\\s+")) }
            .forEach { pixelsPerPanel[it[0]] = it[1].toInt() * 60 }

        super.load()
    }

    override fun createSurface(name: String, faces: List<Face>, lines: List<Line>): Panel {
        val expectedPixelCount = pixelsPerPanel[name]
        if (expectedPixelCount == null) {
            logger.debug { "No pixel count found for $name" }
        }
        return Panel(name, expectedPixelCount, faces, lines)
    }

    class Panel(
        override val name: String,
        override val expectedPixelCount: Int? = null,
        override val faces: List<Face> = listOf(),
        override val lines: List<Line> = listOf()
    ) : Surface {
        override fun allVertices(): Collection<Vector3F> {
            val vertices = hashSetOf<Vector3F>()
            vertices.addAll(lines.flatMap { it.vertices })
            return vertices
        }

        override val description: String = "Panel $name"
        override fun equals(other: Any?): Boolean = other is Panel && name == other.name
        override fun hashCode(): Int = name.hashCode()
    }
}