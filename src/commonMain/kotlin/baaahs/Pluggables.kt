package baaahs

import baaahs.model.Model
import baaahs.model.ObjModel
import baaahs.models.Decom2019Model
import baaahs.models.HonchoModel
import baaahs.models.SheepModel
import baaahs.models.SuiGenerisModel
import baaahs.plugin.beatlink.BeatLinkPlugin
import kotlin.js.JsName

object Pluggables {
    val plugins = listOf(BeatLinkPlugin)

    @JsName("defaultModel")
    const val defaultModel = "Honcho"

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