package baaahs.models

import baaahs.model.Model
import baaahs.model.ObjModelLoader

class SuiGenerisModel : Model() {
    override val name: String = "SuiGeneris"

    private val objModel = ObjModelLoader.load("sui-generis.obj") { 10 * 60 }

    override val allEntities: List<Entity>
        get() = objModel.allEntities
}