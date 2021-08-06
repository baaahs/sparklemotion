package baaahs

import baaahs.model.Model
import baaahs.model.ObjModel
import baaahs.models.Decom2019Model
import baaahs.models.HonchoModel
import baaahs.models.SheepModel
import baaahs.models.SuiGenerisModel
import kotlin.js.JsName

object Pluggables {
    @JsName("defaultModel")
    const val defaultModel = "BAAAHS"

    @JsName("loadModel")
    fun loadModel(name: String): Model {
        return when (name) {
            "Decom2019" -> Decom2019Model()
            "Honcho" -> HonchoModel()
            "SuiGeneris" -> SuiGenerisModel()
            "BAAAHS" -> SheepModel()
            else -> throw IllegalArgumentException("unknown model \"$name\"")
        }.apply { (this as? ObjModel)?.load() }
    }
}