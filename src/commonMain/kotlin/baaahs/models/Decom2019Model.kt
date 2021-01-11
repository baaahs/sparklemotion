package baaahs.models

import baaahs.fixtures.PixelArrayDevice
import baaahs.model.ObjLoader
import baaahs.model.ObjModel

class Decom2019Model : ObjModel(ObjLoader.loadResource("decom-2019-panels.obj")) {
    override val name: String = "Decom2019"

    override fun createSurface(name: String, faces: List<Face>, lines: List<Line>): Surface {
        return Surface(name, name, PixelArrayDevice, 16 * 60, faces, lines)
    }
}