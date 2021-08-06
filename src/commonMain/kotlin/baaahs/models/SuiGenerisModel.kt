package baaahs.models

import baaahs.device.PixelArrayDevice
import baaahs.model.Model
import baaahs.model.ObjModel

class SuiGenerisModel : ObjModel("sui-generis.obj") {
    override val name: String = "Decom2019" // todo: it's weird that we're reusing Decom2019 here.

    override fun createSurface(name: String, faces: List<Face>, lines: List<Line>): Surface {
        return Model.Surface(name, name, PixelArrayDevice, 10 * 60, faces, lines)
    }
}