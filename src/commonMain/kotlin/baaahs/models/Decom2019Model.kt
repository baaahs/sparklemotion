package baaahs.models

import baaahs.model.Model
import baaahs.model.ObjModel

class Decom2019Model : ObjModel<Model.Surface>("decom-2019-panels.obj") {
    override val name: String = "Decom2019"

    override fun createSurface(name: String, faces: List<Model.Face>, lines: List<Model.Line>): Model.Surface {
        return SheepModel.Panel(name, 16 * 60, faces, lines)
    }
}