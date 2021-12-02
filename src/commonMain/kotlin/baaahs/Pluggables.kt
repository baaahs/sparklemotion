package baaahs

import baaahs.model.ConstEntityMetadataProvider
import baaahs.model.EntityMetadataProvider
import baaahs.model.Model
import baaahs.model.ModelData
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
                val (modelData: ModelData, metadata: EntityMetadataProvider) =
                    when (name) {
                        "Decom2019" -> decom2019ModelData to ConstEntityMetadataProvider(16 * 60)
                        "Honcho" -> honchoModelData to ConstEntityMetadataProvider(16 * 60)
                        "Playa2021" -> playa2021ModelData to ConstEntityMetadataProvider(16 * 60)
                        "SuiGeneris" -> suiGenerisModelData to ConstEntityMetadataProvider(10 * 60)
                        "BAAAHS" -> sheepModelData to sheepModelMetadata
                        else -> throw IllegalArgumentException("unknown model \"$name\"")
                    }
                modelData.open(metadata)
            }
    }
}

fun interface ModelProvider {
    suspend fun getModel(): Model
}
