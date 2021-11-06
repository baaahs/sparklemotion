package baaahs

import baaahs.model.Model
import baaahs.model.ObjModel
import baaahs.models.*
import baaahs.models.Decom2019Model
import baaahs.models.HonchoModel
import baaahs.models.SheepModel
import baaahs.models.SuiGenerisModel
import baaahs.plugin.Plugin
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.sound_analysis.SoundAnalysisPlugin
import kotlin.js.JsName

object Pluggables {
    val plugins = listOf<Plugin<*>>(
        BeatLinkPlugin,
        SoundAnalysisPlugin
    )

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