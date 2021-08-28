package baaahs

import baaahs.model.Model
import baaahs.model.ObjModel
import baaahs.models.*
import kotlin.js.JsName

object Pluggables {
    @JsName("defaultModel")
    const val defaultModel = "Playa2021"

    @JsName("loadModel")
    fun loadModel(name: String): Model {
        return when (name) {
            "Decom2019" -> Decom2019Model()
            "Honcho" -> HonchoModel()
            "Playa2021" -> Playa2021Model()
            "SuiGeneris" -> SuiGenerisModel()
            "BAAAHS" -> SheepModel()
            else -> throw IllegalArgumentException("unknown model \"$name\"")
        }.apply { (this as? ObjModel)?.load() }
    }
}