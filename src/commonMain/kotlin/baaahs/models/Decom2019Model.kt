package baaahs.models

import baaahs.fixtures.PixelArrayDevice
import baaahs.model.ObjModel

class Decom2019Model : ObjModel("decom-2019-panels.obj") {
    override val name: String = "Decom2019"

    override fun createSurface(name: String, faces: List<Face>, lines: List<Line>) =
        Surface(name, name, PixelArrayDevice, 16 * 60, faces, lines)
}