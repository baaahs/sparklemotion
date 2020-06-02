package baaahs.models

import baaahs.glsl.LinearModelSpaceUvTranslator
import baaahs.glsl.UvTranslator
import baaahs.model.Model
import baaahs.model.ObjModel

class SuiGenerisModel : ObjModel<Model.Surface>("sui-generis.obj") {
    override val name: String = "Decom2019" // todo: it's weird that we're reusing Decom2019 here.
    override val defaultUvTranslator: UvTranslator by lazy {
        LinearModelSpaceUvTranslator(
            this
        )
    }

    override fun createSurface(name: String, faces: List<Face>, lines: List<Line>): Surface {
        return SheepModel.Panel(name, 10 * 60, faces, lines)
    }
}