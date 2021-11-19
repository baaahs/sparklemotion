package baaahs

import baaahs.model.Model
import baaahs.models.*
import baaahs.plugin.Plugin
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.sound_analysis.SoundAnalysisPlugin

object Pluggables {
    val plugins = listOf<Plugin<*>>(
        BeatLinkPlugin,
        SoundAnalysisPlugin
    )

    const val defaultModel = "Playa2021"

    private val models = mutableMapOf<String, Model>()

    fun loadModel(name: String): ModelProvider = ModelProvider {
        models.getOrPut(name) {
            when (name) {
                "Decom2019" -> Decom2019Model()
                "Honcho" -> HonchoModel()
                "Playa2021" -> Playa2021Model()
                "SuiGeneris" -> SuiGenerisModel()
                "BAAAHS" -> SheepModel()
                else -> throw IllegalArgumentException("unknown model \"$name\"")
            }
        }
    }
}

fun interface ModelProvider {
    fun getModel(): Model
}
