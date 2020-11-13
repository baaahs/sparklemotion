package baaahs.models

import baaahs.fixtures.PixelArrayDevice
import baaahs.model.Model
import baaahs.model.ObjModel

class Decom2019Model : ObjModel("decom-2019-panels.obj") {
    override val name: String = "Decom2019"

    override fun createSurface(name: String, faces: List<Model.Face>, lines: List<Model.Line>): Model.Surface {
        return Surface(name, name, PixelArrayDevice, 16 * 60, faces, lines)
    }
}